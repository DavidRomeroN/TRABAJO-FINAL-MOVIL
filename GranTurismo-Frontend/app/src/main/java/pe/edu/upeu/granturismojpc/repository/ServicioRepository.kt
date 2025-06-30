package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upeu.granturismojpc.data.remote.RestServicio
import pe.edu.upeu.granturismojpc.model.ServicioCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioDto
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface ServicioRepository {
    suspend fun deleteServicio(servicio: ServicioDto): Boolean
    suspend fun reportarServicios(): List<ServicioResp>
    suspend fun buscarServicioId(id: Long): ServicioResp
    suspend fun insertarServicio(servicio: ServicioCreateDto): Boolean
    suspend fun modificarServicio(servicio: ServicioDto): Boolean
}

class ServicioRepositoryImp @Inject constructor(
    private val restServicio: RestServicio,
): ServicioRepository {

    override suspend fun deleteServicio(servicio: ServicioDto): Boolean {
        val response =
            restServicio.deleteServicio(TokenUtils.TOKEN_CONTENT, servicio.idServicio)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarServicios(): List<ServicioResp> {
        val response =
            restServicio.reportarServicios(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarServicioId(id: Long): ServicioResp {
        val response =
            restServicio.getServicioId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Servicio no encontrado")
    }

    override suspend fun insertarServicio(servicio: ServicioCreateDto): Boolean {
        Log.i("REPO", servicio.toString())
        try {
            val response = restServicio.insertarServicio(TokenUtils.TOKEN_CONTENT, servicio)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar servicio", e)
            return false
        }
    }

    override suspend fun modificarServicio(servicio: ServicioDto): Boolean {
        val response =
            restServicio.actualizarServicio(TokenUtils.TOKEN_CONTENT, servicio.idServicio, servicio)
        return response.isSuccessful && response.body()?.idServicio != null
    }
}