package pe.edu.upeu.granturismojpc.model

data class ActividadDto (
    var idActividad: Long,
    var titulo: String,
    var descripcion: String?,
    var tipo: String,
    var duracionHoras: Long,
    //var imagenUrl: String?,
    var precioBase: Double
)

data class ActividadResp (
    val idActividad: Long,
    val titulo: String,
    val descripcion: String?,
    val tipo: String,
    val duracionHoras: Long,
    val imagenUrl: String?,
    val precioBase: Double
)

data class ActividadCreateDto (
    val titulo: String,
    val descripcion: String?,
    val tipo: String,
    val duracionHoras: Long,
    //val imagenUrl: String?,
    val precioBase: Double
)

fun ActividadResp.toDto(): ActividadDto {
    return ActividadDto(
        idActividad = this.idActividad,
        titulo = this.titulo,
        descripcion = this.descripcion,
        tipo = this.tipo,
        duracionHoras = this.duracionHoras,
        //imagenUrl = this.imagenUrl,
        precioBase = this.precioBase,
    )
}

fun ActividadDto.toCreateDto(): ActividadCreateDto {
    return ActividadCreateDto(
        titulo = this.titulo,
        descripcion = this.descripcion,
        tipo = this.tipo,
        duracionHoras = this.duracionHoras,
        //imagenUrl = this.imagenUrl,
        precioBase = this.precioBase,
    )
}