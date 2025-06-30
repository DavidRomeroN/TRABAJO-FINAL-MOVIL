package pe.edu.upeu.granturismojpc.repository

import android.util.Log

import pe.edu.upeu.granturismojpc.data.remote.RestReserva
import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ReservaCreateDto
import pe.edu.upeu.granturismojpc.model.ReservaDto1
import pe.edu.upeu.granturismojpc.model.ReservaResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface ReservaRepository {
    suspend fun deleteReserva(reserva: ReservaDto1): Boolean
    suspend fun reportarReservas(): List<ReservaResp>
    suspend fun buscarReservaId(id: Long): ReservaResp
    suspend fun insertarReserva(reserva: ReservaCreateDto): Boolean
    suspend fun modificarReserva(reserva: ReservaDto1): Boolean
}

class ReservaRepositoryImp @Inject constructor(
    private val restReserva: RestReserva,
): ReservaRepository {

    override suspend fun deleteReserva(reserva: ReservaDto1): Boolean {
        val response = restReserva.deleteReserva(TokenUtils.TOKEN_CONTENT, reserva.idReserva)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarReservas(): List<ReservaResp> {
        val response = restReserva.reportarReservas(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarReservaId(id: Long): ReservaResp {
        val response = restReserva.getReservaId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Reserva no encontrada")
    }

    override suspend fun insertarReserva(reserva: ReservaCreateDto): Boolean {
        Log.i("RESERVA_REPO", reserva.toString())
        return try {
            val response = restReserva.insertarReserva(TokenUtils.TOKEN_CONTENT, reserva)
            Log.i("RESERVA_REPO", "Código de respuesta: ${response.code()}")
            if (response.isSuccessful) true
            else {
                Log.i("RESERVA_REPO", "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("RESERVA_REPO", "Error al insertar reserva", e)
            false
        }
    }

    override suspend fun modificarReserva(reserva: ReservaDto1): Boolean {
        val response = restReserva.actualizarReserva(TokenUtils.TOKEN_CONTENT, reserva.idReserva, reserva)
        return response.isSuccessful && response.body()?.idReserva != null
    }
}
