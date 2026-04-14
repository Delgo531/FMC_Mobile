package mx.edu.utez.fmc_mobile.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object CloudinaryHelper {

    private const val CLOUD_NAME    = "dwm2yefrs"
    private const val UPLOAD_PRESET = "fmcimages"
    private const val UPLOAD_URL    = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    private const val TAG           = "CloudinaryHelper"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Sube una imagen desde un Uri a Cloudinary usando el preset sin firmar.
     * Retorna la secure_url en éxito, null en fallo.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                // Leer los bytes de la imagen en el hilo IO antes de cualquier operación de red
                val imageBytes: ByteArray = context.contentResolver
                    .openInputStream(imageUri)
                    ?.use { it.readBytes() }
                    ?: run {
                        Log.e(TAG, "No se pudo abrir el stream de la imagen: $imageUri")
                        return@withContext null
                    }

                if (imageBytes.isEmpty()) {
                    Log.e(TAG, "El archivo de imagen está vacío")
                    return@withContext null
                }

                Log.d(TAG, "Subiendo imagen: ${imageBytes.size / 1024} KB")

                // Detectar el tipo MIME real a partir del header de bytes
                val mimeType = when {
                    imageBytes.size >= 3 &&
                    imageBytes[0] == 0xFF.toByte() &&
                    imageBytes[1] == 0xD8.toByte() &&
                    imageBytes[2] == 0xFF.toByte() -> "image/jpeg"
                    imageBytes.size >= 8 &&
                    imageBytes[1] == 0x50.toByte() &&
                    imageBytes[2] == 0x4E.toByte() &&
                    imageBytes[3] == 0x47.toByte() -> "image/png"
                    else -> "image/jpeg"
                }
                val extension = if (mimeType == "image/png") "png" else "jpg"

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .addFormDataPart(
                        "file",
                        "image.$extension",
                        imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    val url = json.getString("secure_url")
                    Log.d(TAG, "Imagen subida: $url")
                    url
                } else {
                    Log.e(TAG, "Error Cloudinary ${response.code}: $responseBody")
                    null
                }

            } catch (e: Exception) {
                Log.e(TAG, "Excepción al subir imagen: ${e.javaClass.simpleName} - ${e.message}")
                null
            }
        }

    /**
     * Sube múltiples imágenes y retorna la lista de URLs.
     * Retorna null si alguna falla.
     */
    suspend fun uploadImages(context: Context, imageUris: List<Uri>): List<String>? {
        val urls = mutableListOf<String>()
        for (uri in imageUris) {
            val url = uploadImage(context, uri) ?: return null
            urls.add(url)
        }
        return urls
    }
}
