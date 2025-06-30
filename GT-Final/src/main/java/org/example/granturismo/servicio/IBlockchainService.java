package org.example.granturismo.servicio;

import org.example.granturismo.dtos.ArtesaniaBlockchainDTO;
import org.example.granturismo.excepciones.BlockchainException;

/**
 * Servicio para interactuar con la blockchain
 */
public interface IBlockchainService {

    /**
     * Registra los datos de una artesanía en la blockchain
     * @param artesaniaData Datos de la artesanía a registrar
     * @return Hash de la transacción
     * @throws BlockchainException Si ocurre un error en la blockchain
     */
    String registrarArtesaniaEnBlockchain(ArtesaniaBlockchainDTO artesaniaData) throws BlockchainException;

    /**
     * Genera la URL completa al explorador de la blockchain para un hash específico
     * @param hashTransaccion Hash de la transacción
     * @return URL del explorador
     */
    String generarBlockchainExplorerUrl(String hashTransaccion);

    /**
     * Verifica el estado de una transacción en la blockchain
     * @param hashTransaccion Hash de la transacción
     * @return true si la transacción está confirmada, false en caso contrario
     * @throws BlockchainException Si ocurre un error al verificar
     */
    boolean verificarTransaccion(String hashTransaccion) throws BlockchainException;

    /**
     * Obtiene información detallada de una transacción
     * @param hashTransaccion Hash de la transacción
     * @return Información de la transacción en formato JSON
     * @throws BlockchainException Si ocurre un error al obtener la información
     */
    String obtenerDetallesTransaccion(String hashTransaccion) throws BlockchainException;
}