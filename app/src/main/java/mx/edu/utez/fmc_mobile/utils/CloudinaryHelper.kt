package mx.edu.utez.fmc_mobile.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

object CloudinaryHelper {

    private const val CLOUD_NAME = "dwm2yefrs"
    private const val UPLOAD_PRESET = "fmcimages"
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    /**
     * Uploads an image from a Uri to Cloudinary using unsigned upload preset.
     * Returns the secure_url on success, null on failure.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return@withContext null
            val imageBytes = inputStream.readBytes()
            inputStream.close()

            val boundary = "----FormBoundary${System.currentTimeMillis()}"
            val url = URL(UPLOAD_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            val outputStream: OutputStream = connection.outputStream

            // upload_preset field
            outputStream.write("--$boundary\r\n".toByteArray())
            outputStream.write("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n".toByteArray())
            outputStream.write("$UPLOAD_PRESET\r\n".toByteArray())

            // file field
            outputStream.write("--$boundary\r\n".toByteArray())
            outputStream.write("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n".toByteArray())
            outputStream.write("Content-Type: image/jpeg\r\n\r\n".toByteArray())
            outputStream.write(imageBytes)
            outputStream.write("\r\n".toByteArray())

            outputStream.write("--$boundary--\r\n".toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(responseBody)
                return@withContext json.getString("secure_url")
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.readText()
                android.util.Log.e("CloudinaryHelper", "Upload failed: $responseCode - $errorBody")
                return@withContext null
            }
        } catch (e: Exception) {
            android.util.Log.e("CloudinaryHelper", "Upload error", e)
            return@withContext null
        }
    }

    /**
     * Uploads multiple images and returns a list of URLs.
     * Returns null if any upload fails.
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
