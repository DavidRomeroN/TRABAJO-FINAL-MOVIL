// src/main/java/org/example/granturismo/servicio/ChatWebSocketHandler.java
package org.example.granturismo.servicio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.granturismo.modelo.ChatMessage;
import org.example.granturismo.modelo.Usuario; // Asegúrate de importar tu clase Usuario
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
// import java.util.List; // Ya no es necesario importar List aquí
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Mapea ID de sesión de WebSocket a objeto Usuario para un acceso rápido
    private final ConcurrentHashMap<String, Usuario> sessionUserMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<WebSocketSession> sesiones = new CopyOnWriteArrayList<>();


    // Quitamos la inyección de TokenService, ya que el interceptor se encarga de la validación
    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Recuperar el objeto Usuario que fue autenticado por el HandshakeInterceptor
        Usuario user = (Usuario) session.getAttributes().get("usuario");

        if (user == null) {
            // Esto no debería suceder si el interceptor funcionó correctamente, pero es un buen fallback
            System.out.println("ERROR: Sesión WebSocket establecida sin usuario autenticado. ID: " + session.getId());
            session.close(CloseStatus.SERVER_ERROR.withReason("User not authenticated after handshake."));
            return;
        }

        // Si el usuario es válido, lo asociamos a la sesión
        sessionUserMap.put(session.getId(), user);
        sesiones.add(session); // Añade la sesión a la lista de sesiones activas (si la necesitas para broadcast)

        System.out.println("Nueva conexión WebSocket establecida y autenticada para usuario: " + user.getEmail() + " (ID: " + user.getIdUsuario() + ") - Sesión: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Usuario user = sessionUserMap.get(session.getId());

        if (user == null) {
            // Esto también es un fallback, la conexión debería haber sido cerrada antes si no hay usuario
            System.out.println("Sesión no autenticada intentó enviar mensaje (handleTextMessage): " + session.getId());
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Session not authenticated."));
            return;
        }

        System.out.println("Mensaje recibido de " + user.getEmail() + " (Sesión: " + session.getId() + "): " + message.getPayload());

        ChatMessage recibido = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        // Opcional: Puedes forzar que el remitente en el ChatMessage sea el email del usuario autenticado
        // recibido.setRemitente(user.getEmail());

        // Generar la respuesta usando el ChatService (ahora sin pasar el objeto Usuario)
        //String respuestaContenido = chatService.generarRespuesta(recibido.getContenido()); // Firma actualizada aquí
        String respuestaContenido = chatService.generarRespuestaConSesion(user, recibido.getContenido());



        ChatMessage respuestaDelBot = new ChatMessage("Bot", respuestaContenido);
        String respuestaJson = objectMapper.writeValueAsString(respuestaDelBot);

        if (session.isOpen()) {
            session.sendMessage(new TextMessage(respuestaJson));
            System.out.println("Mensaje enviado a " + user.getEmail() + " (Sesión: " + session.getId() + "): " + respuestaJson);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesiones.remove(session);
        sessionUserMap.remove(session.getId()); // Remover la asociación de usuario al cerrar la sesión
        System.out.println("Conexión WebSocket cerrada: " + session.getId() + " - " + status.getReason());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error de transporte en sesión " + session.getId() + " para usuario " + sessionUserMap.getOrDefault(session.getId(), new Usuario()).getEmail() + ": " + exception.getMessage());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}