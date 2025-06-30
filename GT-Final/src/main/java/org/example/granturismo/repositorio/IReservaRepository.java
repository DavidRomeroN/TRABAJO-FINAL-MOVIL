package org.example.granturismo.repositorio;

import org.example.granturismo.modelo.Reserva;
import org.example.granturismo.modelo.Usuario;

import java.util.List;

public interface IReservaRepository extends ICrudGenericoRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);
}
