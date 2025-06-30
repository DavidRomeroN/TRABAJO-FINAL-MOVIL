package pe.edu.upeu.granturismojpc.model

data class Conversacion(
    val mensajeUsuario: String,
    val respuestaBot: String,
    val fecha: String,
    val sesionId: Long
)

fun Conversacion.toChatMessages(): List<ChatMessage> {
    return listOf(
        ChatMessage("Usuario", mensajeUsuario),
        ChatMessage("Bot", respuestaBot)
    )
}
