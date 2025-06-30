package pe.edu.upeu.granturismojpc.model

data class ServicioArtesaniaDto(
    var idArtesania: Long,
    var servicio: Long,
    var tipoArtesania: String,
    var nivelDificultad: String,
    var duracionTaller: Int,
    var incluyeMaterial: Boolean,
    var artesania: String,
    var origenCultural: String,
    var maxParticipantes: Int,
    var visitaTaller: Boolean,
    var artesano: String,
)

data class ServicioArtesaniaCreateDto(
    val tipoArtesania: String,
    val nivelDificultad: String,
    val duracionTaller: Int,
    val incluyeMaterial: Boolean,
    val artesania: String,
    var origenCultural: String,
    var maxParticipantes: Int,
    var visitaTaller: Boolean,
    var artesano: String,
    val servicio: Long
)

data class ServicioArtesaniaResp(
    val idArtesania: Long,
    val tipoArtesania: String,
    val nivelDificultad: String,
    val duracionTaller: Int,
    val incluyeMaterial: Boolean,
    val artesania: String,
    var origenCultural: String,
    var maxParticipantes: Int,
    var visitaTaller: Boolean,
    var artesano: String,
    val servicio: ServicioResp,
    val hashBlockchain: String? = null
)

fun ServicioArtesaniaResp.toDto(): ServicioArtesaniaDto {
    return ServicioArtesaniaDto(
        idArtesania = this.idArtesania,
        servicio = this.servicio.idServicio,
        tipoArtesania = this.tipoArtesania,
        nivelDificultad = this.nivelDificultad,
        duracionTaller = this.duracionTaller,
        incluyeMaterial = this.incluyeMaterial,
        artesania = this.artesania,
        origenCultural = this.origenCultural,
        maxParticipantes = this.maxParticipantes,
        visitaTaller = this.visitaTaller,
        artesano = this.artesano
    )
}

fun ServicioArtesaniaDto.toCreateDto(): ServicioArtesaniaCreateDto {
    return ServicioArtesaniaCreateDto(
        tipoArtesania = this.tipoArtesania,
        nivelDificultad = this.nivelDificultad,
        duracionTaller = this.duracionTaller,
        incluyeMaterial = this.incluyeMaterial,
        artesania = this.artesania,
        origenCultural = this.origenCultural,
        maxParticipantes = this.maxParticipantes,
        visitaTaller = this.visitaTaller,
        artesano = this.artesano,
        servicio = this.servicio
    )
}