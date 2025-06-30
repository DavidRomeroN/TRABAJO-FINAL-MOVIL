package org.example.granturismo.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para registro interno de transacciones blockchain por artesanía
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "transaccion_blockchain_artesania")
public class TransaccionBlockchainArtesania {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_artesania", nullable = false)
    private Long idArtesania; // FK manual a ServicioArtesania

    @Column(name = "hash_transaccion", nullable = false, length = 66)
    private String hashTransaccion;

    @Column(name = "url_explorer", nullable = false, length = 255)
    private String urlExplorer; // URL para ver la transacción en el explorador

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // PENDIENTE, CONFIRMADO, FALLIDO

    @Column(name = "gas_usado", nullable = true)
    private Long gasUsado;

    @Column(name = "costo_transaccion", nullable = true, length = 50)
    private String costoTransaccion; // En Wei o la unidad correspondiente
}