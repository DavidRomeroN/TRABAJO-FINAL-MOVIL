package org.example.granturismo.control;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.modelo.TipoTransaccion;
import org.example.granturismo.security.JwtTokenUtil;
import org.example.granturismo.servicio.IPuntoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/compartir")
@RequiredArgsConstructor
public class CompartirAppController {

    private final IPuntoService puntoService;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${app.puntos.compartir-app:20}")
    private int puntosCompartirApp;

    @PostMapping("/app")
    public ResponseEntity<PuntoDTO> compartirApp(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);

        PuntoDTO resultado = puntoService.otorgarPuntos(
                userId,
                TipoTransaccion.COMPARTIR_APP,
                puntosCompartirApp,
                null,
                "Puntos por compartir la aplicación"
        );

        return ResponseEntity.ok(resultado);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        return jwtTokenUtil.getUserIdFromToken(token);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}