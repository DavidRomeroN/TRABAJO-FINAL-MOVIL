package org.example.granturismo.configuracion;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j; // Make sure you have Lombok dependency
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Cloudinary para almacenamiento de imágenes
 */
@Slf4j
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:ddkdsx3fg}")
    private String cloudName;

    @Value("${cloudinary.api-key:296669784924797}")
    private String apiKey;

    @Value("${cloudinary.api-secret:mydbfROMEnigdlzje8IrsCtMy3Y}")
    private String apiSecret;

    @Value("${cloudinary.secure:true}")
    private boolean secure;

    @Bean
    public Cloudinary cloudinary() {
        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", secure
            ));

            // Verificar configuración
            cloudinary.api().ping(ObjectUtils.emptyMap()); // Corrected ping method
            log.info("✅ Cloudinary configurado exitosamente - Cloud: {}", cloudName);

            return cloudinary;

        } catch (Exception e) {
            log.error("❌ Error al configurar Cloudinary", e);
            throw new RuntimeException("No se pudo configurar Cloudinary", e);
        }
    }

    /**
     * Bean para proporcionar información de configuración
     */
    @Bean
    public CloudinaryInfo cloudinaryInfo() {
        return CloudinaryInfo.builder()
                .cloudName(cloudName)
                .baseUrl("https://res.cloudinary.com/" + cloudName)
                .uploadUrl("https://api.cloudinary.com/v1_1/" + cloudName + "/upload")
                .secure(secure)
                .build();
    }

    /**
     * Clase para información de configuración
     */
    @lombok.Data
    @lombok.Builder
    public static class CloudinaryInfo {
        private String cloudName;
        private String baseUrl;
        private String uploadUrl;
        private boolean secure;
    }
}