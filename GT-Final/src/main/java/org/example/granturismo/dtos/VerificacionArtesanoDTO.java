package org.example.granturismo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.granturismo.modelo.ServicioArtesania;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificacionArtesanoDTO {

    // Información del artesano
    private String artesano;
    private String tipoArtesania;
    private String origenCultural;
    private String descripcionArtesania;

    // Detalles del taller
    private ServicioArtesania.Nivel nivelDificultad;
    private Integer duracionTaller;
    private Boolean incluyeMaterial;
    private Integer maxParticipantes;
    private Boolean visitaTaller;

    // Información de verificación blockchain
    private String hashBlockchain;
    private String urlBlockchainExplorer;
    private LocalDateTime fechaRegistroBlockchain;
    private Boolean verificadoEnBlockchain;

    // Información adicional de autenticidad
    private String codigoVerificacion; // ID único para verificación
    private String estadoVerificacion; // AUTENTICO, PENDIENTE, NO_VERIFICADO
    private String mensajeVerificacion;

    // URLs útiles
    private String urlVerificacionCompleta; // URL a página web de verificación
    private String urlQrOriginal; // URL de la imagen QR

    // Metadatos
    private LocalDateTime fechaGeneracionQr;
    private String versionSistema;
    private Boolean esArtesanoVerificado;
}