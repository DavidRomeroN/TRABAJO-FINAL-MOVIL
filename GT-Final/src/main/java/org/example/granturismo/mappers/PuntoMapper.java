package org.example.granturismo.mappers;

import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.modelo.Punto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PuntoMapper {

    PuntoDTO toDTO(Punto punto);

    Punto toEntity(PuntoDTO puntoDTO);

    List<PuntoDTO> toDTOList(List<Punto> puntos);
}