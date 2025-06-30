package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestServicioArtesania {
    companion object {
        const val BASE_RUTA = "/servicioartesania"
    }

    @GET("${BASE_RUTA}")
    suspend fun reportarServicioArtesania(
        @Header("Authorization")
        token: String
    ): Response<List<ServicioArtesaniaResp>>

    @GET("${BASE_RUTA}/{id}")
    suspend fun getServicioArtesaniaId(
        @Header("Authorization")
        token: String, @Path("id") id: Long
    ): Response<ServicioArtesaniaResp>

    @GET("${BASE_RUTA}/servicio/{id}")
    suspend fun getServicioArtesaniaByServicio(
        @Header("Authorization")
        token: String, @Path("id") idServicio: Long
    ): Response<ServicioArtesaniaResp>

    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteServicioArtesania(
        @Header("Authorization")
        token: String, @Path("id") id: Long
    ): Response<Message>

    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarServicioArtesania(
        @Header("Authorization")
        token: String, @Path("id") id: Long, @Body ServicioArtesania:
        ServicioArtesaniaDto
    ): Response<ServicioArtesaniaResp>

    @POST("${BASE_RUTA}")
    suspend fun insertarServicioArtesania(
        @Header("Authorization")
        token: String, @Body ServicioArtesania: ServicioArtesaniaCreateDto
    ): Response<Message>
}