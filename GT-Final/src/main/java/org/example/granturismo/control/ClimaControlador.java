package org.example.granturismo.control;

import org.example.granturismo.servicio.IClimaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/clima")
@CrossOrigin(origins = "*") // Permitir CORS si es necesario
public class ClimaControlador {

    @Autowired
    private IClimaServicio climaServicio;

    @GetMapping("/destino")
    public String obtenerClima(
            @RequestParam double latitud,
            @RequestParam double longitud
    ) {
        return climaServicio.obtenerResumenClimaParaDestino(latitud, longitud);
    }

    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> climaPorDia(
            @RequestParam double latitud,
            @RequestParam double longitud,
            @RequestParam(defaultValue = "0") int dia // Cambiar default a 0 (hoy)
    ) {
        try {
            // 🔍 LOGGING PARA DEBUG
            System.out.println("🌐 Recibiendo petición clima:");
            System.out.println("   - Latitud: " + latitud);
            System.out.println("   - Longitud: " + longitud);
            System.out.println("   - Día: " + dia);

            // ✅ VALIDAR PARÁMETROS
            if (latitud < -90 || latitud > 90) {
                Map<String, Object> error = Map.of("error", "Latitud inválida: " + latitud);
                return ResponseEntity.badRequest().body(error);
            }

            if (longitud < -180 || longitud > 180) {
                Map<String, Object> error = Map.of("error", "Longitud inválida: " + longitud);
                return ResponseEntity.badRequest().body(error);
            }

            if (dia < 0 || dia > 6) {
                Map<String, Object> error = Map.of("error", "Día inválido: " + dia + " (debe ser 0-6)");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> resultado = climaServicio.obtenerClimaDeDia(latitud, longitud, dia);

            // 🔍 LOGGING DE RESULTADO
            System.out.println("✅ Resultado del servicio:");
            System.out.println("   - Ideal: " + resultado.get("ideal"));
            System.out.println("   - Temperatura: " + resultado.get("temperatura"));
            System.out.println("   - Error: " + resultado.get("error"));

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            System.err.println("❌ Error en controlador: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = Map.of(
                    "error", "Error interno del servidor: " + e.getMessage()
            );
            return ResponseEntity.status(500).body(error);
        }
    }
}
