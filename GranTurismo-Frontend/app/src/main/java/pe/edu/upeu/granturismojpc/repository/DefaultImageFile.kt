package pe.edu.upeu.granturismojpc.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.io.File

@SuppressLint("ResourceType")
fun getDefaultImageFile(context: Context): File {
    return try {
        val inputStream = context.resources.openRawResource(pe.edu.upeu.granturismojpc.R.drawable.bg)
        val tempFile = File.createTempFile("default_image", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        Log.e("ImageHelper", "Error creando archivo desde drawable", e)
        null
    }!!
}