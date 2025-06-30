package org.example.granturismo.mappers;

import org.example.granturismo.dtos.ArtesaniaBlockchainDTO;
import org.example.granturismo.modelo.ServicioArtesania;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre ServicioArtesania y ArtesaniaBlockchainDTO
 */
@Mapper(componentModel = "spring")
public interface ArtesaniaBlockchainMapper {

    /**
     * Convierte una entidad ServicioArtesania a ArtesaniaBlockchainDTO
     * @param servicioArtesania la entidad a convertir
     * @return el DTO para blockchain
     */
    @Mapping(source = "idArtesania", target = "idArtesania")
    @Mapping(source = "tipoArtesania", target = "tipoArtesania")
    @Mapping(source = "artesano", target = "artesano")
    @Mapping(source = "origenCultural", target = "origenCultural")
    @Mapping(source = "artesania", target = "artesania")
    @Mapping(source = "nivelDificultad", target = "nivelDificultad")
    @Mapping(source = "duracionTaller", target = "duracionTaller")
    @Mapping(source = "incluyeMaterial", target = "incluyeMaterial")
    @Mapping(source = "maxParticipantes", target = "maxParticipantes")
    @Mapping(source = "visitaTaller", target = "visitaTaller")
    ArtesaniaBlockchainDTO toBlockchainDTO(ServicioArtesania servicioArtesania);
}