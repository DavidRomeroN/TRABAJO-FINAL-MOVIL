package org.example.granturismo.repositorio;

import org.example.granturismo.modelo.SesionChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISesionChatRepository extends JpaRepository<SesionChat, Long> {
    Optional<SesionChat> findTopByUsuarioIdOrderByUltimaInteraccionDesc(Long usuarioId);

    //List<SesionChat> findAllByUsuarioIdOrderByFechaDesc(Long usuarioId);
    List<SesionChat> findAllByUsuarioIdOrderByUltimaInteraccionDesc(Long usuarioId);


}
