package org.example.granturismo.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.servicio.impl.QrCodeServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestión de códigos QR
 * NOTA: Con Cloudinary, los QRs se sirven directamente desde su CDN
 */
@Slf4j
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "Códigos QR", description = "API para gestión de códigos QR almacenados en Cloudinary")
public class QrCodeController {

    private final QrCodeServiceImpl qrCodeService;

    /**
     * Generar QR directamente (para testing)
     */
    @PostMapping("/generar")
    @Operation(summary = "Generar código QR",
            description = "Genera un código QR y lo sube a Cloudinary")
    public ResponseEntity<Map<String, String>> generarQr(
            @Parameter(description = "Texto a codificar en el QR")
            @RequestParam String texto,
            @Parameter(description = "Ancho de la imagen (opcional)")
            @RequestParam(defaultValue = "300") int ancho,
            @Parameter(description = "Alto de la imagen (opcional)")
            @RequestParam(defaultValue = "300") int alto) {

        try {
            log.info("Generando QR para texto: {}", texto.substring(0, Math.min(50, texto.length())));

            String qrUrl = qrCodeService.generarQrCodeUrl(texto, ancho, alto);

            Map<String, String> response = new HashMap<>();
            response.put("texto", texto);
            response.put("url_cloudinary", qrUrl);
            response.put("mensaje", "QR generado exitosamente en Cloudinary");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al generar QR", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar QR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtener QR como bytes (para descargas directas)
     */
    @GetMapping("/download")
    @Operation(summary = "Descargar código QR como imagen",
            description = "Genera y devuelve un código QR como bytes para descarga directa")
    public ResponseEntity<byte[]> descargarQr(
            @Parameter(description = "Texto a codificar")
            @RequestParam String texto,
            @RequestParam(defaultValue = "300") int ancho,
            @RequestParam(defaultValue = "300") int alto) {

        try {
            log.info("Generando QR para descarga: {}", texto.substring(0, Math.min(30, texto.length())));

            byte[] qrBytes = qrCodeService.generarQrCodeBytes(texto, ancho, alto);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "qr-code.png");
            headers.setContentLength(qrBytes.length);

            return new ResponseEntity<>(qrBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error al generar QR para descarga", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Eliminar QR de Cloudinary
     */
    @DeleteMapping("/eliminar/{filename}")
    @Operation(summary = "Eliminar código QR de Cloudinary")
    public ResponseEntity<Map<String, Object>> eliminarQr(
            @Parameter(description = "Nombre del archivo QR a eliminar")
            @PathVariable String filename) {

        try {
            boolean eliminado = qrCodeService.eliminarQrCode(filename);

            Map<String, Object> response = new HashMap<>();
            response.put("filename", filename);
            response.put("eliminado", eliminado);
            response.put("mensaje", eliminado ? "QR eliminado exitosamente" : "QR no encontrado");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al eliminar QR: {}", filename, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al eliminar QR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtener estadísticas del cache de QRs
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas del servicio QR")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        try {
            Map<String, Object> estadisticas = qrCodeService.obtenerEstadisticasCache();
            estadisticas.put("servicio", "Cloudinary QR Service");
            estadisticas.put("timestamp", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(estadisticas);

        } catch (Exception e) {
            log.error("Error al obtener estadísticas", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Limpiar cache de QRs
     */
    @PostMapping("/limpiar-cache")
    @Operation(summary = "Limpiar cache de QRs")
    public ResponseEntity<Map<String, String>> limpiarCache() {
        try {
            qrCodeService.limpiarCache();

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cache limpiado exitosamente");
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al limpiar cache", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al limpiar cache: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint de información del servicio
     */
    @GetMapping("/info")
    @Operation(summary = "Información del servicio QR")
    public ResponseEntity<Map<String, Object>> informacionServicio() {
        Map<String, Object> info = new HashMap<>();
        info.put("servicio", "QR Code Service con Cloudinary");
        info.put("version", "2.0.0");
        info.put("almacenamiento", "Cloudinary CDN");
        info.put("formatos_soportados", new String[]{"PNG", "JPG", "JPEG"});
        info.put("tamaño_defecto", "300x300");
        info.put("carpeta_cloudinary", "qr-codes");
        info.put("cache_activo", true);

        return ResponseEntity.ok(info);
    }
}