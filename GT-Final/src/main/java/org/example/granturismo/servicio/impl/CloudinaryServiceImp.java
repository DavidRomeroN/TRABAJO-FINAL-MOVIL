package org.example.granturismo.servicio.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.servicio.ICloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Slf4j
@Service
public class CloudinaryServiceImp implements ICloudinaryService {


    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map uploadFile(MultipartFile file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            log.info("Archivo subido exitosamente a Cloudinary: {}", uploadResult.get("secure_url"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Error al subir archivo a Cloudinary", e);
            throw e;
        }
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            log.warn("Intento de eliminación con publicId nulo o vacío");
            return;
        }

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) result.get("result");

            if ("ok".equals(status)) {
                log.info("Archivo eliminado exitosamente de Cloudinary con publicId: {}", publicId);
            } else {
                log.warn("No se pudo eliminar el archivo de Cloudinary con publicId {}: {}", publicId, status);
            }

        } catch (Exception e) {
            log.error("Error al eliminar archivo de Cloudinary con publicId {}: {}", publicId, e.getMessage());
            throw new IOException("Error al eliminar archivo de Cloudinary", e);
        }
    }

    @Override
    public Map uploadBytes(byte[] bytes, String filename, String folder) throws IOException {
        try {
            // Configurar opciones de subida
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", filename.replaceFirst("\\.[^.]+$", ""), // Remover extensión
                    "resource_type", "auto",
                    "overwrite", true
            );

            // Subir usando bytes directamente
            Map uploadResult = cloudinary.uploader().upload(bytes, uploadOptions);

            log.info("Bytes subidos exitosamente a Cloudinary en folder '{}': {}",
                    folder, uploadResult.get("secure_url"));

            return uploadResult;

        } catch (IOException e) {
            log.error("Error al subir bytes a Cloudinary en folder '{}'", folder, e);
            throw e;
        }
    }

    @Override
    public Map getFileInfo(String publicId) throws IOException {
        try {
            Map result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            log.debug("Información obtenida para publicId {}: {}", publicId, result.get("secure_url"));
            return result;

        } catch (Exception e) {
            log.error("Error al obtener información del archivo con publicId {}: {}", publicId, e.getMessage());
            throw new IOException("Error al obtener información del archivo", e);
        }
    }

    /**
     * Método adicional para listar archivos en una carpeta
     */
    public Map listFilesInFolder(String folder) throws IOException {
        try {
            Map options = ObjectUtils.asMap(
                    "type", "upload",
                    "prefix", folder + "/",
                    "max_results", 100
            );

            Map result = cloudinary.api().resources(options);
            log.info("Listados {} archivos en la carpeta '{}'",
                    ((java.util.List<?>) result.get("resources")).size(), folder);

            return result;

        } catch (Exception e) {
            log.error("Error al listar archivos en la carpeta '{}'", folder, e);
            throw new IOException("Error al listar archivos", e);
        }
    }

    /**
     * Método para obtener estadísticas de uso
     */
    public Map getUsageStats() throws IOException {
        try {
            Map result = cloudinary.api().usage(ObjectUtils.emptyMap());
            log.debug("Estadísticas de uso obtenidas: {} bytes usados", result.get("bytes"));
            return result;

        } catch (Exception e) {
            log.error("Error al obtener estadísticas de uso", e);
            throw new IOException("Error al obtener estadísticas", e);
        }
    }
}
