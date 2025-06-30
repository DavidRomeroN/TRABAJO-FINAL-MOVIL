package org.example.granturismo.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensajeUsuario;
    @Column(name = "respuesta_bot", length = 5000) // Puedes poner 5000 o más si es necesario
    private String respuestaBot;
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "sesion_id")
    private SesionChat sesion;
}
