package pe.edu.upeu.granturismojpc.model

data class ServicioAlimentacionDto(
    var idAlimentacion: Long,
    var tipoComida: String,
    var estiloGastronomico: String,
    var incluyeBebidas:String,
    var servicio: Long,
)

data class ServicioAlimentacionResp(
    val idAlimentacion: Long,
    val tipoComida: String,
    var estiloGastronomico: String,
    var incluyeBebidas:String,
    val servicio: ServicioResp
)

data class ServicioAlimentacionCreateDto(
    val tipoComida: String,
    var estiloGastronomico: String,
    var incluyeBebidas:String,
    val servicio: Long
)

fun ServicioAlimentacionResp.toDto(): ServicioAlimentacionDto {
    return ServicioAlimentacionDto(
        idAlimentacion = this.idAlimentacion,
        servicio = this.servicio.idServicio,
        tipoComida = this.tipoComida,
        estiloGastronomico = this.estiloGastronomico,
        incluyeBebidas = this.incluyeBebidas
    )
}

fun ServicioAlimentacionDto.toCreateDto(): ServicioAlimentacionCreateDto {
    return ServicioAlimentacionCreateDto(
        tipoComida = this.tipoComida,
        estiloGastronomico = this.estiloGastronomico,
        incluyeBebidas = this.incluyeBebidas,
        servicio = this.servicio
    )
}