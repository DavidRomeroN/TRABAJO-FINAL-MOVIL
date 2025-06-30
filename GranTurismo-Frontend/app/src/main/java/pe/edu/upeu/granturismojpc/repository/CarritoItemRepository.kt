package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upeu.granturismojpc.data.remote.RestCarrito
import pe.edu.upeu.granturismojpc.data.remote.RestCarritoItem
import pe.edu.upeu.granturismojpc.model.CarritoItemCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoItemDto
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface CarritoItemRepository {
    suspend fun deleteCarritoItem(carritoItem: CarritoItemDto): Boolean
    suspend fun reportarCarritoItems(): List<CarritoItemResp>
    suspend fun reportarCarritoItemsPorCarrito(): List<CarritoItemResp>
    suspend fun buscarCarritoItemId(id: Long): CarritoItemResp
    suspend fun buscarCarritoItemPorTipo(id: Long, tipo: String): CarritoItemResp
    suspend fun insertarCarritoItem(carritoItem: CarritoItemCreateDto): Boolean
    suspend fun modificarCarritoItem(carritoItem: CarritoItemDto): Boolean
}

class CarritoItemRepositoryImp @Inject constructor(
    private val restCarritoItem: RestCarritoItem,
    private val restCarrito: RestCarrito,
): CarritoItemRepository {

    override suspend fun deleteCarritoItem(carritoItem: CarritoItemDto): Boolean {
        val response =
            restCarritoItem.deleteCarritoItem(TokenUtils.TOKEN_CONTENT, carritoItem.idCarritoItem)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarCarritoItems(): List<CarritoItemResp> {
        val response =
            restCarritoItem.reportarCarritoItem(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }
    override suspend fun reportarCarritoItemsPorCarrito(): List<CarritoItemResp> {
        val carritoResponse = restCarrito.getCarritoPorUsuario(TokenUtils.TOKEN_CONTENT, TokenUtils.USER_ID)

        val carritoBody = carritoResponse.body()
        if (!carritoResponse.isSuccessful || carritoBody == null) {
            return emptyList()
        }

        val response = restCarritoItem.buscarItemsPorCarrito(TokenUtils.TOKEN_CONTENT, carritoBody.idCarrito)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarCarritoItemId(id: Long): CarritoItemResp {
        val response =
            restCarritoItem.getCarritoItemId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("CarritoItem no encontrado")
    }

    override suspend fun buscarCarritoItemPorTipo(id: Long, tipo: String): CarritoItemResp {
        val carrito = restCarrito.getCarritoPorUsuario(TokenUtils.TOKEN_CONTENT, TokenUtils.USER_ID)
        val response =
            restCarritoItem.getCarritoItemPorTipo(TokenUtils.TOKEN_CONTENT,
                carrito.body()?.idCarrito!!,id,tipo)
        return response.body() ?: throw Exception("CarritoItem no encontrado")
    }

    override suspend fun insertarCarritoItem(carritoItem: CarritoItemCreateDto): Boolean {
        Log.i("REPO", carritoItem.toString())
        try {
            val response = restCarritoItem.insertarCarritoItem(TokenUtils.TOKEN_CONTENT, carritoItem)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar carritoItem", e)
            return false
        }
    }

    override suspend fun modificarCarritoItem(carritoItem: CarritoItemDto): Boolean {
        Log.i("REPO M", carritoItem.toString())
        val response =
            restCarritoItem.actualizarCarritoItem(TokenUtils.TOKEN_CONTENT, carritoItem.idCarritoItem, carritoItem)
        return response.isSuccessful && response.body()?.idCarritoItem != null
    }
}