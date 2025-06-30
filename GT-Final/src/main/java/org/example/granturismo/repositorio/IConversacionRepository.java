package org.example.granturismo.repositorio;

import org.example.granturismo.modelo.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IConversacionRepository extends JpaRepository<Conversacion, Long> {
    List<Conversacion> findAllBySesion_UsuarioIdOrderByFechaAsc(Long usuarioId);
    List<Conversacion> findAllBySesion_IdOrderByFechaAsc(Long sesionId);

    List<Conversacion> findBySesionId(Long sesionId);
}
