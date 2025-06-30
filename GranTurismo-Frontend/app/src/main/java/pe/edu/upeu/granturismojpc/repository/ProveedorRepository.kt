package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import pe.edu.upeu.granturismojpc.data.remote.RestProveedor
import pe.edu.upeu.granturismojpc.model.ProveedorCreateDto
import pe.edu.upeu.granturismojpc.model.ProveedorDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.toCreateDto
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

interface ProveedorRepository {
    suspend fun deleteProveedor(proveedor: ProveedorDto): Boolean
    suspend fun reportarProveedores(): List<ProveedorResp>
    suspend fun buscarProveedorId(id: Long): ProveedorResp
    suspend fun insertarProveedor(proveedor: ProveedorCreateDto): Boolean
    suspend fun modificarProveedor(proveedor: ProveedorDto): Boolean
}

class ProveedorRepositoryImp @Inject constructor(
    private val restProveedor: RestProveedor,
): ProveedorRepository {

    override suspend fun deleteProveedor(proveedor: ProveedorDto): Boolean {
        val response =
            restProveedor.deleteProveedor(TokenUtils.TOKEN_CONTENT, proveedor.idProveedor)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarProveedores(): List<ProveedorResp> {
        val response =
            restProveedor.reportarProveedores(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarProveedorId(id: Long): ProveedorResp {
        val response =
            restProveedor.getProveedorId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Proveedor no encontrado")
    }

    override suspend fun insertarProveedor(proveedor: ProveedorCreateDto): Boolean {
        Log.i("REPO", proveedor.toString())
        try {
            val response = restProveedor.insertarProveedor(TokenUtils.TOKEN_CONTENT, proveedor)
            Log.i("REPO", "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                return true
            }

            Log.i("REPO", "ErrorBody: ${response.errorBody()?.string()}")
            return false
        } catch (e: Exception) {
            Log.e("REPO", "Error al insertar proveedor", e)
            return false
        }
    }

    override suspend fun modificarProveedor(proveedor: ProveedorDto): Boolean {
        val response =
            restProveedor.actualizarProveedor(TokenUtils.TOKEN_CONTENT, proveedor.idProveedor, proveedor)
        return response.isSuccessful && response.body()?.idProveedor != null
    }
}