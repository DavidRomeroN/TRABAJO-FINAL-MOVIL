package org.example.granturismo.control;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.modelo.*;
import org.example.granturismo.repositorio.IPaqueteRepository;
import org.example.granturismo.repositorio.IPuntoRepository;
import org.example.granturismo.repositorio.IReservaRepository;
import org.example.granturismo.repositorio.IUsuarioRepository;
import org.example.granturismo.servicio.IPuntoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IPuntoService puntoService;
    private final IUsuarioRepository usuarioRepository;
    private final IPuntoRepository puntoRepository;
    private final IReservaRepository reservaRepository; // NUEVA INYECCIÓN
    private final IPaqueteRepository paqueteRepository; // NUEVA INYECCIÓN

    // ⚠️ ENDPOINT TEMPORAL - Para dar puntos a usuarios existentes
    @PostMapping("/otorgar-puntos-registro/{email}")
    public ResponseEntity<String> otorgarPuntosRegistro(@PathVariable String email) {
        try {
            Usuario usuario = usuarioRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

            // Verificar si ya tiene puntos de registro
            List<Punto> puntosRegistroExistentes = puntoRepository.findByUidUsuario(usuario.getIdUsuario())
                    .stream()
                    .filter(p -> p.getTipoTransaccion() == TipoTransaccion.REGISTRO)
                    .toList();

            if (!puntosRegistroExistentes.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("El usuario " + email + " ya tiene puntos de registro otorgados");
            }

            PuntoDTO resultado = puntoService.otorgarPuntos(
                    usuario.getIdUsuario(),
                    TipoTransaccion.REGISTRO,
                    100,
                    null,
                    "Puntos de bienvenida por registro (otorgado manualmente)"
            );

            return ResponseEntity.ok(
                    "✅ Puntos otorgados exitosamente a " + email +
                            ": " + resultado.cantidadPuntos() + " puntos"
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Error al otorgar puntos: " + e.getMessage());
        }
    }

    // Endpoint para verificar puntos de un usuario
    @GetMapping("/verificar-puntos/{email}")
    public ResponseEntity<String> verificarPuntos(@PathVariable String email) {
        try {
            Usuario usuario = usuarioRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

            Integer puntosTotales = puntoRepository.sumCantidadPuntosByUidUsuario(usuario.getIdUsuario());
            List<Punto> historial = puntoRepository.findByUidUsuario(usuario.getIdUsuario());

            StringBuilder response = new StringBuilder();
            response.append("👤 Usuario: ").append(email).append("\n");
            response.append("💰 Puntos totales: ").append(puntosTotales != null ? puntosTotales : 0).append("\n");
            response.append("📝 Transacciones: ").append(historial.size()).append("\n\n");

            if (!historial.isEmpty()) {
                response.append("📋 Historial:\n");
                for (Punto punto : historial) {
                    response.append("- ").append(punto.getTipoTransaccion())
                            .append(": ").append(punto.getCantidadPuntos())
                            .append(" puntos (").append(punto.getFechaTransaccion()).append(")\n");
                }
            }

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Error: " + e.getMessage());
        }
    }

    // ===== TESTING PARA RESERVAS =====
    @PostMapping("/crear-reserva-test/{email}")
    public ResponseEntity<String> crearReservaTest(@PathVariable String email) {
        try {
            Usuario usuario = usuarioRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

            // Buscar el primer paquete disponible
            Paquete paquete = paqueteRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No hay paquetes disponibles"));

            // Crear reserva directamente (simulando)
            Reserva reserva = new Reserva();
            reserva.setUsuario(usuario);
            reserva.setPaquete(paquete);
            reserva.setEstado(Reserva.Estado.PENDIENTE); // Empezar como pendiente
            // Agregar otros campos necesarios según tu entidad Reserva

            Reserva reservaGuardada = reservaRepository.save(reserva);

            return ResponseEntity.ok(
                    "✅ Reserva de prueba creada:\n" +
                            "- ID: " + reservaGuardada.getIdReserva() + "\n" +
                            "- Usuario: " + email + "\n" +
                            "- Estado: " + reservaGuardada.getEstado() + "\n" +
                            "- Para confirmar: POST /api/admin/confirmar-reserva/" + reservaGuardada.getIdReserva()
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Error: " + e.getMessage());
        }
    }

    @PostMapping("/confirmar-reserva/{reservaId}")
    public ResponseEntity<String> confirmarReserva(@PathVariable Long reservaId) {
        try {
            Reserva reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            Reserva.Estado estadoAnterior = reserva.getEstado();
            reserva.setEstado(Reserva.Estado.CONFIRMADA);

            Reserva reservaActualizada = reservaRepository.save(reserva);

            // Verificar si se otorgaron puntos
            if (estadoAnterior != Reserva.Estado.CONFIRMADA) {
                // Simular la lógica de puntos (normalmente esto lo haría el service)
                puntoService.otorgarPuntos(
                        reservaActualizada.getUsuario().getIdUsuario(),
                        TipoTransaccion.RESERVA_CONFIRMADA,
                        50,
                        reservaActualizada.getIdReserva(),
                        "Puntos por reserva #" + reservaActualizada.getIdReserva() + " confirmada (test manual)."
                );
            }

            return ResponseEntity.ok(
                    "✅ Reserva confirmada exitosamente:\n" +
                            "- ID: " + reservaId + "\n" +
                            "- Estado anterior: " + estadoAnterior + "\n" +
                            "- Estado actual: " + reservaActualizada.getEstado() + "\n" +
                            "- Usuario: " + reservaActualizada.getUsuario().getEmail() + "\n" +
                            "- Puntos otorgados: 50"
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Error: " + e.getMessage());
        }
    }

    @GetMapping("/listar-reservas/{email}")
    public ResponseEntity<String> listarReservas(@PathVariable String email) {
        try {
            Usuario usuario = usuarioRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

            List<Reserva> reservas = reservaRepository.findByUsuario(usuario); // Asume que tienes este método

            StringBuilder response = new StringBuilder();
            response.append("📋 Reservas de ").append(email).append(":\n\n");

            if (reservas.isEmpty()) {
                response.append("No hay reservas para este usuario.");
            } else {
                for (Reserva reserva : reservas) {
                    response.append("- ID: ").append(reserva.getIdReserva())
                            .append(" | Estado: ").append(reserva.getEstado())
                            .append(" | Paquete: ").append(reserva.getPaquete().getTitulo()) // Ajusta según tu entidad
                            .append("\n");
                }
            }

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Error: " + e.getMessage());
        }
    }
}
