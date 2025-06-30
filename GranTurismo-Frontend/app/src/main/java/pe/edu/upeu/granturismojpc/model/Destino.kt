package pe.edu.upeu.granturismojpc.model

data class DestinoDto(
    val idDestino: Long,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    //val imagenUrl: String,
    val latitud: Double,
    val longitud: Double,
    val popularidad: Int,
    val precioMedio: Double,
    val rating: Double,
)

data class DestinoCreateDto(
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    //val imagenUrl: String,
    val latitud: Double,
    val longitud: Double,
    val popularidad: Int,
    val precioMedio: Double,
    val rating: Double,
)

data class DestinoResp(
    val idDestino: Long,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val imagenUrl: String,
    val latitud: Double,
    val longitud: Double,
    val popularidad: Int,
    val precioMedio: Double,
    val rating: Double,
)

data class DestinoEmbeddable(
    val idDestino: Long,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val imagenUrl: String,
    val latitud: Double,
    val longitud: Double,
    val popularidad: Int,
    val precioMedio: Double,
    val rating: Double,
)

fun DestinoResp.toDto(): DestinoDto{
    return DestinoDto(
        idDestino = this.idDestino,
        nombre = this.nombre,
        descripcion = this.descripcion,
        ubicacion = this.ubicacion,
        //imagenUrl = this.imagenUrl,
        latitud = this.latitud,
        longitud = this.longitud,
        popularidad = this.popularidad,
        precioMedio = this.precioMedio,
        rating = this.rating,
    )
}

fun DestinoDto.toCreateDto(): DestinoCreateDto {
    return DestinoCreateDto(
        nombre = this.nombre,
        descripcion = this.descripcion,
        ubicacion = this.ubicacion,
        //imagenUrl = this.imagenUrl,
        latitud = this.latitud,
        longitud = this.longitud,
        popularidad = this.popularidad,
        precioMedio = this.precioMedio,
        rating = this.rating,
    )
}

fun DestinoResp.toEmbeddable(): DestinoEmbeddable = DestinoEmbeddable(
    idDestino = idDestino,
    nombre = nombre,
    descripcion = descripcion,
    ubicacion = ubicacion,
    imagenUrl = imagenUrl,
    latitud = latitud,
    longitud = longitud,
    popularidad = popularidad,
    precioMedio = precioMedio,
    rating = rating
)

fun DestinoEmbeddable.toResp(): DestinoResp = DestinoResp(
    idDestino = idDestino,
    nombre = nombre,
    descripcion = descripcion,
    ubicacion = ubicacion,
    imagenUrl = imagenUrl,
    latitud = latitud,
    longitud = longitud,
    popularidad = popularidad,
    precioMedio = precioMedio,
    rating = rating
)

fun toDestinoEmbeddable(nombre: String): DestinoEmbeddable {
    return DestinoEmbeddable(
        idDestino = 0L,
        nombre = nombre,
        descripcion = "",
        ubicacion = "",
        imagenUrl = "",
        latitud = 0.0,
        longitud = 0.0,
        popularidad = 0,
        precioMedio = 0.0,
        rating = 0.0
    )
}