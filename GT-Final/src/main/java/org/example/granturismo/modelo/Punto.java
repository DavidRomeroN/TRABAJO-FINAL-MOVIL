package org.example.granturismo.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "puntos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Punto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long uidUsuario;

    @Column(nullable = false)
    private Integer cantidadPuntos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoTransaccion tipoTransaccion;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(nullable = true)
    private Long referenciaId;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}