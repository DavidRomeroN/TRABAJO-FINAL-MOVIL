package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.data.remote.RestFavorito.Companion.BASE_RUTA
import pe.edu.upeu.granturismojpc.model.CarritoCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoDto
import pe.edu.upeu.granturismojpc.model.CarritoResp
import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.UserConfig
import pe.edu.upeu.granturismojpc.model.UserConfigUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RestPreference {
    companion object {
        const val BASE_RUTA = "/preferences"
    }
    @GET("${BASE_RUTA}/me")
    suspend fun getMyPreferences(
        @Header("Authorization") token: String
    ): Response<UserConfig>

    @PUT("${BASE_RUTA}/me")
    suspend fun updateMyPreferences(
        @Header("Authorization") token: String,
        @Body updateDto: UserConfigUpdateDto
    ): Response<UserConfig>

    @PATCH("${BASE_RUTA}/me/currency")
    suspend fun updateMyCurrency(
        @Header("Authorization") token: String,
        @Query("currencyCode") currencyCode: String
    ): Response<UserConfig>

    @PATCH("${BASE_RUTA}/me/language")
    suspend fun updateMyLanguage(
        @Header("Authorization") token: String,
        @Query("languageCode") languageCode: String
    ): Response<UserConfig>
    @DELETE("${BASE_RUTA}/me")
    suspend fun deleteMyPreferences(
        @Header("Authorization") token: String
    ): Response<Unit> // HTTP 204 (sin contenido)

    @POST("${BASE_RUTA}/me/default")
    suspend fun createMyDefaultPreferences(
        @Header("Authorization") token: String
    ): Response<UserConfig> // HTTP 201 (creado)

    @GET("${BASE_RUTA}/me/exists")
    suspend fun checkMyPreferencesExist(
        @Header("Authorization") token: String
    ): Response<Map<String, Boolean>> // {"hasPreferences": true/false}
}