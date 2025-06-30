package pe.edu.upeu.granturismojpc.utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.io.File
import android.net.Uri
import android.content.Context
import java.io.InputStream

object MultipartUtils {

    private val gson = Gson()

    /**
     * Convierte un DTO a RequestBody para envío multipart
     */
    fun <T> createDtoRequestBody(dto: T): RequestBody {
        val json = gson.toJson(dto)
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }

    /**
     * Crea MultipartBody.Part desde un archivo
     */
    fun createImagePart(file: File, partName: String = "imagenFile"): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    /**
     * Crea MultipartBody.Part desde URI (Android)
     */
    fun createImagePartFromUri(
        context: Context?,
        imageUri: Uri,
        partName: String = "imagenFile"
    ): MultipartBody.Part? {
        return try {
            val inputStream: InputStream? = context!!.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            bytes?.let {
                val requestBody = it.toRequestBody("image/*".toMediaTypeOrNull())
                val fileName = "image_${System.currentTimeMillis()}.jpg"
                MultipartBody.Part.createFormData(partName, fileName, requestBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Crea MultipartBody.Part desde ByteArray
     */
    fun createImagePartFromBytes(
        bytes: ByteArray,
        fileName: String = "image.jpg",
        partName: String = "imagenFile"
    ): MultipartBody.Part {
        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }
}

// Clase para manejar respuestas paginadas (si la necesitas)
data class PageableResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
)
/*
data class Pageable(
    val sort: Sort,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)*/

data class Sort(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)

data class LocalizedResponse<T>(
    val data: T,
    val appliedLanguage: String?,
    val appliedCurrency: String?,
    val wasTranslated: Boolean?,
    val wasCurrencyConverted: Boolean?,
    val exchangeRate: Double?
)