package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestServicioHotelera {
    companion object {
        const val BASE_RUTA = "/serviciohoteles"
    }

    @GET("${BASE_RUTA}")
    suspend fun reportarServicioHotelera(
        @Header("Authorization")
        token: String
    ): Response<List<ServicioHoteleraResp>>

    @GET("${BASE_RUTA}/{id}")
    suspend fun getServicioHoteleraId(
        @Header("Authorization")
        token: String, @Path("id") id: Long
    ): Response<ServicioHoteleraResp>

    @GET("${BASE_RUTA}/servicio/{id}")
    suspend fun getServicioHoteleraByServicio(
        @Header("Authorization")
        token: String, @Path("id") idServicio: Long
    ): Response<ServicioHoteleraResp>

    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteServicioHotelera(
        @Header("Authorization")
        token: String, @Path("id") id: Long
    ): Response<Message>

    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarServicioHotelera(
        @Header("Authorization")
        token: String, @Path("id") id: Long, @Body ServicioHotelera:
        ServicioHoteleraDto
    ): Response<ServicioHoteleraResp>

    @POST("${BASE_RUTA}")
    suspend fun insertarServicioHotelera(
        @Header("Authorization")
        token: String, @Body ServicioHotelera: ServicioHoteleraCreateDto
    ): Response<Message>
}