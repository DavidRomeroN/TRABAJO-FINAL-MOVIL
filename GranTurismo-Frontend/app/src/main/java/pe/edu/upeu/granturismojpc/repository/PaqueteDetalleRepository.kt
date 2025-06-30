package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import pe.edu.upeu.granturismojpc.data.remote.RestPaqueteDetalle
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface PaqueteDetalleRepository {
    suspend fun deletePaqueteDetalle(paqueteDetalle: PaqueteDetalleDto): Boolean
    suspend fun deletePaqueteDetalleId(id: Long): Boolean
    suspend fun reportarPaqueteDetalles(): List<PaqueteDetalleResp>
    suspend fun buscarPaqueteDetalleId(id: Long): PaqueteDetalleResp
    suspend fun buscarPaqueteDetallesByPaqueteId(paqueteId: Long): List<PaqueteDetalleResp>
    suspend fun insertarPaqueteDetalle(paqueteDetalle: PaqueteDetalleCreateDto): Boolean
    suspend fun modificarPaqueteDetalle(paqueteDetalle: PaqueteDetalleDto): Boolean
}

class PaqueteDetalleRepositoryImpl @Inject constructor(
    private val restPaqueteDetalle: RestPaqueteDetalle
): PaqueteDetalleRepository {

    override suspend fun deletePaqueteDetalle(paqueteDetalle: PaqueteDetalleDto): Boolean {
        val response = restPaqueteDetalle.deletePaqueteDetalle(
            TokenUtils.TOKEN_CONTENT,
            paqueteDetalle.idPaqueteDetalle
        )
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun deletePaqueteDetalleId(id: Long): Boolean {
        val response = restPaqueteDetalle.deletePaqueteDetalle(
            TokenUtils.TOKEN_CONTENT,
            id
        )
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarPaqueteDetalles(): List<PaqueteDetalleResp> {
        val response = restPaqueteDetalle.reportarPaqueteDetalle(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarPaqueteDetalleId(id: Long): PaqueteDetalleResp {
        val response = restPaqueteDetalle.getPaqueteDetalleId(TokenUtils.TOKEN_CONTENT, id)
        Log.i("REPO", response.toString())
        return response.body() ?: throw Exception("Detalle de paquete no encontrado")
    }

    override suspend fun insertarPaqueteDetalle(paqueteDetalle: PaqueteDetalleCreateDto): Boolean {
        Log.i("REPO", paqueteDetalle.toString())
        try {
            val response = restPaqueteDetalle.insertarPaqueteDetalle(
                TokenUtils.TOKEN_CONTENT,
                paqueteDetalle
            )
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar detalle de paquete", e)
            return false
        }
    }

    override suspend fun modificarPaqueteDetalle(paqueteDetalle: PaqueteDetalleDto): Boolean {
        Log.i("REPO", paqueteDetalle.toString())
        val response = restPaqueteDetalle.actualizarPaqueteDetalle(
            TokenUtils.TOKEN_CONTENT,
            paqueteDetalle.idPaqueteDetalle,
            paqueteDetalle
        )
        Log.i("REPO", "Código de respuesta: ${response.code()}")
        return response.isSuccessful && response.body()?.idPaqueteDetalle != null
    }
    override suspend fun buscarPaqueteDetallesByPaqueteId(paqueteId: Long): List<PaqueteDetalleResp> {
        try {
            // Primero obtenemos todos los detalles
            val allDetalles = reportarPaqueteDetalles()
            // Filtramos por ID de paquete
            return allDetalles.filter { it.paquete.idPaquete == paqueteId }
        } catch (e: Exception) {
            Log.e("REPO", "Error al buscar detalles por ID de paquete", e)
            return emptyList()
        }
    }
}