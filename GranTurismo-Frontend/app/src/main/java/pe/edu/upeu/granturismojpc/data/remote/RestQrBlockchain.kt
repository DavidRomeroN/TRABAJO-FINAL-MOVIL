package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.EstadoVerificacionDto
import pe.edu.upeu.granturismojpc.model.QrCodeResponseDto
import pe.edu.upeu.granturismojpc.model.QrInfoDto
import pe.edu.upeu.granturismojpc.model.VerificacionArtesanoDto
import retrofit2.Response
import retrofit2.http.*

interface RestQrBlockchain {
    companion object {
        const val BASE_RUTA_ARTESANIA = "servicioartesania"
        const val BASE_RUTA_QR = "/api/qr"
        const val BASE_RUTA_VERIFICAR = "/verificar/api"
    }

    /**
     * Generar QR + Blockchain para una artesanía
     */
    @POST("$BASE_RUTA_ARTESANIA/{idArtesania}/generar-qr-blockchain")
    suspend fun generarQrYBlockchain(
        @Header("Authorization") token: String,
        @Path("idArtesania") idArtesania: Long
    ): Response<QrCodeResponseDto>

    /**
     * Verificar estado en blockchain
     */
    @GET("$BASE_RUTA_ARTESANIA/{idArtesania}/verificar-blockchain")
    suspend fun verificarEstadoBlockchain(
        @Header("Authorization") token: String,
        @Path("idArtesania") idArtesania: Long
    ): Response<Map<String, String>>

    /**
     * Regenerar QR
     */
    @POST("$BASE_RUTA_ARTESANIA/{idArtesania}/regenerar-qr")
    suspend fun regenerarQr(
        @Header("Authorization") token: String,
        @Path("idArtesania") idArtesania: Long
    ): Response<Map<String, String>>

    /**
     * Generar QR directo (para testing)
     */
    @POST("$BASE_RUTA_QR/generar")
    suspend fun generarQrDirecto(
        @Header("Authorization") token: String,
        @Query("texto") texto: String,
        @Query("ancho") ancho: Int = 300,
        @Query("alto") alto: Int = 300
    ): Response<Map<String, String>>

    /**
     * Obtener información del servicio QR
     */
    @GET("$BASE_RUTA_QR/info")
    suspend fun obtenerInfoQr(
        @Header("Authorization") token: String
    ): Response<QrInfoDto>

    /**
     * Verificar artesano por código
     */
    @GET("$BASE_RUTA_VERIFICAR/artesano/{codigoVerificacion}")
    suspend fun verificarArtesanoPorCodigo(
        @Path("codigoVerificacion") codigoVerificacion: String
    ): Response<VerificacionArtesanoDto>

    /**
     * Verificación rápida del estado
     */
    @GET("$BASE_RUTA_VERIFICAR/estado/{codigoVerificacion}")
    suspend fun verificarEstadoRapido(
        @Path("codigoVerificacion") codigoVerificacion: String
    ): Response<EstadoVerificacionDto>

    /**
     * Eliminar QR de Cloudinary
     */
    @DELETE("$BASE_RUTA_QR/eliminar/{filename}")
    suspend fun eliminarQr(
        @Header("Authorization") token: String,
        @Path("filename") filename: String
    ): Response<Map<String, Any>>

    /**
     * Obtener estadísticas del servicio QR
     */
    @GET("$BASE_RUTA_QR/estadisticas")
    suspend fun obtenerEstadisticasQr(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>
}