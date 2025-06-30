package pe.edu.upeu.granturismojpc.model

data class CarritoItemDto(
    var idCarritoItem: Long,
    var carrito: Long,
    var referenciaId: Long,
    var tipo: String,   //"actividad", "servicioAlimentacion", "servicioHoteleria", "servicioArtesania"
    var cantidadPersonas: Int?,
    var fechaReserva: String?,
    var notas: String?
)
data class CarritoItemResp(
    var idCarritoItem: Long,
    var carrito: CarritoResp,
    var referenciaId: Long,
    var tipo: String,   //"actividad", "servicioAlimentacion", "servicioHoteleria", "servicioArtesania"
    var cantidadPersonas: Int?,
    var fechaReserva: List<Int>?,
    var notas: String?
)

data class CarritoItemCreateDto(
    var carrito: Long,
    var referenciaId: Long,
    var tipo: String,   //"actividad", "servicioAlimentacion", "servicioHoteleria", "servicioArtesania"
    var cantidadPersonas: Int?,
    var fechaReserva: String?,
    var notas: String?
)

fun CarritoItemResp.toDto(): CarritoItemDto{
    return CarritoItemDto(
        idCarritoItem=this.idCarritoItem,
        carrito=this.carrito.idCarrito,
        referenciaId=this.referenciaId,
        tipo=this.tipo,
        cantidadPersonas=this.cantidadPersonas,
        fechaReserva=formatFecha(this.fechaReserva),
        notas=this.notas
    )
}

fun CarritoItemDto.toCreateDto(): CarritoItemCreateDto{
    return CarritoItemCreateDto(
        carrito=this.carrito,
        referenciaId=this.referenciaId,
        tipo=this.tipo,
        cantidadPersonas=this.cantidadPersonas,
        fechaReserva=this.fechaReserva,
        notas=this.notas
    )
}

private fun formatFecha(fecha: List<Int>?): String {
    val fechaD=fecha.orEmpty()
    return if (fechaD.size >= 5) {
        "%04d-%02d-%02d %02d:%02d:%02d".format(
            fechaD[0], fechaD[1], fechaD[2], fechaD[3], fechaD[4], fechaD[5]
        )
    } else {
        "Fecha inválida"
    }
}