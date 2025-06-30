package org.example.granturismo.servicio.impl;

import org.example.granturismo.servicio.IAsesorTuristicoService;
import org.example.granturismo.servicio.impl.LlamaService;
import org.springframework.stereotype.Service;

@Service
public class AsesorTuristicoServiceImpl implements IAsesorTuristicoService {

    private final LlamaService llamaService;

    public AsesorTuristicoServiceImpl(LlamaService llamaService) {
        this.llamaService = llamaService;
    }

    @Override
    public String generarRecomendacion(String prompt) {
        return llamaService.enviarPrompt(prompt);
    }
}
