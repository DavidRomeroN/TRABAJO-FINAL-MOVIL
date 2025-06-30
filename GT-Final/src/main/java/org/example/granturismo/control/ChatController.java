package org.example.granturismo.control;

import lombok.RequiredArgsConstructor;

import org.example.granturismo.dtos.SesionDTO;
import org.example.granturismo.modelo.ChatMessage;
import org.example.granturismo.modelo.Conversacion;
import org.example.granturismo.modelo.SesionChat;
import org.example.granturismo.modelo.Usuario;
import org.example.granturismo.repositorio.IConversacionRepository;
import org.example.granturismo.repositorio.ISesionChatRepository;
import org.example.granturismo.repositorio.IUsuarioRepository;
import org.example.granturismo.security.JwtUserDetailsService;
import org.example.granturismo.servicio.ChatService;
import org.example.granturismo.servicio.SesionChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IUsuarioRepository usuarioRepository;
    private final IConversacionRepository conversacionRepository;
    private final SesionChatService sesionChatService; // ✅ Inyectamos correctamente el servicio
    private final JwtUserDetailsService jwtUserDetailsService;
    private final ISesionChatRepository iSesionChatRepository;
    private final ChatService chatService;

    @GetMapping("/historial")
    public List<Conversacion> obtenerHistorial(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario u = usuarioRepository.findOneByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return conversacionRepository.findAllBySesion_UsuarioIdOrderByFechaAsc(u.getIdUsuario());
    }
    @GetMapping("/sesiones/activa")
    public ResponseEntity<SesionDTO> obtenerSesionActiva(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUserDetailsService.extractUserId(authHeader.replace("Bearer ", ""));
        System.out.println("🔍 Usuario ID extraído del token: " + usuarioId);
        SesionChat sesion = sesionChatService.obtenerSesionActivaOCrear(usuarioId);
        return ResponseEntity.ok(new SesionDTO(
                sesion.getId(),
                sesion.getUltimaInteraccion(),
                "(sesión activa)"
        ));
    }

    @GetMapping("/sesiones/{id}/mensajes")
    public List<ChatMessage> obtenerMensajesPorSesion(@PathVariable Long id) {
        List<Conversacion> mensajes = conversacionRepository.findBySesionId(id);

        return mensajes.stream()
                .flatMap(c -> Stream.of(
                        new ChatMessage("usuario", c.getMensajeUsuario()),
                        new ChatMessage("bot", c.getRespuestaBot())
                ))
                .toList();
    }
    @GetMapping("/sesiones")
    public List<SesionDTO> listarSesiones(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUserDetailsService.extractUserId(authHeader.replace("Bearer ", ""));
        List<SesionChat> sesiones = iSesionChatRepository.findAllByUsuarioIdOrderByUltimaInteraccionDesc(usuarioId);


        return sesiones.stream().map(sesion -> {
            List<Conversacion> mensajes = conversacionRepository.findBySesionId(sesion.getId());
            String preview = mensajes.isEmpty()
                    ? "(sin mensajes)"
                    : mensajes.get(mensajes.size() - 1).getMensajeUsuario();

            return new SesionDTO(sesion.getId(), sesion.getUltimaInteraccion(), preview);
        }).toList();
    }
    @PostMapping("/sesiones")
    public ResponseEntity<SesionDTO> crearSesion(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUserDetailsService.extractUserId(authHeader.replace("Bearer ", ""));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SesionChat nuevaSesion = SesionChat.builder()
                .usuarioId(usuarioId)
                .inicio(LocalDateTime.now())
                .ultimaInteraccion(LocalDateTime.now())
                .build();

        SesionChat guardada = iSesionChatRepository.save(nuevaSesion);

        return ResponseEntity.ok(
                new SesionDTO(guardada.getId(), guardada.getUltimaInteraccion(), "(nuevo chat)")
        );
    }
    @DeleteMapping("/sesiones/{id}")
    public ResponseEntity<Void> eliminarSesion(@PathVariable Long id) {
        chatService.eliminarSesionPorId(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }




}
