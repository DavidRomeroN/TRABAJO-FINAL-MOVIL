package org.example.granturismo.dtos;

import org.example.granturismo.modelo.TipoTransaccion;

import java.time.LocalDateTime;

public record PuntoDTO(
        Long id,
        Long uidUsuario,
        Integer cantidadPuntos,
        TipoTransaccion tipoTransaccion,
        LocalDateTime fechaTransaccion,
        String descripcion
) {}