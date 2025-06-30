package org.example.granturismo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SesionDTO {
    private Long sesionId;
    private LocalDateTime ultimaInteraccion;
    private String preview; // Último mensaje del usuario
}
