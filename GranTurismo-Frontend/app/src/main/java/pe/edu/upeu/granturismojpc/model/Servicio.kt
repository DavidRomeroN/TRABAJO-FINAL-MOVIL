package pe.edu.upeu.granturismojpc.model

// Modelo principal de Servicio
data class ServicioDto(
    var idServicio: Long,
    var nombreServicio: String,
    var descripcion: String,
    var precioBase: Double,
    var estado: String,
    var tipo: Long,
)

// Modelo para crear un nuevo Servicio
data class ServicioCreateDto(
    val nombreServicio: String,
    val descripcion: String,
    val precioBase: Double,
    val estado: String,
    var tipo: Long,
)

data class ServicioResp(
    val idServicio: Long,
    var nombreServicio: String,
    var descripcion: String,
    var precioBase: Double,
    var estado: String,
    var tipo: TipoServicioResp,
    )

fun ServicioResp.toDto(): ServicioDto {
    return ServicioDto(
        idServicio = this.idServicio,
        tipo = this.tipo.idTipo,
        nombreServicio = this.nombreServicio,
        descripcion = this.descripcion,
        precioBase = this.precioBase,
        estado = this.estado
    )
}

fun ServicioDto.toCreateDto(): ServicioCreateDto {
    return ServicioCreateDto(
        tipo = this.tipo,
        nombreServicio = this.nombreServicio,
        descripcion = this.descripcion,
        precioBase = this.precioBase,
        estado = this.estado
    )
}
