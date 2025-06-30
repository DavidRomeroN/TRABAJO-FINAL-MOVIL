package org.example.granturismo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.granturismo.modelo.ServicioArtesania;

/**
 * DTO para representar los datos de una artesanía que se enviarán a la blockchain
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtesaniaBlockchainDTO {

    private Long idArtesania;
    private String tipoArtesania;
    private String artesano;
    private String origenCultural;
    private String artesania; // descripción completa
    private ServicioArtesania.Nivel nivelDificultad;
    private Integer duracionTaller;
    private Boolean incluyeMaterial;
    private Integer maxParticipantes;
    private Boolean visitaTaller;
}