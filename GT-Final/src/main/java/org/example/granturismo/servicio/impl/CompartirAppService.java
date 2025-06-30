package org.example.granturismo.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.modelo.TipoTransaccion;
import org.example.granturismo.servicio.IPuntoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompartirAppService {

    private final IPuntoService puntoService;

    @Value("${app.puntos.compartir-app:20}")
    private int puntosCompartirApp;

    public PuntoDTO compartirApp(Long uidUsuario) {
        return puntoService.otorgarPuntos(
                uidUsuario,
                TipoTransaccion.COMPARTIR_APP,
                puntosCompartirApp,
                null,
                "Puntos por compartir la aplicación"
        );
    }
}