package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.granturismojpc.data.remote.RestServicioHotelera
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject
import javax.inject.Singleton

interface ServicioHoteleraRepository {
    suspend fun deleteServicioHotelera(servicioHotelera: ServicioHoteleraDto): Boolean
    suspend fun reportarServicioHotelera(): List<ServicioHoteleraResp>
    suspend fun buscarServicioHoteleraId(id: Long): ServicioHoteleraResp
    suspend fun buscarServicioHoteleraPorServicio(idServicio: Long): ServicioHoteleraResp?
    suspend fun insertarServicioHotelera(servicioHotelera: ServicioHoteleraCreateDto): Boolean
    suspend fun modificarServicioHotelera(servicioHotelera: ServicioHoteleraDto): Boolean
}

class ServicioHoteleraRepositoryImp @Inject constructor(
    private val restServicioHotelera: RestServicioHotelera,
): ServicioHoteleraRepository {

    override suspend fun deleteServicioHotelera(servicioHotelera: ServicioHoteleraDto): Boolean {
        val response =
            restServicioHotelera.deleteServicioHotelera(TokenUtils.TOKEN_CONTENT, servicioHotelera.idHoteleria)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarServicioHotelera(): List<ServicioHoteleraResp> {
        val response =
            restServicioHotelera.reportarServicioHotelera(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarServicioHoteleraId(id: Long): ServicioHoteleraResp {
        val response =
            restServicioHotelera.getServicioHoteleraId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("ServicioHotelera no encontrado")
    }

    override suspend fun buscarServicioHoteleraPorServicio(idServicio: Long): ServicioHoteleraResp? {
        val response = restServicioHotelera.getServicioHoteleraByServicio(TokenUtils.TOKEN_CONTENT, idServicio)
        return if (response.isSuccessful) response.body() else null
    }

    override suspend fun insertarServicioHotelera(servicioHotelera: ServicioHoteleraCreateDto): Boolean {
        Log.i("REPO", servicioHotelera.toString())
        try {
            val response = restServicioHotelera.insertarServicioHotelera(TokenUtils.TOKEN_CONTENT, servicioHotelera)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar servicioHotelera", e)
            return false
        }
    }

    override suspend fun modificarServicioHotelera(servicioHotelera: ServicioHoteleraDto): Boolean {
        val response =
            restServicioHotelera.actualizarServicioHotelera(TokenUtils.TOKEN_CONTENT, servicioHotelera.idHoteleria, servicioHotelera)
        return response.isSuccessful && response.body()?.idHoteleria != null
    }
}