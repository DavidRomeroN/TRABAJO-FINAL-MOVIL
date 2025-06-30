package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ResenaCreateDto
import pe.edu.upeu.granturismojpc.model.ResenaDto
import pe.edu.upeu.granturismojpc.model.ResenaResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestResena {
    companion object {
        const val BASE_RUTA = "/resenas"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarResenas(@Header("Authorization")
                                    token:String): Response<List<ResenaResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getResenaId(@Header("Authorization")
                               token:String, @Path("id") id:Long): Response<ResenaResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteResena(@Header("Authorization")
                                token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarResena(@Header("Authorization")
                                    token:String, @Path("id") id:Long, @Body Resena:
                                    ResenaDto): Response<ResenaResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarResena(@Header("Authorization")
                                  token:String, @Body Resena: ResenaCreateDto): Response<Void>
}