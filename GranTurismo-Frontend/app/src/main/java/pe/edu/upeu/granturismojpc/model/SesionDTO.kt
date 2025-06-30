package pe.edu.upeu.granturismojpc.model

import java.time.LocalDateTime

data class SesionDTO(
    val sesionId: Long,
    val fecha: LocalDateTime?,      // o LocalDateTime si usas Moshi con adaptador
    val preview: String
)
