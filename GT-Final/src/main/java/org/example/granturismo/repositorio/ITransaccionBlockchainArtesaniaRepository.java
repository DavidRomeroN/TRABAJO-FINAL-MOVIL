package org.example.granturismo.repositorio;

import org.example.granturismo.modelo.TransaccionBlockchainArtesania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las transacciones blockchain de artesanías
 */
@Repository
public interface ITransaccionBlockchainArtesaniaRepository extends JpaRepository<TransaccionBlockchainArtesania, Long> {

    /**
     * Busca todas las transacciones de una artesanía específica
     * @param idArtesania ID de la artesanía
     * @return Lista de transacciones
     */
    List<TransaccionBlockchainArtesania> findByIdArtesaniaOrderByFechaRegistroDesc(Long idArtesania);

    /**
     * Busca la última transacción confirmada de una artesanía
     * @param idArtesania ID de la artesanía
     * @return Transacción más reciente confirmada
     */
    @Query("SELECT t FROM TransaccionBlockchainArtesania t WHERE t.idArtesania = :idArtesania AND t.estado = 'CONFIRMADO' ORDER BY t.fechaRegistro DESC")
    Optional<TransaccionBlockchainArtesania> findUltimaTransaccionConfirmada(@Param("idArtesania") Long idArtesania);

    /**
     * Busca transacción por hash
     * @param hashTransaccion Hash de la transacción
     * @return Transacción encontrada
     */
    Optional<TransaccionBlockchainArtesania> findByHashTransaccion(String hashTransaccion);

    /**
     * Cuenta transacciones por estado
     * @param estado Estado de la transacción
     * @return Cantidad de transacciones
     */
    long countByEstado(String estado);
}