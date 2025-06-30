package org.example.granturismo.servicio.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.excepciones.QrGenerationException;
import org.example.granturismo.servicio.IQrCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del servicio de generación de códigos QR con Cloudinary
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements IQrCodeService {

    private final Cloudinary cloudinary;

    @Value("${qr.image-width:300}")
    private int defaultWidth;

    @Value("${qr.image-height:300}")
    private int defaultHeight;

    @Value("${qr.image-format:PNG}")
    private String imageFormat;

    @Value("${cloudinary.folder:qr-codes}")
    private String cloudinaryFolder;

    // Cache para almacenar relación filename -> public_id de Cloudinary
    private final Map<String, String> filenameToPublicIdCache = new ConcurrentHashMap<>();

    @Override
    public String generarQrCodeUrl(String dataToEncode, int width, int height) throws QrGenerationException {
        try {
            log.info("Generando código QR para datos: {}", dataToEncode.substring(0, Math.min(50, dataToEncode.length())));

            // Generar código QR como bytes
            byte[] qrCodeBytes = generarQrCodeBytes(dataToEncode, width, height);

            // Generar nombre único para el archivo
            String filename = generarNombreArchivo();

            // Subir a Cloudinary
            Map<String, Object> uploadResult = subirACloudinary(qrCodeBytes, filename);

            // Extraer URLs del resultado
            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            // Guardar en cache para futuras eliminaciones
            filenameToPublicIdCache.put(filename, publicId);

            log.info("Código QR subido exitosamente a Cloudinary: {} -> {}", filename, secureUrl);

            return secureUrl;

        } catch (Exception e) {
            log.error("Error al generar y subir código QR a Cloudinary", e);
            throw new QrGenerationException("Error al generar código QR: " + e.getMessage(), e);
        }
    }

    @Override
    public String generarQrCodeUrl(String dataToEncode) throws QrGenerationException {
        return generarQrCodeUrl(dataToEncode, defaultWidth, defaultHeight);
    }

    @Override
    public byte[] generarQrCodeBytes(String dataToEncode, int width, int height) throws QrGenerationException {
        try {
            log.debug("Generando código QR como bytes");

            BitMatrix bitMatrix = generarBitMatrix(dataToEncode, width, height);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, outputStream);
                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            log.error("Error al generar código QR como bytes", e);
            throw new QrGenerationException("Error al generar código QR como bytes: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminarQrCode(String filename) {
        try {
            // Buscar public_id en cache
            String publicId = filenameToPublicIdCache.get(filename);

            if (publicId == null) {
                // Si no está en cache, intentar construir el public_id
                publicId = cloudinaryFolder + "/" + filename.replaceFirst("\\.[^.]+$", "");
                log.warn("Public ID no encontrado en cache para {}, intentando con: {}", filename, publicId);
            }

            // Eliminar de Cloudinary
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            // Verificar resultado
            String resultStatus = (String) result.get("result");
            boolean deleted = "ok".equals(resultStatus);

            if (deleted) {
                log.info("Código QR eliminado de Cloudinary: {} (publicId: {})", filename, publicId);
                filenameToPublicIdCache.remove(filename);
            } else {
                log.warn("No se pudo eliminar el código QR de Cloudinary: {} - Status: {}", filename, resultStatus);
            }

            return deleted;

        } catch (Exception e) {
            log.error("Error al eliminar código QR de Cloudinary: {}", filename, e);
            return false;
        }
    }

    /**
     * Sube los bytes del QR a Cloudinary
     */
    private Map<String, Object> subirACloudinary(byte[] qrCodeBytes, String filename) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeBytes)) {

            // Configurar opciones de subida
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", cloudinaryFolder,                    // Carpeta en Cloudinary
                    "public_id", filename.replaceFirst("\\.[^.]+$", ""), // Nombre sin extensión
                    "resource_type", "image",                      // Tipo de recurso
                    "format", imageFormat.toLowerCase(),           // Formato de imagen
                    "overwrite", true,                            // Sobrescribir si existe
                    "quality", "auto:good",                       // Calidad automática
                    "tags", "qr-code,artesania,verification"      // Tags para organización
            );

            // Subir a Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(qrCodeBytes, uploadOptions);

            log.debug("Resultado de subida a Cloudinary: {}", uploadResult);

            return uploadResult;
        }
    }

    /**
     * Genera la matriz de bits para el código QR
     */
    private BitMatrix generarBitMatrix(String dataToEncode, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(dataToEncode, BarcodeFormat.QR_CODE, width, height);
    }

    /**
     * Genera un nombre único para el archivo QR
     */
    private String generarNombreArchivo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("qr_%s_%s.%s", timestamp, uuid, imageFormat.toLowerCase());
    }

    /**
     * Método adicional para limpiar cache (útil para mantenimiento)
     */
    public void limpiarCache() {
        filenameToPublicIdCache.clear();
        log.info("Cache de public_ids de Cloudinary limpiado");
    }

    /**
     * Método para obtener estadísticas del cache
     */
    public Map<String, Object> obtenerEstadisticasCache() {
        return Map.of(
                "tamaño_cache", filenameToPublicIdCache.size(),
                "archivos_en_cache", filenameToPublicIdCache.keySet()
        );
    }
}