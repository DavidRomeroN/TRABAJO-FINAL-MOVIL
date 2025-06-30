package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
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

interface RestServicioAlimentacion {
    companion object {
        const val BASE_RUTA = "/servicioalimento"
    }

    @GET("${BASE_RUTA}")
    suspend fun reportarServicioAlimentacion(
        @Header("Authorization")
        token: String
    ): Response<List<ServicioAlimentacionResp>>

    @GET("${BASE_RUTA}/{id}")
    suspend fun getServicioAlimentacionId(
        @Header("Authorization")
        token: String, @Path("id") id: Long
    ): Response<ServicioAlimentacionResp>

    @GET("${BASE_RUTA}/servicio/{id}")
    suspend fun getServicioAlimentacionByServicio(
        @Header("Authorization")
        token: String, @Path("id") idServicio: Long
    ): Response<ServicioAlimentacionResp>

    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteServicioAlimentacion(
        @Header("Authorization")
        token: String, @Path("id") id: Long
    ): Response<Message>

    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarServicioAlimentacion(
        @Header("Authorization")
        token: String, @Path("id") id: Long, @Body ServicioAlimentacion:
        ServicioAlimentacionDto
    ): Response<ServicioAlimentacionResp>

    @POST("${BASE_RUTA}")
    suspend fun insertarServicioAlimentacion(
        @Header("Authorization")
        token: String, @Body ServicioAlimentacion: ServicioAlimentacionCreateDto
    ): Response<Message>
}