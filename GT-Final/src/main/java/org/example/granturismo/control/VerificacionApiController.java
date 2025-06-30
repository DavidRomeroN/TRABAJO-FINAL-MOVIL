package org.example.granturismo.control; // Or a new package like .api

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.VerificacionArtesanoDTO;
import org.example.granturismo.servicio.IVerificacionArtesanoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // <-- MUST be @RestController

import java.util.Map;

@Slf4j
@RestController // <--- This must be @RestController
@RequestMapping("/api/verificar") // Different base path for APIs
@RequiredArgsConstructor
@Tag(name = "Verificación de Artesanos API", description = "API para verificar autenticidad de artesanos y artesanías (JSON)")
public class VerificacionApiController {

    private final IVerificacionArtesanoService verificacionArtesanoService;

    @GetMapping("/artesano/{codigoVerificacion}")
    @Operation(summary = "API de verificación del artesano",
            description = "Devuelve información de verificación en formato JSON")
    public ResponseEntity<VerificacionArtesanoDTO> verificarArtesanoApi(
            @Parameter(description = "Código único de verificación")
            @PathVariable String codigoVerificacion) {
        try {
            VerificacionArtesanoDTO verificacion = verificacionArtesanoService.obtenerVerificacionPorCodigo(codigoVerificacion);
            return ResponseEntity.ok(verificacion);
        } catch (Exception e) {
            log.error("Error en API de verificación: {}", codigoVerificacion, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estado/{codigoVerificacion}")
    @Operation(summary = "Verificación rápida del estado")
    public ResponseEntity<Map<String, Object>> verificarEstadoRapido(
            @PathVariable String codigoVerificacion) {
        try {
            Map<String, Object> estado = verificacionArtesanoService.verificarEstadoRapido(codigoVerificacion);
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}