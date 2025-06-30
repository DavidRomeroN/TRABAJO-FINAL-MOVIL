package pe.edu.upeu.granturismojpc.data.remote

import pe.edu.upeu.granturismojpc.model.Message
import pe.edu.upeu.granturismojpc.model.ProveedorCreateDto
import pe.edu.upeu.granturismojpc.model.ProveedorDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestProveedor {
    companion object {
        const val BASE_RUTA = "/proveedores"
    }
    @GET("${BASE_RUTA}")
    suspend fun reportarProveedores(@Header("Authorization")
                                   token:String): Response<List<ProveedorResp>>
    @GET("${BASE_RUTA}/{id}")
    suspend fun getProveedorId(@Header("Authorization")
                             token:String, @Path("id") id:Long): Response<ProveedorResp>
    @DELETE("${BASE_RUTA}/{id}")
    suspend fun deleteProveedor(@Header("Authorization")
                              token:String, @Path("id") id:Long): Response<Message>
    @PUT("${BASE_RUTA}/{id}")
    suspend fun actualizarProveedor(@Header("Authorization")
                                  token:String, @Path("id") id:Long, @Body Proveedor:
                                    ProveedorDto): Response<ProveedorResp>
    @POST("${BASE_RUTA}")
    suspend fun insertarProveedor(@Header("Authorization")
                                token:String, @Body Proveedor: ProveedorCreateDto): Response<Message>
}