package pe.edu.upeu.granturismojpc.data.remote

import okhttp3.ResponseBody
import pe.edu.upeu.granturismojpc.model.UsuarioDto
import pe.edu.upeu.granturismojpc.model.UsuarioRegisterDto
import pe.edu.upeu.granturismojpc.model.UsuarioResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RestUsuario {
    @POST("/users/login")
    suspend fun login(@Body user: UsuarioDto):
            Response<UsuarioResp>
    @POST("/users/register")
    suspend fun register(@Body user: UsuarioRegisterDto):
            Response<ResponseBody>
}