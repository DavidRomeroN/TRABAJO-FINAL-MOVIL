package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.TipoServicioCreateDto
import pe.edu.upeu.granturismojpc.model.TipoServicioDto
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import pe.edu.upeu.granturismojpc.model.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestTipoServicio {
    companion object {
        const val BASE_RUTA = "/tipos"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarTipoServicio(@Header("Authorization")
                                 token:String): Response<List<TipoServicioResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getTipoServicioId(@Header("Authorization")
                             token:String, @Path("id") id:Long): Response<TipoServicioResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteTipoServicio(@Header("Authorization")
                              token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarTipoServicio(@Header("Authorization")
                                  token:String, @Path("id") id:Long, @Body TipoServicio:
                                  TipoServicioDto): Response<TipoServicioResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarTipoServicio(@Header("Authorization")
                                token:String, @Body TipoServicio: TipoServicioCreateDto): Response<Message>
}