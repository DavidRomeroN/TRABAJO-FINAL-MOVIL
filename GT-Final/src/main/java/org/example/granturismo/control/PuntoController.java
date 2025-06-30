package org.example.granturismo.control;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.dtos.CanjePuntosRequestDTO;
import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.dtos.PuntosDisponiblesResponseDTO;
import org.example.granturismo.excepciones.SaldoInsuficienteException;
import org.example.granturismo.security.JwtTokenUtil;
import org.example.granturismo.servicio.IPuntoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/puntos")
@RequiredArgsConstructor
public class PuntoController {

    private final IPuntoService puntoService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/me")
    public ResponseEntity<PuntosDisponiblesResponseDTO> getPuntosDisponibles(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        PuntosDisponiblesResponseDTO response = puntoService.getPuntosDisponibles(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<PuntoDTO>> getHistorialPuntos(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<PuntoDTO> historial = puntoService.getHistorialPuntos(userId);
        return ResponseEntity.ok(historial);
    }

    @PostMapping("/canjear")
    public ResponseEntity<PuntoDTO> canjearPuntos(@RequestBody CanjePuntosRequestDTO requestDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            PuntoDTO resultado = puntoService.canjearPuntos(userId, requestDTO);
            return ResponseEntity.ok(resultado);
        } catch (SaldoInsuficienteException e) {
            return ResponseEntity.badRequest().body(null);
        }
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

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficiente(SaldoInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}