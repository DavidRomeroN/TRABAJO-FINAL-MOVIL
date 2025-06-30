package org.example.granturismo.servicio;

import org.example.granturismo.modelo.SesionChat;

import java.time.LocalDateTime;

public interface SesionChatService {

    SesionChat obtenerSesionActivaOCrear(Long usuarioId);
}
