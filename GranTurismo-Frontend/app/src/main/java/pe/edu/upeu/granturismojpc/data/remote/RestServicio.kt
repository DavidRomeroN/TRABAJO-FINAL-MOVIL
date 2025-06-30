package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ServicioCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioDto
import pe.edu.upeu.granturismojpc.model.ServicioResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestServicio {
    companion object {
        const val BASE_RUTA = "/servicios"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarServicios(@Header("Authorization")
                                    token:String): Response<List<ServicioResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getServicioId(@Header("Authorization")
                               token:String, @Path("id") id:Long): Response<ServicioResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteServicio(@Header("Authorization")
                                token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarServicio(@Header("Authorization")
                                    token:String, @Path("id") id:Long, @Body Servicio:
                                    ServicioDto): Response<ServicioResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarServicio(@Header("Authorization")
                                  token:String, @Body Servicio: ServicioCreateDto): Response<Message>
}