package pe.edu.upeu.granturismojpc.model

data class ReservaDto1(
                      var idReserva: Long = 0L,
                      var fechaInicio: String,
                      var fechaFin: String,
                      var estado: String,
                      var cantidadPersonas: Int,
                      var observaciones: String?,
                      var usuario: Long,
                      var paquete: Long,
)


data class ReservaCreateDto(
    var fechaInicio: String,
    var fechaFin: String,
    var estado: String,
    var cantidadPersonas: Int,
    var observaciones: String?,
    var usuario: Long,
    var paquete: Long?,
)


data class ReservaResp(
    val idReserva: Long,
    val cantidadPersonas: Int,
    val fechaInicio: List<Int>,
    val fechaFin: List<Int>,
    val estado: String,
    val observaciones: String?,
    val usuario: UsuarioRespSinToken?,
    val paquete: PaqueteRespBuscar?,
)

fun ReservaResp.toDto(): ReservaDto1 {
    return ReservaDto1(
        idReserva = this.idReserva,
        cantidadPersonas = this.cantidadPersonas,
        fechaInicio = formatFecha(this.fechaInicio),
        fechaFin = formatFecha(this.fechaFin),
        estado = this.estado,
        observaciones = this.observaciones,
        usuario = this.usuario?.idUsuario ?: 0L,
        paquete = this.paquete?.idPaquete ?: 0L
    )
}


fun ReservaDto1.toCreateDto(): ReservaCreateDto {
    return ReservaCreateDto(
        fechaInicio = this.fechaInicio,
        fechaFin = this.fechaFin,
        estado = this.estado,
        cantidadPersonas = this.cantidadPersonas,
        observaciones = this.observaciones,
        usuario = this.usuario,
                paquete = this.paquete
    )
}

private fun formatFecha(fecha: List<Int>): String {
    return if (fecha.size >= 5) {
        "%04d-%02d-%02d %02d:%02d:00".format(
            fecha[0], fecha[1], fecha[2], fecha[3], fecha[4]
        )
    } else {
        "Fecha inválida"
    }
}