package pe.edu.upeu.granturismojpc.model

data class ServicioHoteleraDto(
    var idHoteleria: Long,
    var servicio: Long,
    var tipoHabitacion: String,
    var estrellas: Int,
    var incluyeDesayuno: String,
    var maxPersonas: Int
)

data class ServicioHoteleraCreateDto(
    val tipoHabitacion: String,
    val estrellas: Int,
    val incluyeDesayuno: String,
    val maxPersonas: Int,
    val servicio: Long
)

data class ServicioHoteleraResp(
    val idHoteleria: Long,
    var tipoHabitacion: String,
    val estrellas: Int,
    val incluyeDesayuno: String,
    var maxPersonas: Int,
    val servicio: ServicioResp
)

fun ServicioHoteleraResp.toDto(): ServicioHoteleraDto {
    return ServicioHoteleraDto(
        idHoteleria = this.idHoteleria,
        servicio = this.servicio.idServicio,
        tipoHabitacion = this.tipoHabitacion,
        estrellas = this.estrellas,
        incluyeDesayuno = this.incluyeDesayuno,
        maxPersonas = this.maxPersonas
    )
}

fun ServicioHoteleraDto.toCreateDto(): ServicioHoteleraCreateDto {
    return ServicioHoteleraCreateDto(
        tipoHabitacion = this.tipoHabitacion,
        estrellas = this.estrellas,
        incluyeDesayuno = this.incluyeDesayuno,
        maxPersonas = this.maxPersonas,
        servicio = this.servicio
    )
}