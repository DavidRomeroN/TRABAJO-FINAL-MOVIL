package pe.edu.upeu.granturismojpc.model

data class PuntosDisponiblesResponseDTO(
    val puntosDisponibles: Int
)

data class PuntoDTO(
    val id: Long,
    val uidUsuario: Long,
    val cantidadPuntos: Int,
    val tipoTransaccion: String,
    val fechaTransaccion: Any, // ⭐ Cambiado de String a Any para manejar arrays de fecha
    val referenciaId: Long?,
    val descripcion: String?
)

data class CanjePuntosRequestDTO(
    val cantidad: Int,
    val descripcion: String,
    val idServicio: Long? = null // ⭐ Nuevo campo para vincular con servicios
)