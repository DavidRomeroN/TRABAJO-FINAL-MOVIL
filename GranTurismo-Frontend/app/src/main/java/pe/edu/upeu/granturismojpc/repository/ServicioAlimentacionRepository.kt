package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.granturismojpc.data.remote.RestServicioAlimentacion
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject
import javax.inject.Singleton

interface ServicioAlimentacionRepository {
    suspend fun deleteServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionDto): Boolean
    suspend fun reportarServicioAlimentaciones(): List<ServicioAlimentacionResp>
    suspend fun buscarServicioAlimentacionId(id: Long): ServicioAlimentacionResp
    suspend fun buscarServicioAlimentacionPorServicio(id: Long): ServicioAlimentacionResp?
    suspend fun insertarServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionCreateDto): Boolean
    suspend fun modificarServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionDto): Boolean
}

class ServicioAlimentacionRepositoryImp @Inject constructor(
    private val restServicioAlimentacion: RestServicioAlimentacion,
): ServicioAlimentacionRepository {

    override suspend fun deleteServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionDto): Boolean {
        val response =
            restServicioAlimentacion.deleteServicioAlimentacion(TokenUtils.TOKEN_CONTENT, servicioAlimentacion.idAlimentacion)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarServicioAlimentaciones(): List<ServicioAlimentacionResp> {
        val response =
            restServicioAlimentacion.reportarServicioAlimentacion(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarServicioAlimentacionId(id: Long): ServicioAlimentacionResp {
        val response =
            restServicioAlimentacion.getServicioAlimentacionId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("ServicioAlimentacion no encontrado")
    }

    override suspend fun buscarServicioAlimentacionPorServicio(idServicio: Long): ServicioAlimentacionResp? {
        val response = restServicioAlimentacion.getServicioAlimentacionByServicio(TokenUtils.TOKEN_CONTENT, idServicio)
        return if (response.isSuccessful) response.body() else null
    }

    override suspend fun insertarServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionCreateDto): Boolean {
        Log.i("REPO", servicioAlimentacion.toString())
        try {
            val response = restServicioAlimentacion.insertarServicioAlimentacion(TokenUtils.TOKEN_CONTENT, servicioAlimentacion)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar servicioAlimentacion", e)
            return false
        }
    }

    override suspend fun modificarServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionDto): Boolean {
        val response =
            restServicioAlimentacion.actualizarServicioAlimentacion(TokenUtils.TOKEN_CONTENT, servicioAlimentacion.idAlimentacion, servicioAlimentacion)
        return response.isSuccessful && response.body()?.idAlimentacion != null
    }
}
