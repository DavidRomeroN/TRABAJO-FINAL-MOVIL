package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upeu.granturismojpc.data.remote.RestCarrito
import pe.edu.upeu.granturismojpc.model.CarritoCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoDto
import pe.edu.upeu.granturismojpc.model.CarritoResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface CarritoRepository {
    suspend fun deleteCarrito(carrito: CarritoDto): Boolean
    suspend fun reportarCarritos(): List<CarritoResp>
    suspend fun buscarCarritoId(id: Long): CarritoResp
    suspend fun buscarCarritoPorUsuario(): CarritoResp
    suspend fun insertarCarrito(carrito: CarritoCreateDto): Boolean
    suspend fun modificarCarrito(carrito: CarritoDto): Boolean
}

class CarritoRepositoryImp @Inject constructor(
    private val restCarrito: RestCarrito,
): CarritoRepository {

    override suspend fun deleteCarrito(carrito: CarritoDto): Boolean {
        val response =
            restCarrito.deleteCarrito(TokenUtils.TOKEN_CONTENT, carrito.idCarrito)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarCarritos(): List<CarritoResp> {
        val response =
            restCarrito.reportarCarrito(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarCarritoId(id: Long): CarritoResp {
        val response =
            restCarrito.getCarritoId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Carrito no encontrado")
    }

    override suspend fun buscarCarritoPorUsuario(): CarritoResp {
        val response =
            restCarrito.getCarritoPorUsuario(TokenUtils.TOKEN_CONTENT, TokenUtils.USER_ID)
        return response.body() ?: throw Exception("Carrito no encontrado")
    }

    override suspend fun insertarCarrito(carrito: CarritoCreateDto): Boolean {
        Log.i("REPO", carrito.toString())
        try {
            val response = restCarrito.insertarCarrito(TokenUtils.TOKEN_CONTENT, carrito)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar carrito", e)
            return false
        }
    }

    override suspend fun modificarCarrito(carrito: CarritoDto): Boolean {
        val response =
            restCarrito.actualizarCarrito(TokenUtils.TOKEN_CONTENT, carrito.idCarrito, carrito)
        return response.isSuccessful && response.body()?.idCarrito != null
    }
}