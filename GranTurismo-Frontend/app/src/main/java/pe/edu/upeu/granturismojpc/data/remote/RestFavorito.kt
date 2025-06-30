package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.FavoritoResp
import pe.edu.upeu.granturismojpc.model.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RestFavorito {
    companion object {
        const val BASE_RUTA = "/favoritos"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarFavoritos(@Header("Authorization") token:String):
            Response<List<FavoritoResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getFavoritosId(@Header("Authorization") token:String,
                            @Path("id") id:Long): Response<FavoritoResp>
    @GET("${BASE_RUTA}/buscar-por-tipo")
    suspend fun buscarFavoritosPorTipo(@Header("Authorization") token: String,
                                        @Query("usuarioId") usuarioId: Long,
                                        @Query("tipo") tipo: String
    ): Response<List<FavoritoResp>>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteFavorito(@Header("Authorization") token: String,
                               @Path("id") id: Long
    ): Response<Message>
    @POST("${BASE_RUTA}")
    suspend fun insertarFavorito(@Header("Authorization") token:String,
                              @Body favorito: FavoritoCreateDto): Response<Void>

}