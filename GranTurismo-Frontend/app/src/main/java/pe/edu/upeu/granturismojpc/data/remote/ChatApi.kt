package pe.edu.upeu.granturismojpc.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pe.edu.upeu.granturismojpc.model.ChatMessage
import pe.edu.upeu.granturismojpc.model.Conversacion
import pe.edu.upeu.granturismojpc.model.SesionDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {

    @GET("/api/chat/sesiones/{id}/mensajes")
    suspend fun obtenerMensajesPorSesion(
        @Header("Authorization") token: String,
        @Path("id") sesionId: Long
    ): List<ChatMessage>

    @GET("/api/chat/sesiones/activa")
    suspend fun obtenerSesionActiva(
        @Header("Authorization") token: String
    ): SesionDTO

    @GET("/api/chat/sesiones")
    suspend fun listarSesiones(@Header("Authorization") token: String): List<SesionDTO>

    @POST("/api/chat/sesiones")
    suspend fun crearSesion(@Header("Authorization") token: String): SesionDTO

    @DELETE("/api/chat/sesiones/{id}")
    suspend fun eliminarSesion(
        @Header("Authorization") token: String,
        @Path("id") sesionId: Long
    )


    object ApiClient {
        val chatApi: ChatApi by lazy {
            // Interceptor para mostrar el cuerpo de la respuesta (incluye JSON)
            val logging = HttpLoggingInterceptor { message ->
                println("📦 JSON recibido: $message")
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Cliente OkHttp con el interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // Gson configurado como "lenient" para tolerar formatos relajados
            val gson = GsonBuilder()
                .setLenient()
                .create()

            Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ChatApi::class.java)
        }
    }
}