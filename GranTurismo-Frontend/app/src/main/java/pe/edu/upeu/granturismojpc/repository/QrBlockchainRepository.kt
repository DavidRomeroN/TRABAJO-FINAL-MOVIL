package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import pe.edu.upeu.granturismojpc.data.remote.RestQrBlockchain
import pe.edu.upeu.granturismojpc.model.EstadoVerificacionDto
import pe.edu.upeu.granturismojpc.model.QrCodeResponseDto
import pe.edu.upeu.granturismojpc.model.QrInfoDto
import pe.edu.upeu.granturismojpc.model.VerificacionArtesanoDto
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface QrBlockchainRepository {
    suspend fun generarQrYBlockchainParaArtesania(idArtesania: Long): Result<QrCodeResponseDto>
    suspend fun verificarEstadoBlockchain(idArtesania: Long): Result<Map<String, String>>
    suspend fun regenerarQr(idArtesania: Long): Result<Map<String, String>>
    suspend fun generarQrDirecto(texto: String, ancho: Int = 300, alto: Int = 300): Result<Map<String, String>>
    suspend fun obtenerInfoQr(): Result<QrInfoDto>
    suspend fun verificarArtesanoPorCodigo(codigoVerificacion: String): Result<VerificacionArtesanoDto>
    suspend fun verificarEstadoRapido(codigoVerificacion: String): Result<EstadoVerificacionDto>
    suspend fun eliminarQr(filename: String): Result<Map<String, Any>>
    suspend fun obtenerEstadisticasQr(): Result<Map<String, Any>>
}

class QrBlockchainRepositoryImpl @Inject constructor(
    private val restQrBlockchain: RestQrBlockchain
) : QrBlockchainRepository {

    companion object {
        private const val TAG = "QrBlockchainRepository"
    }

    override suspend fun generarQrYBlockchainParaArtesania(idArtesania: Long): Result<QrCodeResponseDto> {
        return try {
            Log.i(TAG, "Generando QR y blockchain para artesanía ID: $idArtesania")

            val response = restQrBlockchain.generarQrYBlockchain(
                TokenUtils.TOKEN_CONTENT,
                idArtesania
            )

            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "QR y blockchain generados exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Error al generar QR: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al generar QR y blockchain", e)
            Result.failure(e)
        }
    }

    override suspend fun verificarEstadoBlockchain(idArtesania: Long): Result<Map<String, String>> {
        return try {
            Log.i(TAG, "Verificando estado blockchain para artesanía ID: $idArtesania")

            val response = restQrBlockchain.verificarEstadoBlockchain(
                TokenUtils.TOKEN_CONTENT,
                idArtesania
            )

            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "Estado verificado exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Error al verificar estado: ${response.code()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al verificar estado blockchain", e)
            Result.failure(e)
        }
    }

    override suspend fun regenerarQr(idArtesania: Long): Result<Map<String, String>> {
        return try {
            Log.i(TAG, "Regenerando QR para artesanía ID: $idArtesania")

            val response = restQrBlockchain.regenerarQr(
                TokenUtils.TOKEN_CONTENT,
                idArtesania
            )

            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "QR regenerado exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Error al regenerar QR: ${response.code()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al regenerar QR", e)
            Result.failure(e)
        }
    }

    override suspend fun generarQrDirecto(texto: String, ancho: Int, alto: Int): Result<Map<String, String>> {
        return try {
            Log.i(TAG, "Generando QR directo para texto: ${texto.take(50)}")

            val response = restQrBlockchain.generarQrDirecto(
                TokenUtils.TOKEN_CONTENT,
                texto,
                ancho,
                alto
            )

            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "QR directo generado exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Error al generar QR directo: ${response.code()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al generar QR directo", e)
            Result.failure(e)
        }
    }

    override suspend fun obtenerInfoQr(): Result<QrInfoDto> {
        return try {
            val response = restQrBlockchain.obtenerInfoQr(TokenUtils.TOKEN_CONTENT)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener info QR: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener info QR", e)
            Result.failure(e)
        }
    }

    override suspend fun verificarArtesanoPorCodigo(codigoVerificacion: String): Result<VerificacionArtesanoDto> {
        return try {
            Log.i(TAG, "Verificando artesano por código: $codigoVerificacion")

            val response = restQrBlockchain.verificarArtesanoPorCodigo(codigoVerificacion)

            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "Artesano verificado exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Error al verificar artesano: ${response.code()}"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al verificar artesano", e)
            Result.failure(e)
        }
    }

    override suspend fun verificarEstadoRapido(codigoVerificacion: String): Result<EstadoVerificacionDto> {
        return try {
            val response = restQrBlockchain.verificarEstadoRapido(codigoVerificacion)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al verificar estado rápido: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al verificar estado rápido", e)
            Result.failure(e)
        }
    }

    override suspend fun eliminarQr(filename: String): Result<Map<String, Any>> {
        return try {
            val response = restQrBlockchain.eliminarQr(TokenUtils.TOKEN_CONTENT, filename)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al eliminar QR: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al eliminar QR", e)
            Result.failure(e)
        }
    }

    override suspend fun obtenerEstadisticasQr(): Result<Map<String, Any>> {
        return try {
            val response = restQrBlockchain.obtenerEstadisticasQr(TokenUtils.TOKEN_CONTENT)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener estadísticas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener estadísticas", e)
            Result.failure(e)
        }
    }
}