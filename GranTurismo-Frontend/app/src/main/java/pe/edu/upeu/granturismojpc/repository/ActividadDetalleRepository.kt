package pe.edu.upeu.granturismojpc.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import pe.edu.upeu.granturismojpc.data.remote.RestActividadDetalle
import pe.edu.upeu.granturismojpc.model.ActividadCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleResp
import pe.edu.upeu.granturismojpc.model.ActividadDto
import pe.edu.upeu.granturismojpc.utils.MultipartUtils
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.io.File
import javax.inject.Inject

interface ActividadDetalleRepository {
    suspend fun deleteActividadDetalle(actividadDetalle: ActividadDetalleDto): Boolean
    suspend fun deleteActividadDetalleId(id: Long): Boolean
    suspend fun reportarActividadDetalles(): List<ActividadDetalleResp>
    suspend fun buscarActividadDetalleId(id: Long): ActividadDetalleResp
    suspend fun buscarActividadDetallesByPaqueteId(paqueteId: Long): List<ActividadDetalleResp>
    // Métodos con archivos - CREATE
    suspend fun insertarActividadDetalleConImagen(actividadDetalle: ActividadDetalleCreateDto, imageFile: File): Boolean
    suspend fun insertarActividadDetalleConImagenUri(actividadDetalle: ActividadDetalleCreateDto, imageUri: Uri, context: Context?): Boolean
    suspend fun insertarActividadDetalleSinImagen(actividadDetalle: ActividadDetalleCreateDto, context: Context?): Boolean

    // Métodos con archivos - UPDATE
    suspend fun modificarActividadDetalleConImagen(actividadDetalle: ActividadDetalleDto, imageFile: File): Boolean
    suspend fun modificarActividadDetalleConImagenUri(actividadDetalle: ActividadDetalleDto, imageUri: Uri, context: Context?): Boolean
    suspend fun modificarActividadDetalleSinImagen(actividadDetalle: ActividadDetalleDto): Boolean
}

class ActividadDetalleRepositoryImpl @Inject constructor(
    private val restActividadDetalle: RestActividadDetalle
): ActividadDetalleRepository {
    companion object {
        private const val TAG = "PaqueteDetalleRepository"
    }
    override suspend fun deleteActividadDetalle(actividadDetalle: ActividadDetalleDto): Boolean {
        val response = restActividadDetalle.deleteActividadDetalle(
            TokenUtils.TOKEN_CONTENT,
            actividadDetalle.idActividadDetalle
        )
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun deleteActividadDetalleId(id: Long): Boolean {
        val response = restActividadDetalle.deleteActividadDetalle(
            TokenUtils.TOKEN_CONTENT,
            id
        )
        return response.code() == 204 || response.isSuccessful
    }

    override suspend fun reportarActividadDetalles(): List<ActividadDetalleResp> {
        val response = restActividadDetalle.reportarActividadDetalle(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    override suspend fun buscarActividadDetalleId(id: Long): ActividadDetalleResp {
        val response = restActividadDetalle.getActividadDetalleId(TokenUtils.TOKEN_CONTENT, id)
        Log.i("REPO", response.toString())
        return response.body() ?: throw Exception("Detalle de actividad no encontrado")
    }

    override suspend fun insertarActividadDetalleConImagen(actividadDetalle: ActividadDetalleCreateDto, imageFile: File): Boolean {
        Log.i(TAG, "Insertando actividadDetalle con imagen: $actividadDetalle")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividadDetalle)
            val imagePart = MultipartUtils.createImagePart(imageFile)

            val response = restActividadDetalle.insertarActividadDetalle(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "ActividadDetalle insertado exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar actividadDetalle: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar actividadDetalle con imagen", e)
            false
        }
    }

    override suspend fun insertarActividadDetalleConImagenUri(actividadDetalle: ActividadDetalleCreateDto, imageUri: Uri, context: Context?): Boolean {
        Log.i(TAG, "Insertando actividadDetalle con imagen URI: $actividadDetalle")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividadDetalle)
            val imagePart = MultipartUtils.createImagePartFromUri(context, imageUri)

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restActividadDetalle.insertarActividadDetalle(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "ActividadDetalle insertado exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar actividadDetalle: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar actividadDetalle con imagen URI", e)
            false
        }
    }
    override suspend fun insertarActividadDetalleSinImagen(actividadDetalle: ActividadDetalleCreateDto, context: Context?): Boolean {
        Log.i(TAG, "Insertando actividadDetalle con imagen URI: $actividadDetalle")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividadDetalle)
            val imagePart = MultipartUtils.createImagePart(getDefaultImageFile(context!!))

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restActividadDetalle.insertarActividadDetalle(
                token = TokenUtils.TOKEN_CONTENT,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            Log.i(TAG, "Código de respuesta: ${response.code()}")

            if (response.isSuccessful) {
                Log.i(TAG, "ActividadDetalle insertado exitosamente")
                true
            } else {
                Log.e(TAG, "Error al insertar actividadDetalle: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al insertar actividadDetalle con imagen URI", e)
            false
        }
    }

    override suspend fun modificarActividadDetalleConImagen(actividadDetalle: ActividadDetalleDto, imageFile: File): Boolean {
        Log.i(TAG, "Modificando actividadDetalle con imagen: ${actividadDetalle.idActividadDetalle}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividadDetalle)
            val imagePart = MultipartUtils.createImagePart(imageFile)

            val response = restActividadDetalle.actualizarActividadDetalle(
                token = TokenUtils.TOKEN_CONTENT,
                id = actividadDetalle.idActividadDetalle,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            val isSuccess = response.isSuccessful && response.body()?.idActividadDetalle != null

            if (isSuccess) {
                Log.i(TAG, "ActividadDetalle modificado exitosamente")
            } else {
                Log.e(TAG, "Error al modificar actividadDetalle: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar actividadDetalle con imagen", e)
            false
        }
    }

    override suspend fun modificarActividadDetalleConImagenUri(actividadDetalle: ActividadDetalleDto, imageUri: Uri, context: Context?): Boolean {
        Log.i(TAG, "Modificando actividadDetalle con imagen URI: ${actividadDetalle.idActividadDetalle}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividadDetalle)
            val imagePart = MultipartUtils.createImagePartFromUri(context, imageUri)

            if (imagePart == null) {
                Log.e(TAG, "No se pudo crear la parte de imagen desde URI")
                return false
            }

            val response = restActividadDetalle.actualizarActividadDetalle(
                token = TokenUtils.TOKEN_CONTENT,
                id = actividadDetalle.idActividadDetalle,
                dto = dtoRequestBody,
                imagenFile = imagePart
            )

            val isSuccess = response.isSuccessful && response.body()?.idActividadDetalle != null

            if (isSuccess) {
                Log.i(TAG, "ActividadDetalle modificado exitosamente")
            } else {
                Log.e(TAG, "Error al modificar actividadDetalle: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar actividadDetalle con imagen URI", e)
            false
        }
    }

    override suspend fun modificarActividadDetalleSinImagen(actividadDetalle: ActividadDetalleDto): Boolean {
        Log.i(TAG, "Modificando actividadDetalle sin cambiar imagen: ${actividadDetalle.idActividadDetalle}")

        return try {
            val dtoRequestBody = MultipartUtils.createDtoRequestBody(actividadDetalle)

            val response = restActividadDetalle.actualizarActividadDetalle(
                token = TokenUtils.TOKEN_CONTENT,
                id = actividadDetalle.idActividadDetalle,
                dto = dtoRequestBody,
                imagenFile = null
            )

            val isSuccess = response.isSuccessful && response.body()?.idActividadDetalle != null

            if (isSuccess) {
                Log.i(TAG, "ActividadDetalle modificado exitosamente (sin imagen)")
            } else {
                Log.e(TAG, "Error al modificar actividadDetalle: ${response.code()} - ${response.message()}")
                Log.e(TAG, "ErrorBody: ${response.errorBody()?.string()}")
            }

            isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al modificar actividadDetalle sin imagen", e)
            false
        }
    }
    override suspend fun buscarActividadDetallesByPaqueteId(paqueteId: Long): List<ActividadDetalleResp> {
        try {
            // Primero obtenemos todos los detalles
            val allDetalles = reportarActividadDetalles()
            // Filtramos por ID de actividad
            return allDetalles.filter { it.paquete?.idPaquete == paqueteId }
        } catch (e: Exception) {
            Log.e("REPO", "Error al buscar detalles por ID de paquete", e)
            return emptyList()
        }
    }
}