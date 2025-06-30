package org.example.granturismo.servicio;

import org.example.granturismo.dtos.VerificacionArtesanoDTO;

import java.util.Map;

/**
 * Servicio para verificar autenticidad de artesanos
 */
public interface IVerificacionArtesanoService {

    /**
     * Genera un código de verificación único para una artesanía
     * @param idArtesania ID de la artesanía
     * @return Código de verificación único
     */
    String generarCodigoVerificacion(Long idArtesania);

    /**
     * Obtiene información completa de verificación por código
     * @param codigoVerificacion Código único de verificación
     * @return Información completa del artesano y verificación
     */
    VerificacionArtesanoDTO obtenerVerificacionPorCodigo(String codigoVerificacion);

    /**
     * Verificación rápida del estado
     * @param codigoVerificacion Código de verificación
     * @return Estado básico de verificación
     */
    Map<String, Object> verificarEstadoRapido(String codigoVerificacion);

    /**
     * Genera URL completa de verificación para incluir en QR
     * @param codigoVerificacion Código de verificación
     * @return URL completa de verificación
     */
    String generarUrlVerificacion(String codigoVerificacion);

    /**
     * Actualizar estado de verificación en blockchain
     * @param codigoVerificacion Código de verificación
     * @param hashBlockchain Hash de la transacción
     */
    void actualizarEstadoBlockchain(String codigoVerificacion, String hashBlockchain);
}