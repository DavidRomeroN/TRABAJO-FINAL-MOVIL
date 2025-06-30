package pe.edu.upeu.granturismojpc.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.edu.upeu.granturismojpc.model.CarritoCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoDto
import pe.edu.upeu.granturismojpc.model.CarritoResp
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

interface RestCarrito {
    companion object {
        const val BASE_RUTA = "/carrito"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarCarrito(@Header("Authorization")
                                     token:String): Response<List<CarritoResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getCarritoId(@Header("Authorization")
                                  token:String, @Path("id") id:Long): Response<CarritoResp>
    @GET("${BASE_RUTA}/buscar-por-usuario/{id}")
    suspend fun getCarritoPorUsuario(@Header("Authorization")
                             token:String, @Path("id") id:Long): Response<CarritoResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteCarrito(@Header("Authorization")
                                   token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarCarrito(@Header("Authorization")
                                       token:String, @Path("id") id:Long, @Body Carrito:
                                       CarritoDto): Response<CarritoResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarCarrito(@Header("Authorization")
                                     token:String, @Body Carrito: CarritoCreateDto): Response<Message>
}