package pe.edu.upeu.granturismojpc.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.edu.upeu.granturismojpc.model.ActividadDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleResp
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

interface RestActividadDetalle {
    companion object {
        const val BASE_RUTA = "/actividaddetalle"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarActividadDetalle(@Header("Authorization")
                                    token:String): Response<List<ActividadDetalleResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getActividadDetalleId(@Header("Authorization")
                               token:String, @Path("id") id:Long): Response<ActividadDetalleResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteActividadDetalle(@Header("Authorization")
                                token:String, @Path("id") id:Long): Response<Message>
    @Multipart
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarActividadDetalle(@Header("Authorization")
                                               token:String, @Path("id") id:Long,
                                           @Part("dto") dto: RequestBody,
                                           @Part imagenFile: MultipartBody.Part? = null): Response<ActividadDetalleResp>
    @Multipart
    @POST("${BASE_RUTA}")
    suspend fun insertarActividadDetalle(@Header("Authorization")
                                             token:String, @Part("dto") dto: RequestBody,
                                         @Part imagenFile: MultipartBody.Part): Response<ActividadDetalleResp>
}