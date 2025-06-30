package pe.edu.upeu.granturismojpc.ui.presentation.screens.chatbot

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
//import pe.edu.upeu.granturismojpc.data.remote.ApiClient
import pe.edu.upeu.granturismojpc.data.remote.ChatApi
//import pe.edu.upeu.granturismojpc.data.remote.ChatApiClient
import pe.edu.upeu.granturismojpc.data.remote.ChatWebSocketClient
import pe.edu.upeu.granturismojpc.model.ChatMessage
import pe.edu.upeu.granturismojpc.model.SesionDTO

import pe.edu.upeu.granturismojpc.utils.ChatStateHolder
import pe.edu.upeu.granturismojpc.utils.TokenUtils

import pe.edu.upeu.granturismojpc.model.toChatMessages




class ChatViewModel : ViewModel() {

    val mensajes = ChatStateHolder.mensajes

    private var cliente: ChatWebSocketClient? = null
    private var conectado = false
    /*
            private val _sesiones = MutableStateFlow<List<SesionDTO>>(emptyList())
            val sesiones: StateFlow<List<SesionDTO>> = _sesiones
    */
    private val _sesionIdSeleccionada = MutableStateFlow<Long?>(null)
    val sesionIdSeleccionada: StateFlow<Long?> = _sesionIdSeleccionada

    private val _sesiones = MutableStateFlow<List<SesionDTO>>(emptyList())
    val sesiones: StateFlow<List<SesionDTO>> = _sesiones


    init {
        ChatStateHolder.clearMessages()

        viewModelScope.launch {
            try {
                val token = "Bearer ${TokenUtils.TOKEN}"

                // Carga sesión activa y mensajes
                val sesionDTO = ChatApi.ApiClient.chatApi.obtenerSesionActiva(token)
                _sesionIdSeleccionada.value = sesionDTO.sesionId

                val historial = ChatApi.ApiClient.chatApi.obtenerMensajesPorSesion(token, sesionDTO.sesionId)
                historial.forEach { mensaje ->
                    ChatStateHolder.addMessage(mensaje)
                }

                // Carga todas las sesiones para el sidebar
                _sesiones.value = ChatApi.ApiClient.chatApi.listarSesiones(token)

            } catch (e: Exception) {
                println("❌ Error cargando historial o sesiones: ${e.message}")
            }
        }

        conectarWebSocket()
    }

    private fun conectarWebSocket() {
        cliente = ChatWebSocketClient(
            onMessageReceived = { mensaje ->
                viewModelScope.launch {
                    ChatStateHolder.addMessage(mensaje) // Agrega el mensaje al Singleton
                }
            },
            onConnected = {
                conectado = true
                println("✅ El ViewModel fue notificado: WebSocket conectado")
            },
            onDisconnected = {
                conectado = false
                println("🔌 WebSocket desconectado.")
                // Opcional: Podrías querer reconectar aquí si es un cierre inesperado
            },
            onError = { error ->
                conectado = false
                println("❌ Error en WebSocket: $error")
            },
            token = TokenUtils.TOKEN
        )
        cliente?.connect()
    }


    fun enviarMensaje(contenido: String) {

        val sesionId = sesionIdSeleccionada.value

        if (sesionId == null || sesiones.value.none { it.sesionId == sesionId }) {
            println("❌ Sesión inválida o eliminada. No se puede enviar el mensaje.")
            // Aquí podrías usar un estado de UI para mostrar un mensaje al usuario
            return
        }

        val mensaje = ChatMessage("usuario", contenido)
        cliente?.enviar("usuario", contenido)
        ChatStateHolder.addMessage(mensaje)
    }
    // Opcional: Un método para desconectar el WebSocket cuando el ViewModel ya no es necesario
    override fun onCleared() {
        super.onCleared()
        cliente?.disconnect()
        println("🔌 WebSocket desconectado al limpiar ViewModel.")
    }

    fun cargarSesiones() {
        viewModelScope.launch {
            try {
                val token = "Bearer ${TokenUtils.TOKEN}"
                val lista = ChatApi.ApiClient.chatApi.listarSesiones(token)
                _sesiones.value = lista
            } catch (e: Exception) {
                println("❌ Error cargando sesiones: ${e.message}")
            }
        }
    }

    fun seleccionarSesion(id: Long) {
        _sesionIdSeleccionada.value = id
        ChatStateHolder.clearMessages()
        viewModelScope.launch {
            try {
                val token = "Bearer ${TokenUtils.TOKEN}"
                val mensajes = ChatApi.ApiClient.chatApi.obtenerMensajesPorSesion(token, id)
                mensajes.forEach { ChatStateHolder.addMessage(it) }
            } catch (e: Exception) {
                println("❌ Error cargando mensajes de sesión: ${e.message}")
            }
        }
    }

    fun crearNuevaSesion() {
        viewModelScope.launch {
            try {
                val token = "Bearer ${TokenUtils.TOKEN}"
                val nueva = ChatApi.ApiClient.chatApi.crearSesion(token)
                cargarSesiones()
                seleccionarSesion(nueva.sesionId)
            } catch (e: Exception) {
                println("❌ Error creando nueva sesión: ${e.message}")
            }
        }
    }

    fun eliminarSesion(sesionId: Long) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${TokenUtils.TOKEN}"
                ChatApi.ApiClient.chatApi.eliminarSesion(token, sesionId)

                // Limpiar mensajes si era la sesión activa
                if (sesionId == sesionIdSeleccionada.value) {
                    _sesionIdSeleccionada.value = null
                    clearChatHistory()
                }
                // Luego de eliminarla, recarga la lista
                cargarSesiones() // esta función debería recargar la lista
            } catch (e: Exception) {
                println("❌ Error al eliminar sesión: ${e.message}")
            }
        }
    }












    /**
     * Este método debería llamarse desde tu lógica de cierre de sesión.
     * No desde este ViewModel, sino desde el ViewModel que gestiona la sesión de usuario.
     */
    fun clearChatHistory() {
        ChatStateHolder.clearMessages()
    }


}
