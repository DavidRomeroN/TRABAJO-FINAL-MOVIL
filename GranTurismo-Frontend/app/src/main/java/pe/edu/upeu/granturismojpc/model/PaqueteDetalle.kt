package pe.edu.upeu.granturismojpc.model

data class PaqueteDetalleDto(
    var idPaqueteDetalle: Long,
    var cantidad: Long,
    var precioEspecial: Double,
    var paquete: Long,
    var servicio: Long
)

data class PaqueteDetalleResp(
    val idPaqueteDetalle: Long,
    val cantidad: Long,
    val precioEspecial: Double,
    val paquete: PaqueteRespBuscar,
    val servicio: ServicioResp
)

data class PaqueteDetalleCreateDto(
    var cantidad: Long,
    var precioEspecial: Double,
    var paquete: Long,
    var servicio: Long
)

fun PaqueteDetalleResp.toDto(): PaqueteDetalleDto {
    return PaqueteDetalleDto(
        idPaqueteDetalle = this.idPaqueteDetalle,
        cantidad = this.cantidad,
        precioEspecial = this.precioEspecial,
        paquete = this.paquete.idPaquete,
        servicio = this.servicio.idServicio
    )
}

fun PaqueteDetalleDto.toCreateDto(): PaqueteDetalleCreateDto {
    return PaqueteDetalleCreateDto(
        cantidad = this.cantidad,
        precioEspecial = this.precioEspecial,
        paquete = this.paquete,
        servicio = this.servicio
    )
}