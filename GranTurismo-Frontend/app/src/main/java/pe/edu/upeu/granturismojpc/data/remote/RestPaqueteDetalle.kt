package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestPaqueteDetalle {
    companion object {
        const val BASE_RUTA = "/paquetedetalle"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarPaqueteDetalle(@Header("Authorization")
                                token:String): Response<List<PaqueteDetalleResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getPaqueteDetalleId(@Header("Authorization")
                            token:String, @Path("id") id:Long): Response<PaqueteDetalleResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deletePaqueteDetalle(@Header("Authorization")
                             token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarPaqueteDetalle(@Header("Authorization")
                                 token:String, @Path("id") id:Long, @Body PaqueteDetalle:
                                 PaqueteDetalleDto): Response<PaqueteDetalleResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarPaqueteDetalle(@Header("Authorization")
                               token:String, @Body PaqueteDetalle: PaqueteDetalleCreateDto): Response<Message>
}