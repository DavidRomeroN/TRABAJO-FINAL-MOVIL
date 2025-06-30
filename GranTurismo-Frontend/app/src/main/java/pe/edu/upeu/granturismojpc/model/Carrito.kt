package pe.edu.upeu.granturismojpc.model

data class CarritoDto(
    var idCarrito: Long,
    var usuario: Long?,
    var fechaCreacion: String,
    var estado: String, //pendiente, confirmado, cancelado, etc.
)
data class CarritoResp(
    val idCarrito: Long,
    val usuario: UsuarioResp?,
    val fechaCreacion: List<Int>,
    val estado: String,
){
    @Transient
    val fechaCreacionStr: String = formatFecha(fechaCreacion)

    companion object {
        fun formatFecha(fecha: List<Int>): String {
            return if (fecha.size >= 5) {
                "%04d-%02d-%02d %02d:%02d:00".format(
                    fecha[0], fecha[1], fecha[2], fecha[3], fecha[4]
                )
            } else {
                "Fecha inválida"
            }
        }
    }
}
data class CarritoCreateDto(
    var usuario: Long?,
    var fechaCreacion: String,
    var estado: String,
)

fun CarritoResp.toDto(): CarritoDto{
    return CarritoDto(
        idCarrito =this.idCarrito,
        usuario =this.usuario?.idUsuario,
        fechaCreacion =this.fechaCreacionStr,
        estado =this.estado
    )
}

fun CarritoDto.toCreateDto(): CarritoCreateDto{
    return CarritoCreateDto(
        usuario=this.usuario,
        fechaCreacion=this.fechaCreacion,
        estado=this.estado
    )
}