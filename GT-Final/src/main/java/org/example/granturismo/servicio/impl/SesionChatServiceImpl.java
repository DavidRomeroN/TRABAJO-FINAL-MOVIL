package org.example.granturismo.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.modelo.SesionChat;
import org.example.granturismo.repositorio.ISesionChatRepository;
import org.example.granturismo.servicio.SesionChatService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SesionChatServiceImpl implements SesionChatService {

    private final ISesionChatRepository sesionChatRepository;

    @Override
    public SesionChat obtenerSesionActivaOCrear(Long usuarioId) {
        return sesionChatRepository.findTopByUsuarioIdOrderByUltimaInteraccionDesc(usuarioId)
                .map(sesion -> {
                    // 🟡 Actualiza la última interacción
                    sesion.setUltimaInteraccion(LocalDateTime.now());
                    System.out.println("♻️ Sesión existente encontrada para usuario " + usuarioId + ", ID: " + sesion.getId());
                    return sesionChatRepository.save(sesion);
                })
                .orElseGet(() -> {
                    // 🆕 Crea nueva sesión
                    SesionChat nueva = new SesionChat();
                    nueva.setUsuarioId(usuarioId);
                    nueva.setInicio(LocalDateTime.now());
                    nueva.setUltimaInteraccion(LocalDateTime.now());
                    System.out.println("🆕 Nueva sesión creada para usuario " + usuarioId);
                    return sesionChatRepository.save(nueva);
                });
    }
}

