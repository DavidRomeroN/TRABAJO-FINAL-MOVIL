package org.example.granturismo.configuracion;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración para el servicio de códigos QR
 */
@Configuration
@Slf4j
public class QrCodeConfig implements WebMvcConfigurer {

    @Value("${qr.storage.local.path:./qr-codes}")
    private String localStoragePath;

    @Value("${qr.image-width:300}")
    private int imageWidth;

    @Value("${qr.image-height:300}")
    private int imageHeight;

    @Value("${qr.image-format:PNG}")
    private String imageFormat;

    /**
     * Inicialización del directorio de almacenamiento
     */
    @PostConstruct
    public void initializeStorage() {
        try {
            Path storagePath = Paths.get(localStoragePath);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                log.info("Directorio de almacenamiento QR creado: {}", localStoragePath);
            } else {
                log.info("Directorio de almacenamiento QR existe: {}", localStoragePath);
            }
        } catch (IOException e) {
            log.error("Error al crear directorio de almacenamiento QR: {}", localStoragePath, e);
            throw new RuntimeException("No se pudo inicializar el almacenamiento de QR", e);
        }
    }

    /**
     * Configurar el manejo de recursos estáticos para servir las imágenes QR
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/qr/**")
                .addResourceLocations("file:" + localStoragePath + "/")
                .setCachePeriod(3600); // Cache por 1 hora

        log.info("Configurado manejo de recursos QR: /api/qr/** -> file:{}/", localStoragePath);
    }

    // Getters para uso en otros componentes si es necesario
    public String getLocalStoragePath() {
        return localStoragePath;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public String getImageFormat() {
        return imageFormat;
    }
}