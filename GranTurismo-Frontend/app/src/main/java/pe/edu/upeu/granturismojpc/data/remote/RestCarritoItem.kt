package pe.edu.upeu.granturismojpc.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.edu.upeu.granturismojpc.model.CarritoItemCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoItemDto
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RestCarritoItem {
    companion object {
        const val BASE_RUTA = "/carritoitem"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarCarritoItem(@Header("Authorization")
                                     token:String): Response<List<CarritoItemResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getCarritoItemId(@Header("Authorization")
                                  token:String, @Path("id") id:Long): Response<CarritoItemResp>
    @GET("${BASE_RUTA}/buscar-por-tipo")
    suspend fun getCarritoItemPorTipo(@Header("Authorization") token: String,
                                       @Query("carritoId") carritoId: Long,
                                       @Query("referenciaId") referenciaId: Long,
                                       @Query("tipo") tipo: String,
    ): Response<CarritoItemResp>
    @GET("${BASE_RUTA}/buscar-por-carrito")
    suspend fun buscarItemsPorCarrito(@Header("Authorization") token: String,
                                      @Query("carritoId") carritoId: Long
    ): Response<List<CarritoItemResp>>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteCarritoItem(@Header("Authorization")
                                   token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarCarritoItem(@Header("Authorization")
                                       token:String, @Path("id") id:Long, @Body CarritoItem:
                                       CarritoItemDto): Response<CarritoItemResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarCarritoItem(@Header("Authorization")
                                     token:String, @Body CarritoItem: CarritoItemCreateDto): Response<Void>
}