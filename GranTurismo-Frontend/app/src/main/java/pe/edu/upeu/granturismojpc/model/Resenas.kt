package pe.edu.upeu.granturismojpc.model

data class ResenaDto(
    var idResena: Long,
    var calificacion: Int,
    var comentario: String?,
    var fecha: String,
    var usuario: Long,
    var paquete: Long,
)

data class ResenaResp(
    val idResena: Long,
    val calificacion: Int,
    val comentario: String?,
    val fecha: String,
    val usuario: UsuarioResp,
    val paquete: PaqueteRespBuscar
)

data class ResenaCreateDto(
    val calificacion: Int,
    val comentario: String?,
    val fecha: String,
    val usuario: Long,
    val paquete: Long,
)

fun ResenaResp.toDto(): ResenaDto {
    return ResenaDto(
        idResena = this.idResena,
        usuario = this.usuario.idUsuario,
        paquete = this.paquete.idPaquete,
        calificacion = this.calificacion,
        comentario = this.comentario,
        fecha = this.fecha
    )
}

fun ResenaDto.toCreateDto(): ResenaCreateDto {
    return ResenaCreateDto(
        usuario = this.usuario,
        paquete = this.paquete,
        calificacion = this.calificacion,
        comentario = this.comentario,
        fecha = this.fecha
    )
}