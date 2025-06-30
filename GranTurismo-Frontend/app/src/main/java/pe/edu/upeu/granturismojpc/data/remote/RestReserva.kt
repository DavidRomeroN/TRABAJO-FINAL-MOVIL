package pe.edu.upeu.granturismojpc.data.remote


import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ReservaCreateDto
import pe.edu.upeu.granturismojpc.model.ReservaDto1
import pe.edu.upeu.granturismojpc.model.ReservaResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestReserva {
    companion object {
        const val BASE_RUTA = "/reservas"
    }

    @GET(BASE_RUTA)
    suspend fun reportarReservas(@Header("Authorization") token: String): Response<List<ReservaResp>>

    @GET("$BASE_RUTA/{id}")
    suspend fun getReservaId(@Header("Authorization") token: String, @Path("id") id: Long): Response<ReservaResp>

    @DELETE("$BASE_RUTA/{id}")
    suspend fun deleteReserva(@Header("Authorization") token: String, @Path("id") id: Long): Response<Message>

    @PUT("$BASE_RUTA/{id}")
    suspend fun actualizarReserva(@Header("Authorization") token: String, @Path("id") id: Long, @Body reserva: ReservaDto1): Response<ReservaResp>

    @POST(BASE_RUTA)
    suspend fun insertarReserva(@Header("Authorization") token: String, @Body reserva: ReservaCreateDto): Response<Message>
}
