// src/main/java/org/example/granturismo/configuracion/WebSocketConfig.java
package org.example.granturismo.configuracion;

import org.example.granturismo.servicio.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import org.example.granturismo.security.JwtTokenUtil;
import org.example.granturismo.repositorio.IUsuarioRepository;
import org.example.granturismo.modelo.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private IUsuarioRepository usuarioRepository;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/chat")
                .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        Map<String, String> params = UriComponentsBuilder.fromUri(request.getURI())
                                .build()
                                .getQueryParams()
                                .toSingleValueMap();
                        String token = params.get("token");

                        if (token == null || token.isEmpty()) {
                            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            System.out.println("Handshake rechazado: Token no proporcionado.");
                            return false;
                        }

                        try {
                            String username = jwtTokenUtil.getUsernameFromToken(token);
                            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

                            if (jwtTokenUtil.validateToken(token, userDetails)) {
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authentication);

                                Usuario usuario = usuarioRepository.findOneByEmail(username)
                                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado por email: " + username));

                                attributes.put("usuario", usuario); // Clave "usuario" para accederlo después
                                System.out.println("Handshake exitoso: Usuario " + username + " autenticado.");
                                return true;
                            } else {
                                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                                System.out.println("Handshake rechazado: Token inválido para usuario " + username);
                                return false;
                            }
                        } catch (UsernameNotFoundException e) {
                            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            System.out.println("Handshake rechazado: Usuario no encontrado para el token. " + e.getMessage());
                            return false;
                        } catch (Exception e) {
                            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            System.out.println("Handshake rechazado: Error al validar token. " + e.getMessage());
                            return false;
                        }
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                        // Opcional: lógica después del handshake
                    }
                });
    }
}