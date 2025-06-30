package pe.edu.upeu.granturismojpc.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.edu.upeu.granturismojpc.model.ActividadCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDto
import pe.edu.upeu.granturismojpc.model.ActividadResp
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

interface RestActividad {
    companion object {
        const val BASE_RUTA = "/actividad"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarActividades(@Header("Authorization")
                                  token:String): Response<List<ActividadResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getActividadId(@Header("Authorization")
                              token:String, @Path("id") id:Long): Response<ActividadResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteActividad(@Header("Authorization")
                               token:String, @Path("id") id:Long): Response<Message>
    @Multipart
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarActividad(@Header("Authorization")
                                   token:String, @Path("id") id:Long,
                                    @Part("dto") dto: RequestBody,
                                    @Part imagenFile: MultipartBody.Part? = null): Response<ActividadResp>
    @Multipart
    @POST("${BASE_RUTA}")
    suspend fun insertarActividad(@Header("Authorization")
                                 token:String, @Part("dto") dto: RequestBody,
                                  @Part imagenFile: MultipartBody.Part): Response<ActividadResp>
}