package pe.edu.upeu.granturismojpc.model

data class UsuarioDto(
    var email: String,
    var clave: String,
)
data class UsuarioResp(
    val idUsuario: Long,
    val email: String,
    val token: String,
    val role: String
)
data class UsuarioRegisterDto(
    val email: String,
    val clave: String,
)
data class UsuarioRespSinToken(
    val idUsuario: Long,
    val email: String,
)