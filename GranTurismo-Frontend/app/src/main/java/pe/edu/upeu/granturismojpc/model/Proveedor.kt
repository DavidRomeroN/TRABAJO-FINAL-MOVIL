package pe.edu.upeu.granturismojpc.model

data class ProveedorDto(
    var idProveedor: Long,
    var nombreCompleto: String,
    var email: String,
    var telefono: String,
    var fechaRegistro: String,
    var usuario: Long
)

data class ProveedorResp(
    val idProveedor: Long,
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val fechaRegistro: String,
    val usuario: UsuarioRespSinToken
)

data class ProveedorCreateDto(
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val fechaRegistro: String,
    val usuario: Long
)

data class ProveedorEmbeddable(
    var idProveedor: Long,
    var nombreCompleto: String,
    var email: String,
    var telefono: String,
    var fechaRegistro: String,
    var usuario: Long
)

fun ProveedorResp.toDto(): ProveedorDto {
    return ProveedorDto(
        idProveedor = this.idProveedor,
        nombreCompleto = this.nombreCompleto,
        email = this.email,
        telefono = this.telefono,
        fechaRegistro = this.fechaRegistro,
        usuario = this.usuario.idUsuario
    )
}
fun ProveedorDto.toCreateDto(): ProveedorCreateDto {
    return ProveedorCreateDto(
        nombreCompleto = this.nombreCompleto,
        email = this.email,
        telefono = this.telefono,
        fechaRegistro = this.fechaRegistro,
        usuario = this.usuario
    )
}
fun ProveedorResp.toEmbeddable(): ProveedorEmbeddable = ProveedorEmbeddable(
    idProveedor = idProveedor,
    nombreCompleto = nombreCompleto,
    email = email,
    telefono = telefono,
    fechaRegistro = fechaRegistro,
    usuario = usuario.idUsuario
)

fun ProveedorEmbeddable.toResp(): ProveedorResp = ProveedorResp(
    idProveedor = idProveedor,
    nombreCompleto = nombreCompleto,
    email = email,
    telefono = telefono,
    fechaRegistro = fechaRegistro,
    usuario = UsuarioRespSinToken(
        idUsuario = usuario,
        email = email // Aquí reutilizas el email del proveedor
    )
)

fun toProveedorEmbeddable(nombre: String): ProveedorEmbeddable {
    return ProveedorEmbeddable(
        idProveedor = 0L, // o -1 si quieres representar "desconocido"
        nombreCompleto = nombre,
        email = "",
        telefono = "",
        fechaRegistro = "",
        usuario = 0L
    )
}