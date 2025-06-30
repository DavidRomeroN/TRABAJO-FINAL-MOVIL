package pe.edu.upeu.granturismojpc.utils

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.granturismojpc.model.ChatMessage

/**
 * Singleton para mantener el estado del chat a lo largo del ciclo de vida de la aplicación.
 * Los mensajes persisten mientras la aplicación esté en memoria.
 * Se pueden borrar explícitamente al cerrar la sesión.
 */
object ChatStateHolder {

    private val _mensajes = MutableStateFlow<List<ChatMessage>>(emptyList())
    val mensajes: StateFlow<List<ChatMessage>> = _mensajes.asStateFlow()

    fun addMessage(message: ChatMessage) {
        _mensajes.update { it + message }
    }

    fun setMessages(messages: List<ChatMessage>) {
        _mensajes.value = messages
    }

    /**
     * Limpia el historial de mensajes del chat.
     * Esto debería llamarse cuando el usuario cierra sesión.
     */
    fun clearMessages() {
        _mensajes.value = emptyList()
    }
}
