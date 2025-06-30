package pe.edu.upeu.granturismojpc.model

data class ActividadDetalleDto (
    var idActividadDetalle: Long,
    var titulo: String,
    var descripcion: String,
    //var imagenUrl: String,
    var orden: Int,
    var paquete: Long?,
    var actividad: Long?
)

data class ActividadDetalleResp (
    val idActividadDetalle: Long,
    val titulo: String,
    val descripcion: String,
    val imagenUrl: String,
    val orden: Int,
    val paquete: PaqueteRespBuscar?,
    val actividad: ActividadResp?
)

data class ActividadDetalleCreateDto (
    var titulo: String,
    var descripcion: String,
    //var imagenUrl: String,
    var orden: Int,
    var paquete: Long?,
    var actividad: Long?
)
data class ActividadDetalleCompleto(
    val detalle: ActividadDetalleDto,
    val detalleR: ActividadDetalleResp
)

fun ActividadDetalleResp.toDto(): ActividadDetalleDto {
    return ActividadDetalleDto(
        idActividadDetalle = this.idActividadDetalle,
        titulo = this.titulo,
        descripcion = this.descripcion,
        //imagenUrl = this.imagenUrl,
        orden = this.orden,
        paquete = this.paquete?.idPaquete,
        actividad = this.actividad?.idActividad,
    )
}

fun ActividadDetalleDto.toCreateDto(): ActividadDetalleCreateDto {
    return ActividadDetalleCreateDto(
        titulo = this.titulo,
        descripcion = this.descripcion,
        //imagenUrl = this.imagenUrl,
        orden = this.orden,
        paquete = this.paquete,
        actividad = this.actividad,
    )
}