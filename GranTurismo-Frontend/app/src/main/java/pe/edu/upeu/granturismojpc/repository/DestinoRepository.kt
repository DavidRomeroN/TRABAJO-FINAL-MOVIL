package pe.edu.upeu.granturismojpc.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import jakarta.inject.Inject
import pe.edu.upeu.granturismojpc.data.remote.RestDestino
import pe.edu.upeu.granturismojpc.model.DestinoCreateDto
import pe.edu.upeu.granturismojpc.model.DestinoDto
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.utils.MultipartUtils
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.io.File

interface DestinoRepository{
    suspend fun deleteDestino(destino: DestinoDto): Boolean
    suspend fun reportarDestinos(): List<DestinoResp> // Cambiado
    suspend fun buscarDestinoId(id: Long): DestinoResp // Cambiado
    // Métodos con archivos - CREATE
    suspend fun insertarDestinoConImagen(destino: DestinoCreateDto, imageFile: File): Boolean
    suspend fun insertarDestinoConImagenUri(destino: DestinoCreateDto, imageUri: Uri, context: Context?): Boolean
    suspend fun insertarDestinoSinImagen(destino: DestinoCreateDto, context: Context?): Boolean

    // Métodos con archivos - UPDATE
    suspend fun modificarDestinoConImagen(destino: DestinoDto, imageFile: File): Boolean
    suspend fun modificarDestinoConImagenUri(destino: DestinoDto, imageUri: Uri, context: Context?): Boolean
    suspend fun modificarDestinoSinImagen(destino: DestinoDto): Boolean
}
class DestinoRepositoryImp @Inject constructor(
    private val restDestino: RestDestino,
    //private val actividadDao: ActividadDao,
): DestinoRepository{
    companion object {
        private const val TAG = "DestinoRepository"
    }
    override suspend fun deleteDestino(destino: DestinoDto): Boolean {
        val response =
            restDestino.deleteDestino(TokenUtils.TOKEN_CONTENT,
                destino.idDestino)
        return response.code() == 204 || response.isSuccessful
    }
    override suspend fun reportarDestinos(): List<DestinoResp> {
        val response =
            restDestino.reportarDestinos(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }
    override suspend fun buscarDestinoId(id: Long): DestinoResp {
        val response =
            restDestino.getDestinoId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Destino no encontrado")
    }
    override suspend fun insertarDestinoConImagen(destino: DestinoCreateDto, imageFile: File): Boolean {
        Log.i(TAG, "Insertando destino con imagen: $destino")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(destino)
            val imagePart = MultipartUtils.createImagePart(imageFile)

            val response = restDestino.insertarDestino(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "Destino insertado exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar destino: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar destino con imagen", e)
            false
        }
    }

    override suspend fun insertarDestinoConImagenUri(destino: DestinoCreateDto, imageUri: Uri, context: Context?): Boolean {
        Log.i(TAG, "Insertando destino con imagen URI: $destino")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(destino)
            val imagePart = MultipartUtils.createImagePartFromUri(context, imageUri)

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restDestino.insertarDestino(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "Destino insertado exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar destino: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar destino con imagen URI", e)
            false
        }
    }
    override suspend fun insertarDestinoSinImagen(destino: DestinoCreateDto, context: Context?): Boolean {
        Log.i(TAG, "Insertando destino con imagen URI: $destino")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(destino)
            val imagePart = MultipartUtils.createImagePart(getDefaultImageFile(context!!))

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restDestino.insertarDestino(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "Destino insertado exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar destino: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar destino con imagen URI", e)
            false
        }
    }

    override suspend fun modificarDestinoConImagen(destino: DestinoDto, imageFile: File): Boolean {
        Log.i(TAG, "Modificando destino con imagen: ${destino.idDestino}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(destino)
            val imagePart = MultipartUtils.createImagePart(imageFile)

            val response = restDestino.actualizarDestino(
                token = TokenUtils.TOKEN_CONTENT,
                id = destino.idDestino,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            val isSuccess = response.isSuccessful && response.body()?.idDestino != null

            if (isSuccess) {
                Log.i(TAG, "Destino modificado exitosamente")
            } else {
                Log.e(TAG, "Error al modificar destino: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar destino con imagen", e)
            false
        }
    }

    override suspend fun modificarDestinoConImagenUri(destino: DestinoDto, imageUri: Uri, context: Context?): Boolean {
        Log.i(TAG, "Modificando destino con imagen URI: ${destino.idDestino}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(destino)
            val imagePart = MultipartUtils.createImagePartFromUri(context, imageUri)

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restDestino.actualizarDestino(
                token = TokenUtils.TOKEN_CONTENT,
                id = destino.idDestino,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            val isSuccess = response.isSuccessful && response.body()?.idDestino != null

            if (isSuccess) {
                Log.i(TAG, "Destino modificado exitosamente")
            } else {
                Log.e(TAG, "Error al modificar destino: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar destino con imagen URI", e)
            false
        }
    }

    override suspend fun modificarDestinoSinImagen(destino: DestinoDto): Boolean {
        Log.i(TAG, "Modificando destino sin cambiar imagen: ${destino.idDestino}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(destino)

            val response = restDestino.actualizarDestino(
                token = TokenUtils.TOKEN_CONTENT,
                id = destino.idDestino,
                dto = dtoRequestBody,
                imagenFile = null
            )

            val isSuccess = response.isSuccessful && response.body()?.idDestino != null

            if (isSuccess) {
                Log.i(TAG, "Destino modificado exitosamente (sin imagen)")
            } else {
                Log.e(TAG, "Error al modificar destino: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar destino sin imagen", e)
            false
        }
    }
}