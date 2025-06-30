package org.example.granturismo.repositorio;

import org.example.granturismo.modelo.Punto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPuntoRepository extends JpaRepository<Punto, Long> {

    List<Punto> findByUidUsuario(Long uidUsuario);

    @Query("SELECT COALESCE(SUM(p.cantidadPuntos), 0) FROM Punto p WHERE p.uidUsuario = :uidUsuario")
    Integer sumCantidadPuntosByUidUsuario(@Param("uidUsuario") Long uidUsuario);
}