package pe.edu.upeu.granturismojpc.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import pe.edu.upeu.granturismojpc.data.remote.RestActividad
import pe.edu.upeu.granturismojpc.model.ActividadCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDto
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.utils.MultipartUtils
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.io.File
import javax.inject.Inject

interface ActividadRepository {
    suspend fun deleteActividad(actividad: ActividadDto): Boolean
    suspend fun reportarActividades(): List<ActividadResp>
    suspend fun buscarActividadId(id: Long): ActividadResp
    // Métodos con archivos - CREATE
    suspend fun insertarActividadConImagen(actividad: ActividadCreateDto, imageFile: File): Boolean
    suspend fun insertarActividadConImagenUri(actividad: ActividadCreateDto, imageUri: Uri, context: Context?): Boolean
    suspend fun insertarActividadSinImagen(actividad: ActividadCreateDto, context: Context?): Boolean

    // Métodos con archivos - UPDATE
    suspend fun modificarActividadConImagen(actividad: ActividadDto, imageFile: File): Boolean
    suspend fun modificarActividadConImagenUri(actividad: ActividadDto, imageUri: Uri, context: Context?): Boolean
    suspend fun modificarActividadSinImagen(actividad: ActividadDto): Boolean
}

class ActividadRepositoryImp @Inject constructor(
    private val restActividad: RestActividad,
): ActividadRepository {
    companion object {
        private const val TAG = "PaqueteRepository"
    }
    override suspend fun deleteActividad(actividad: ActividadDto): Boolean {
        val response =
            restActividad.deleteActividad(TokenUtils.TOKEN_CONTENT, actividad.idActividad)
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarActividades(): List<ActividadResp> {
        val response =
            restActividad.reportarActividades(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarActividadId(id: Long): ActividadResp {
        val response =
            restActividad.getActividadId(TokenUtils.TOKEN_CONTENT, id)
        return response.body() ?: throw Exception("Actividad no encontrado")
    }

    override suspend fun insertarActividadConImagen(actividad: ActividadCreateDto, imageFile: File): Boolean {
        Log.i(TAG, "Insertando actividad con imagen: $actividad")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividad)
            val imagePart = MultipartUtils.createImagePart(imageFile)

            val response = restActividad.insertarActividad(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "Actividad insertada exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar actividad: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar actividad con imagen", e)
            false
        }
    }

    override suspend fun insertarActividadConImagenUri(actividad: ActividadCreateDto, imageUri: Uri, context: Context?): Boolean {
        Log.i(TAG, "Insertando actividad con imagen URI: $actividad")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividad)
            val imagePart = MultipartUtils.createImagePartFromUri(context, imageUri)

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restActividad.insertarActividad(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "Actividad insertada exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar actividad: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar actividad con imagen URI", e)
            false
        }
    }
    override suspend fun insertarActividadSinImagen(actividad: ActividadCreateDto, context: Context?): Boolean {
        Log.i(TAG, "Insertando actividad con imagen URI: $actividad")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividad)
            val imagePart = MultipartUtils.createImagePart(getDefaultImageFile(context!!))

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restActividad.insertarActividad(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "Actividad insertada exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar actividad: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar actividad con imagen URI", e)
            false
        }
    }

    override suspend fun modificarActividadConImagen(actividad: ActividadDto, imageFile: File): Boolean {
        Log.i(TAG, "Modificando actividad con imagen: ${actividad.idActividad}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividad)
            val imagePart = MultipartUtils.createImagePart(imageFile)

            val response = restActividad.actualizarActividad(
                token = TokenUtils.TOKEN_CONTENT,
                id = actividad.idActividad,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            val isSuccess = response.isSuccessful && response.body()?.idActividad != null

            if (isSuccess) {
                Log.i(TAG, "Actividad modificado exitosamente")
            } else {
                Log.e(TAG, "Error al modificar actividad: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar actividad con imagen", e)
            false
        }
    }

    override suspend fun modificarActividadConImagenUri(actividad: ActividadDto, imageUri: Uri, context: Context?): Boolean {
        Log.i(TAG, "Modificando actividad con imagen URI: ${actividad.idActividad}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividad)
            val imagePart = MultipartUtils.createImagePartFromUri(context, imageUri)

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restActividad.actualizarActividad(
                token = TokenUtils.TOKEN_CONTENT,
                id = actividad.idActividad,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            val isSuccess = response.isSuccessful && response.body()?.idActividad != null

            if (isSuccess) {
                Log.i(TAG, "Actividad modificado exitosamente")
            } else {
                Log.e(TAG, "Error al modificar actividad: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar actividad con imagen URI", e)
            false
        }
    }

    override suspend fun modificarActividadSinImagen(actividad: ActividadDto): Boolean {
        Log.i(TAG, "Modificando actividad sin cambiar imagen: ${actividad.idActividad}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividad)

            val response = restActividad.actualizarActividad(
                token = TokenUtils.TOKEN_CONTENT,
                id = actividad.idActividad,
                dto = dtoRequestBody,
                imagenFile = null
            )

            val isSuccess = response.isSuccessful && response.body()?.idActividad != null

            if (isSuccess) {
                Log.i(TAG, "Actividad modificado exitosamente (sin imagen)")
            } else {
                Log.e(TAG, "Error al modificar actividad: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar actividad sin imagen", e)
            false
        }
    }
}