package org.example.granturismo.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.modelo.*;
import org.example.granturismo.repositorio.*;
import org.example.granturismo.servicio.ChatService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final VertexAiGeminiChatModel geminiClient;

    private final IPaqueteRepository paqueteRepository;
    private final IDestinoRepository destinoRepository;
    private final IServicioAlimentacionRepository alimentacionRepository;
    private final IServicioArtesaniaRepository artesaniaRepository;
    private final IServicioHoteleriaRepository hoteleriaRepository;
    private final IServicioRepository servicioRepository;

    private final ISesionChatRepository sesionRepository;

    @Override
    public void eliminarSesionPorId(Long id) {
        if (!sesionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada");
        }
        sesionRepository.deleteById(id);
    }

    @Override
    public String generarRespuesta(String mensaje) {
        String mensajeLowerCase = mensaje.toLowerCase().trim();

        // Buscar por nombre de paquete
        for (Paquete paquete : paqueteRepository.findAll()) {
            if (mensajeLowerCase.contains(paquete.getDestino().getNombre().toLowerCase())) {
                return generarRespuestaParaPaqueteEspecifico(paquete, mensaje);
            }
        }

        // Buscar por nombre de destino
        for (Destino destino : destinoRepository.findAll()) {
            if (mensajeLowerCase.contains(destino.getNombre().toLowerCase())) {
                return generarRespuestaParaDestinoEspecifico(destino, mensaje);
            }
        }

        // Buscar por nombre de servicio hotelería
        for (ServicioHoteleria h : hoteleriaRepository.findAll()) {
            if (mensajeLowerCase.contains(h.getServicio().getNombreServicio().toLowerCase())) {
                return generarRespuestaParaHoteleriaEspecifica(h, mensaje);
            }
        }

        // Buscar por nombre de artesanía
        for (ServicioArtesania a : artesaniaRepository.findAll()) {
            if (mensajeLowerCase.contains(a.getServicio().getNombreServicio().toLowerCase())) {
                return generarRespuestaParaArtesaniaEspecifica(a, mensaje);
            }
        }

        // Buscar por nombre de alimentación
        for (ServicioAlimentacion a : alimentacionRepository.findAll()) {
            if (mensajeLowerCase.contains(a.getServicio().getNombreServicio().toLowerCase())) {
                return generarRespuestaParaAlimentacionEspecifica(a, mensaje);
            }
        }

        // Buscar por nombre de servicio general
        for (Servicio s : servicioRepository.findAll()) {
            if (mensajeLowerCase.contains(s.getNombreServicio().toLowerCase())) {
                return generarRespuestaParaServicioGeneral(s, mensaje);
            }
        }



        // Intenciones generales
        if (mensajeLowerCase.contains("paquete")) {
            return generarRespuestaConPaquetes(mensaje);
        } else if (mensajeLowerCase.contains("destino")) {
            return generarRespuestaConDestinos(mensaje);
        }
        else if (mensajeLowerCase.contains("servicio") && !mensajeLowerCase.contains("hotel") && !mensajeLowerCase.contains("alimentación") && !mensajeLowerCase.contains("artesanía")) {
            return generarRespuestaConServicios(mensaje);
        } else if (mensajeLowerCase.contains("hotel") || mensajeLowerCase.contains("hospedaje")) {
            return generarRespuestaConHoteleria(mensaje);
        } else if (mensajeLowerCase.contains("artesanía") || mensajeLowerCase.contains("artesania")) {
            return generarRespuestaConArtesania(mensaje);
        } else if (mensajeLowerCase.contains("comida") || mensajeLowerCase.contains("alimentación")) {
            return generarRespuestaConAlimentacion(mensaje);
        } else if (mensajeLowerCase.matches("hola|buenas( noches| tardes| dias)?|qué tal|saludos|hi")) {
            return "¡Hola! ¿En qué puedo ayudarte hoy con tu experiencia turística?";
        } else if (mensajeLowerCase.contains("adiós") || mensajeLowerCase.contains("chao") || mensajeLowerCase.contains("hasta luego")) {
            return "¡Hasta pronto! Espero que tengas una excelente experiencia.";
        }

/*
        // Validar si el mensaje está fuera del contexto turístico
        if (!esMensajeRelacionadoATurismo(mensajeLowerCase)) {
            return "Lo siento, solo puedo ayudarte con temas relacionados al turismo y nuestros servicios.";
        }
*/
        // Pregunta genérica
        String prompt = """
            Eres un asistente virtual turístico llamado GranTurBot que trabaja para la empresa GranTurismo, especializada en ofrecer experiencias de viaje en Perú. 
            Tienes acceso completo y actualizado a toda la información de la empresa, incluyendo destinos turísticos, paquetes de viaje, servicios de hotelería, alimentación y artesanía. 
            Tu misión es responder de forma clara, cálida y profesional únicamente con información **extraída directamente de la base de datos de GranTurismo**. 
            No puedes hacer reservas, pagos ni realizar acciones administrativas. 

            Si el usuario hace preguntas que no estén relacionadas con turismo o con los servicios que ofrece la empresa GranTurismo, responde cortésmente indicando que no puedes ayudar en ese tema.

            El usuario dijo: "%s"

            Responde basándote exclusivamente en los datos proporcionados y relacionados al turismo.
        """.formatted(mensaje);
        Prompt p = new Prompt(new UserMessage(prompt));
        return geminiClient.call(p).getResult().getOutput().getText();


    }

/*
    // Método para verificar si el mensaje trata sobre turismo
    private boolean esMensajeRelacionadoATurismo(String mensaje) {
        return mensaje.contains("turismo") ||
                mensaje.contains("viaje") ||
                mensaje.contains("paquete") ||
                mensaje.contains("destino") ||
                mensaje.contains("hotel") ||
                mensaje.contains("hospedaje") ||
                mensaje.contains("artesanía") ||
                mensaje.contains("artesania") ||
                mensaje.contains("comida") ||
                mensaje.contains("alimentación") ||
                mensaje.contains("alimentacion") ||
                mensaje.contains("excursión") ||
                mensaje.contains("excursion") ||
                mensaje.contains("visitar") ||
                mensaje.contains("clima") ||
                mensaje.contains("servicio");
    }
*/


    // ===== PAQUETE ESPECÍFICO =====
    private String generarRespuestaParaPaqueteEspecifico(Paquete paquete, String mensajeUsuario) {
        String datos = """
            Nombre del paquete: %s
            Descripción: %s
            Precio total: %.2f soles
            Estado: %s
            """.formatted(

                paquete.getDestino().getNombre(),
                paquete.getDescripcion(),
                paquete.getPrecioTotal(),
                paquete.getEstado().name()
        );

        String prompt = """
            El usuario preguntó: "%s".
            Detalles del paquete turístico:

            %s

            Redacta una respuesta cálida, clara y atractiva para el usuario.
            """.formatted(mensajeUsuario, datos);

        return responderConGemini(prompt);
    }

    // ===== DESTINO ESPECÍFICO =====
    private String generarRespuestaParaDestinoEspecifico(Destino destino, String mensajeUsuario) {
        String datos = """
            Nombre: %s
            Descripción: %s
            Ubicación: %s
            """.formatted(
                destino.getNombre(),
                destino.getDescripcion(),
                destino.getUbicacion()
        );

        String prompt = """
            El usuario preguntó: "%s".
            Aquí tienes información del destino mencionado:

            %s

            Elabora una recomendación turística informativa y atractiva.
            """.formatted(mensajeUsuario, datos);

        return responderConGemini(prompt);
    }

    // ===== HOTELERÍA ESPECÍFICA =====
    private String generarRespuestaParaHoteleriaEspecifica(ServicioHoteleria hotel, String mensajeUsuario) {
        String datos = """
            Servicio: %s
            Descripción: %s
            Precio base: %.2f soles
            """.formatted(
                hotel.getServicio().getNombreServicio(),
                hotel.getServicio().getDescripcion(),
                hotel.getServicio().getPrecioBase()
        );

        String prompt = """
            El usuario preguntó: "%s".
            Detalles del servicio de hotelería mencionado:

            %s

            Responde de forma cordial y útil.
            """.formatted(mensajeUsuario, datos);

        return responderConGemini(prompt);
    }

    // ===== ARTESANÍA ESPECÍFICA =====
    private String generarRespuestaParaArtesaniaEspecifica(ServicioArtesania artesania, String mensajeUsuario) {
        String datos = """
            Artesanía: %s
            Descripción: %s
            Precio base: %.2f soles
            """.formatted(
                artesania.getServicio().getNombreServicio(),
                artesania.getServicio().getDescripcion(),
                artesania.getServicio().getPrecioBase()
        );

        String prompt = """
            El usuario preguntó: "%s".
            Aquí está la información de la artesanía mencionada:

            %s

            Crea una respuesta amigable e informativa.
            """.formatted(mensajeUsuario, datos);

        return responderConGemini(prompt);
    }

    // ===== ALIMENTACIÓN ESPECÍFICA =====
    private String generarRespuestaParaAlimentacionEspecifica(ServicioAlimentacion comida, String mensajeUsuario) {
        String datos = """
            Servicio: %s
            Descripción: %s
            Precio base: %.2f soles
            """.formatted(
                comida.getServicio().getNombreServicio(),
                comida.getServicio().getDescripcion(),
                comida.getServicio().getPrecioBase()
        );

        String prompt = """
            El usuario preguntó: "%s".
            Información sobre el servicio de alimentación:

            %s

            Redacta una recomendación cálida y atractiva.
            """.formatted(mensajeUsuario, datos);

        return responderConGemini(prompt);
    }

    // ===== RESPUESTA GENERAL GEMINI =====
    private String responderConGemini(String prompt) {
        Prompt p = new Prompt(new UserMessage(prompt));
        return geminiClient.call(p).getResult().getOutput().getText();
    }

    // ===== CONSULTA GENERAL POR CATEGORÍA (paquetes, destinos, etc.) =====

    private String generarRespuestaConPaquetes(String mensajeUsuario) {
        List<Paquete> paquetes = paqueteRepository.findAll();

        if (paquetes.isEmpty()) {
            return "Por el momento no hay paquetes turísticos registrados.";
        }

        StringBuilder resumen = new StringBuilder();
        for (Paquete p : paquetes) {
            resumen.append("- ").append(p.getDestino().getNombre()).append(": ")
                    .append(p.getDescripcion()).append(" (Precio: ")
                    .append(p.getPrecioTotal()).append(" soles)\n");
        }

        String prompt = """
            El usuario preguntó: "%s".
            Información general de los paquetes turísticos:

            %s

            Genera una respuesta turística cálida y atractiva.
            """.formatted(mensajeUsuario, resumen);

        return responderConGemini(prompt);
    }

    private String generarRespuestaConDestinos(String mensajeUsuario) {
        List<Destino> destinos = destinoRepository.findAll();

        if (destinos.isEmpty()) {
            return "Actualmente no hay destinos registrados.";
        }

        StringBuilder resumen = new StringBuilder();
        for (Destino d : destinos) {
            resumen.append("- ").append(d.getNombre()).append(": ")
                    .append(d.getDescripcion()).append("\n");
        }

        String prompt = """
            El usuario preguntó: "%s".
            Lista de destinos turísticos disponibles:

            %s

            Responde con una recomendación turística clara y profesional.
            """.formatted(mensajeUsuario, resumen);

        return responderConGemini(prompt);
    }

    private String generarRespuestaConHoteleria(String mensajeUsuario) {
        List<ServicioHoteleria> servicios = hoteleriaRepository.findAll();

        if (servicios.isEmpty()) {
            return "No hay servicios de hotelería registrados por ahora.";
        }

        StringBuilder resumen = new StringBuilder();
        for (ServicioHoteleria h : servicios) {
            resumen.append("- ").append(h.getServicio().getNombreServicio()).append(": ")
                    .append(h.getServicio().getDescripcion()).append(" (Precio base: ")
                    .append(h.getServicio().getPrecioBase()).append(" soles)\n");
        }

        String prompt = """
            El usuario preguntó: "%s".
            Servicios de hotelería disponibles:

            %s

            Crea una respuesta clara y amigable con esta información.
            """.formatted(mensajeUsuario, resumen);

        return responderConGemini(prompt);
    }

    private String generarRespuestaConArtesania(String mensajeUsuario) {
        List<ServicioArtesania> artesanias = artesaniaRepository.findAll();

        if (artesanias.isEmpty()) {
            return "No se encontraron servicios de artesanía actualmente.";
        }

        StringBuilder resumen = new StringBuilder();
        for (ServicioArtesania a : artesanias) {
            resumen.append("- ").append(a.getServicio().getNombreServicio()).append(": ")
                    .append(a.getServicio().getDescripcion()).append(" (Precio base: ")
                    .append(a.getServicio().getPrecioBase()).append(" soles)\n");
        }

        String prompt = """
            El usuario preguntó: "%s".
            Lista de servicios de artesanía disponibles:

            %s

            Redacta una respuesta turística cálida para el usuario.
            """.formatted(mensajeUsuario, resumen);

        return responderConGemini(prompt);
    }

    private String generarRespuestaConAlimentacion(String mensajeUsuario) {
        List<ServicioAlimentacion> alimentacion = alimentacionRepository.findAll();

        if (alimentacion.isEmpty()) {
            return "No se han registrado servicios de alimentación aún.";
        }

        StringBuilder resumen = new StringBuilder();
        for (ServicioAlimentacion a : alimentacion) {
            resumen.append("- ").append(a.getServicio().getNombreServicio()).append(": ")
                    .append(a.getServicio().getDescripcion()).append(" (Precio base: ")
                    .append(a.getServicio().getPrecioBase()).append(" soles)\n");
        }

        String prompt = """
            El usuario preguntó: "%s".
            Servicios de alimentación turística:

            %s

            Elabora una recomendación cálida y útil con base en esta información.
            """.formatted(mensajeUsuario, resumen);

        return responderConGemini(prompt);
    }

    private String generarRespuestaConServicios(String mensajeUsuario) {
        List<Servicio> servicios = servicioRepository.findAll();

        if (servicios.isEmpty()) {
            return "Actualmente no hay servicios registrados.";
        }

        StringBuilder resumen = new StringBuilder();
        for (Servicio s : servicios) {
            resumen.append("- ").append(s.getNombreServicio()).append(": ")
                    .append(s.getDescripcion()).append(" (Precio base: ")
                    .append(s.getPrecioBase()).append(" soles)\n");
        }

        String prompt = """
        El usuario preguntó: "%s".
        Lista de todos los servicios registrados en la empresa:

        %s

        Redacta una respuesta clara, profesional y turística basada en los datos.
        """.formatted(mensajeUsuario, resumen);

        return responderConGemini(prompt);
    }



    // ===== SERVICIO GENERAL =====
    private String generarRespuestaParaServicioGeneral(Servicio servicio, String mensajeUsuario) {
        String datos = """
        Nombre del servicio: %s
        Descripción: %s
        Precio base: %.2f soles
        """.formatted(
                servicio.getNombreServicio(),
                servicio.getDescripcion(),
                servicio.getPrecioBase()
        );

        String prompt = """
        El usuario preguntó: "%s".
        Información general del servicio:

        %s

        Redacta una respuesta clara, informativa y turística con esta información.
        """.formatted(mensajeUsuario, datos);

        return responderConGemini(prompt);
    }



    @Autowired
    private ISesionChatRepository sesionChatRepository;

    @Autowired
    private IConversacionRepository conversacionRepository;

    private SesionChat obtenerSesionActiva(Long usuarioId) {
        Optional<SesionChat> sesion = sesionChatRepository.findTopByUsuarioIdOrderByUltimaInteraccionDesc(usuarioId);
        if (sesion.isPresent()) {
            SesionChat s = sesion.get();
            if (Duration.between(s.getUltimaInteraccion(), LocalDateTime.now()).toMinutes() < 15) {
                s.setUltimaInteraccion(LocalDateTime.now());
                return sesionChatRepository.save(s);
            }
        }

        return sesionChatRepository.save(SesionChat.builder()
                .usuarioId(usuarioId)
                .inicio(LocalDateTime.now())
                .ultimaInteraccion(LocalDateTime.now())
                .build());
    }

    @Override
    public String generarRespuestaConSesion(Usuario usuario, String mensaje) {
        SesionChat sesion = obtenerSesionActiva(usuario.getIdUsuario());

        String respuesta = generarRespuesta(mensaje);

        conversacionRepository.save(Conversacion.builder()
                .mensajeUsuario(mensaje)
                .respuestaBot(respuesta)
                .fecha(LocalDateTime.now())
                .sesion(sesion)
                .build());

        return respuesta;
    }

}
