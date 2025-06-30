package org.example.granturismo.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.VerificacionArtesanoDTO;
import org.example.granturismo.servicio.IVerificacionArtesanoService;
import org.example.granturismo.servicio.impl.VerificationTokenServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/verificar")
@RequiredArgsConstructor
public class VerificacionWebController {

    private final IVerificacionArtesanoService verificacionArtesanoService;
    private final VerificationTokenServiceImpl tokenService;

    @GetMapping("/artesano/{codigoVerificacion}")
    public String paginaVerificacionArtesano(
            @PathVariable String codigoVerificacion,
            Model model) {
        log.info("Verificando artesano con código: {}", codigoVerificacion);
        try {
            VerificacionArtesanoDTO verificacion = verificacionArtesanoService.obtenerVerificacionPorCodigo(codigoVerificacion);
            model.addAttribute("verificacion", verificacion);
            model.addAttribute("titulo", "Verificación de Autenticidad - " + verificacion.getArtesano());
            model.addAttribute("esValido", verificacion.getVerificadoEnBlockchain());
            if (verificacion.getVerificadoEnBlockchain()) {
                return "verificacion/artesano-verificado";
            } else {
                return "verificacion/artesano-pendiente";
            }
        } catch (Exception e) {
            log.error("Error al verificar artesano: {}", codigoVerificacion, e);
            model.addAttribute("error", "Código de verificación no válido o expirado");
            model.addAttribute("codigoError", codigoVerificacion);
            return "verificacion/error";
        }
    }

    @GetMapping("/info")
    public String paginaInformacion(Model model) {
        model.addAttribute("titulo", "Sistema de Verificación Blockchain");
        model.addAttribute("descripcion", "Cómo funciona nuestro sistema de autenticidad");
        return "verificacion/informacion";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam String token, Model model) {
        log.info("Intentando verificar usuario con token: {}", token);
        boolean valid = tokenService.validateVerificationToken(token); // Este método ya actualiza el usuario como verificado.

        if (valid) {
            model.addAttribute("titulo", "Verificación Exitosa");
            model.addAttribute("mensaje", "¡Tu cuenta ha sido verificada correctamente!");
            // Aquí podrías añadir el email del usuario si tu tokenService devuelve el usuario,
            // pero para esta plantilla básica no es estrictamente necesario.
            return "verificacion/verificacion-exitosa"; // <-- CAMBIO CLAVE AQUÍ
        } else {
            model.addAttribute("titulo", "Error de Verificación");
            model.addAttribute("error", "El token de verificación es inválido o ha expirado.");
            model.addAttribute("codigoError", token);
            return "verificacion/error";
        }
    }
}