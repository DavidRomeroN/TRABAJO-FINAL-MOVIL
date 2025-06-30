package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import pe.edu.upeu.granturismojpc.data.remote.RestResena
import pe.edu.upeu.granturismojpc.model.ResenaCreateDto
import pe.edu.upeu.granturismojpc.model.ResenaDto
import pe.edu.upeu.granturismojpc.model.ResenaResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface ResenaRepository {
    suspend fun deleteResena(resena: ResenaDto): Boolean
    suspend fun reportarResenas(): List<ResenaResp>
    suspend fun buscarResenaId(id: Long): ResenaResp
    suspend fun insertarResena(resena: ResenaCreateDto): Boolean
    suspend fun modificarResena(resena: ResenaDto): Boolean
}

class ResenaRepositoryImp @Inject constructor(
    private val restResena: RestResena,
): ResenaRepository {

    override suspend fun deleteResena(resena: ResenaDto): Boolean {
        val response =
            restResena.deleteResena(TokenUtils.TOKEN_CONTENT, resena.idResena)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarResenas(): List<ResenaResp> {
        val response =
            restResena.reportarResenas(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarResenaId(id: Long): ResenaResp {
        val response =
            restResena.getResenaId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Reseña no encontrada")
    }

    override suspend fun insertarResena(resena: ResenaCreateDto): Boolean {
        Log.i("REPO", resena.toString())
        try {
            val response = restResena.insertarResena(TokenUtils.TOKEN_CONTENT, resena)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar reseña", e)
            return false
        }
    }

    override suspend fun modificarResena(resena: ResenaDto): Boolean {
        val response =
            restResena.actualizarResena(TokenUtils.TOKEN_CONTENT, resena.idResena, resena)
        return response.isSuccessful && response.body()?.idResena != null
    }
}