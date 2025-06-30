package pe.edu.upeu.granturismojpc.model

data class TipoServicioDto(
    val idTipo: Long,
    val nombre: String,
    val descripcion: String
)

data class TipoServicioCreateDto(
    val nombre: String,
    val descripcion: String
)

data class TipoServicioResp(
    val idTipo: Long,
    val nombre: String,
    val descripcion: String
)

