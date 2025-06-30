package pe.edu.upeu.granturismojpc.data.remote

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.PaqueteCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteDto
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteResponse
import pe.edu.upeu.granturismojpc.utils.LocalizedResponse
import pe.edu.upeu.granturismojpc.utils.PageableResponse
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
import java.io.File

interface RestPaquete {
    @GET("${BASE_PACK}")
    suspend fun reportarPaquete(@Header("Authorization")
                                token:String): Response<List<PaqueteResp>>
    @GET("${BASE_PACK}/{id}")
    suspend fun getPaqueteId(@Header("Authorization")
                             token:String, @Path("id") id:Long): Response<PaqueteResponse>
    @DELETE("${BASE_PACK}/{id}")
    suspend fun deletePaquete(@Header("Authorization")
                              token:String, @Path("id") id:Long): Response<Message>
    // Métodos CON multipart para crear y actualizar
    @GET("${BASE_PACK}/pageable")
    suspend fun getPaquetesPaginate(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageableResponse<LocalizedResponse<PaqueteResp>>>
    @Multipart
    @POST("${BASE_PACK}")
    suspend fun insertarPaquete(
        @Header("Authorization") token: String,
        @Part("dto") dto: RequestBody, // El DTO como JSON
        @Part imagenFile: MultipartBody.Part // La imagen
    ): Response<PaqueteResp>

    @Multipart
    @PUT("${BASE_PACK}/{id}")
    suspend fun actualizarPaquete(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part("dto") dto: RequestBody, // El DTO como JSON
        @Part imagenFile: MultipartBody.Part? = null // Imagen opcional en actualización
    ): Response<PaqueteResp>

    // Métodos alternativos para casos donde NO se envía imagen

    @PUT("${BASE_PACK}/{id}")
    suspend fun actualizarPaqueteSinImagen(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body paquete: PaqueteDto
    ): Response<PaqueteResp>

    // Método para actualizar solo la imagen
    @Multipart
    @PUT("${BASE_PACK}/{id}/imagen")
    suspend fun actualizarImagenPaquete(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part imagenFile: MultipartBody.Part
    ): Response<PaqueteResp>

    companion object {
        const val BASE_PACK = "/paquetes"
    }
}