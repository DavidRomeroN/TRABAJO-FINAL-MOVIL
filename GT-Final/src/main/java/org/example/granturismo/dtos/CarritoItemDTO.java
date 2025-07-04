package org.example.granturismo.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarritoItemDTO {
    private Long idCarritoItem;
    private String tipo;
    private Long referenciaId;
    private Integer cantidadPersonas;
    private LocalDateTime fechaReserva ;
    private String notas ;
    private CarritoDTO carrito;


    public record CarritoItemCADTO(
            Long idCarritoItem,
            String tipo,
            Long referenciaId,
            Integer cantidadPersonas,
            LocalDateTime fechaReserva,
            String notas,
            Long carrito

    ){}
}



