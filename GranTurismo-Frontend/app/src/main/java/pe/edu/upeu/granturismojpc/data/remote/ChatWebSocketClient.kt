package pe.edu.upeu.granturismojpc.data.remote

import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import pe.edu.upeu.granturismojpc.model.ChatMessage
import pe.edu.upeu.granturismojpc.utils.TokenUtils // Asegúrate de importar TokenUtils
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ChatWebSocketClient(
    private val token: String, // El token se pasa aquí
    private val onMessageReceived: (ChatMessage) -> Unit,
    private val onConnected: (() -> Unit)? = null,
    private val onDisconnected: ((String) -> Unit)? = null, // Callback para desconexión
    private val onError: ((Exception) -> Unit)? = null // Callback para errores


) : WebSocketClient(
    URI("ws://localhost:8080/chat?token=${URLEncoder.encode(token, StandardCharsets.UTF_8.toString())}")

) {
    private val gson = Gson()

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("✅ WebSocket conectado. Estatus: ${handshakedata?.httpStatus}, Mensaje: ${handshakedata?.httpStatusMessage}")
        onConnected?.invoke()
    }

    override fun onMessage(message: String?) {
        message?.let {
            try {
                val originalMsg = gson.fromJson(it, ChatMessage::class.java)
                val msg = ChatMessage(remitente = "bot", contenido = originalMsg.contenido)
                onMessageReceived(msg)
            } catch (e: Exception) {
                println("Error al parsear mensaje JSON del WebSocket: $it, Error: ${e.message}")
                onError?.invoke(e) // Notifica error de parseo
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("WebSocket cerrado. Código: $code, Razón: '$reason', Remoto: $remote")
        onDisconnected?.invoke(reason ?: "Cierre desconocido")

        // Aquí puedes detectar si la razón de cierre es por un 401
        // Nota: Los códigos de cierre de WebSocket (como 1000, 1006) son diferentes a los HTTP.
        // Si el servidor envía un código personalizado (como 4001 para UNAUTHORIZED en Spring),
        // este debería reflejarse en 'code' o 'reason'.
        if (reason != null && (reason.contains("401", ignoreCase = true) || reason.contains("Unauthorized", ignoreCase = true))) {
            println("WebSocket cerrado debido a un token inválido/no autorizado.")
            // Podrías notificar al ViewModel para que navegue a Login
        }
    }

    override fun onError(ex: Exception?) {
        println("❌ Error WebSocket: ${ex?.message}")
        ex?.let { onError?.invoke(it) }
    }

    fun enviar(remitente: String, contenido: String) {
        val msg = ChatMessage(remitente, contenido)
        send(gson.toJson(msg))
        println("Mensaje enviado: ${gson.toJson(msg)}")
    }

    // Método para desconectar explícitamente
    fun disconnect() {
        if (isOpen) {
            close()
        }
    }
}