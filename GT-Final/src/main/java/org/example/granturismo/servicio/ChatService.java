// src/main/java/org/example/granturismo/servicio/ChatService.java
package org.example.granturismo.servicio;

// Ya no necesitamos importar Usuario aquí

import org.example.granturismo.modelo.Usuario;

import java.util.List; // Aunque solo devolvamos una respuesta, mantenemos List para consistencia

public interface ChatService {
    // La firma ahora es más simple
    String generarRespuesta(String mensaje); // Ya no se pasa el objeto Usuario

    String generarRespuestaConSesion(Usuario usuario, String mensaje);


    void eliminarSesionPorId(Long id);

}