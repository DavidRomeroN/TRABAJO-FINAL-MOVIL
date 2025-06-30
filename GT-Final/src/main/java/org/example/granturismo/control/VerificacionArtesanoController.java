/*package org.example.granturismo.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.VerificacionArtesanoDTO;
import org.example.granturismo.servicio.IVerificacionArtesanoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/verificar")
@RequiredArgsConstructor
@Tag(name = "Verificación de Artesanos", description = "API para verificar autenticidad de artesanos y artesanías")
public class VerificacionArtesanoController {

    private final IVerificacionArtesanoService verificacionArtesanoService;



    @GetMapping("/artesano/{codigoVerificacion}")
    @Operation(summary = "Página de verificación del artesano",
            description = "Muestra información completa del artesano y su verificación en blockchain")
    public String paginaVerificacionArtesano(
            @Parameter(description = "Código único de verificación")
            @PathVariable String codigoVerificacion,
            Model model) {

        log.info("Verificando artesano con código: {}", codigoVerificacion);

        try {
            VerificacionArtesanoDTO verificacion = verificacionArtesanoService.obtenerVerificacionPorCodigo(codigoVerificacion);

            model.addAttribute("verificacion", verificacion);
            model.addAttribute("titulo", "Verificación de Autenticidad - " + verificacion.getArtesano());
            model.addAttribute("esValido", verificacion.getVerificadoEnBlockchain());

            // Determinar el template según el estado
            if (verificacion.getVerificadoEnBlockchain()) {
                return "verificacion/artesano-verificado"; // Template para artesano verificado
            } else {
                return "verificacion/artesano-pendiente"; // Template para verificación pendiente
            }

        } catch (Exception e) {
            log.error("Error al verificar artesano: {}", codigoVerificacion, e);
            model.addAttribute("error", "Código de verificación no válido o expirado");
            model.addAttribute("codigoError", codigoVerificacion);
            return "verificacion/error"; // Template de error
        }
    }


    @GetMapping("/api/artesano/{codigoVerificacion}")
    @ResponseBody
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


    @GetMapping("/api/estado/{codigoVerificacion}")
    @ResponseBody
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


    @GetMapping("/info")
    public String paginaInformacion(Model model) {
        model.addAttribute("titulo", "Sistema de Verificación Blockchain");
        model.addAttribute("descripcion", "Cómo funciona nuestro sistema de autenticidad");
        return "verificacion/informacion";
    }
}*/