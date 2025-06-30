package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.data.local.dao.FavoritoDao
import pe.edu.upeu.granturismojpc.data.remote.RestFavorito
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.FavoritoDto
import pe.edu.upeu.granturismojpc.model.FavoritoResp
import pe.edu.upeu.granturismojpc.model.toRoom
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import pe.edu.upeu.granturismojpc.utils.isNetworkAvailable
import javax.inject.Inject

interface FavoritoRepository {
    suspend fun findAll(): List<FavoritoResp>
    suspend fun findAllR(): Flow<List<Favorito>>
    suspend fun saveFavorito(favorito: FavoritoCreateDto)
    suspend fun deleteFavorito(favorito: FavoritoDto): Boolean
    suspend fun getFavoritoById(id: Long): Flow<Favorito?>
    suspend fun obtenerFavoritosPorTipo(tipo: String): List<Favorito>
}

class FavoritoRepositoryImp @Inject constructor(
    private val rest: RestFavorito,
    private val dao: FavoritoDao,
): FavoritoRepository{
    override suspend fun findAll(): List<FavoritoResp> {
        val response =rest.reportarFavoritos(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?:emptyList() else emptyList()
    }
    override suspend fun findAllR(): Flow<List<Favorito>> {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                if (isNetworkAvailable(TokenUtils.CONTEXTO_APPX)) {
                    val response = rest.reportarFavoritos(TokenUtils.TOKEN_CONTENT)
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            val favoritos = data.map { it.toRoom() }
                            dao.insertarEntidadRegs(favoritos)
                        } ?: Log.e("ERROR", "El cuerpo de la respuesta es nulo")
                    } else {
                        Log.e("ERROR", "Respuesta no exitosa: ${response.code()}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Excepción en findAllR: ${e.message}", e)
        }
        return dao.reportatEntidad()
    }
    override suspend fun saveFavorito(favorito: FavoritoCreateDto) {
        Log.i("REPO", favorito.toString())
        try {
            if (isNetworkAvailable(TokenUtils.CONTEXTO_APPX)) {
                val response=rest.insertarFavorito(TokenUtils.TOKEN_CONTENT, favorito)
                Log.i("REPO", "Código de respuesta: ${response.code()}")
                dao.insertarEntidad(favorito.toRoom()) //prueba
            } else {
                dao.insertarEntidad(favorito.toRoom())
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error al guardar favorito: ${e.message}")
        }
    }
    override suspend fun deleteFavorito(favorito: FavoritoDto): Boolean {
        try {
            if (isNetworkAvailable(TokenUtils.CONTEXTO_APPX)) {
                val response = rest.deleteFavorito(TokenUtils.TOKEN_CONTENT, favorito.idFavorito)
                dao.eliminarEntidad(favorito.toRoom()) //prueba
                if (response.isSuccessful) {
                    dao.eliminarEntidad(favorito.toRoom())
                }
            } else {
                dao.eliminarEntidad(favorito.toRoom())

            }
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error al eliminar favorito: ${e.message}")
            return false
        }
    }
    override suspend fun getFavoritoById(id: Long): Flow<Favorito?> {
        return if (isNetworkAvailable(TokenUtils.CONTEXTO_APPX)) {
            try {
                val response = rest.getFavoritosId(TokenUtils.TOKEN_CONTENT, id)
                if (response.isSuccessful) {
                    response.body()?.let { dao.insertarEntidad(it.toRoom()) } // sincroniza con Room
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error al obtener favorito desde API: ${e.message}")
            }
            dao.buscarEntidad(id)
        } else {
            dao.buscarEntidad(id)
        }
    }
    override suspend fun obtenerFavoritosPorTipo(tipo: String): List<Favorito> {
        return try {
            if (isNetworkAvailable(TokenUtils.CONTEXTO_APPX)) {
                val response = rest.buscarFavoritosPorTipo(TokenUtils.TOKEN_CONTENT, TokenUtils.USER_ID, tipo)
                if (response.isSuccessful) {
                    val favoritosResp = response.body()!!
                    val favoritosRoom = favoritosResp.map { it.toRoom() }
                    dao.insertarEntidadRegs(favoritosRoom)
                    favoritosRoom
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error: ${e.message}")
            emptyList()
        }
    }
} 
