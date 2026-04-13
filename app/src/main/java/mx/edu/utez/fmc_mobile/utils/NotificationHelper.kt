package mx.edu.utez.fmc_mobile.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.edu.utez.fmc_mobile.data.repository.NotificationRepository

// Debe ser top-level para que Gson pueda usar reflection correctamente
private data class RawNotification(
    val id: Long,
    val type: String,
    val message: String,
    val read: Boolean
)

object NotificationHelper {

    const val CHANNEL_ID   = "fmc_notifications"
    const val CHANNEL_NAME = "Fix My City"

    /** Tipos del backend → texto en español */
    fun typeToSpanish(type: String): String = when (type) {
        "REPORT_ASSIGNED"      -> "Reporte Asignado"
        "STATUS_CHANGED"       -> "Estado Actualizado"
        "REPORT_REJECTED"      -> "Reporte Rechazado"
        "APPLICATION_RESOLVED" -> "Solicitud Resuelta"
        "LEADER_APPLICATION"   -> "Postulación a Líder"
        "NEW_VOLUNTEER"        -> "Nuevo Voluntario"
        "LEADER_LEFT"          -> "Sin Líder de Cuadrilla"
        "DUPLICATE_REPORT"     -> "Reporte Duplicado"
        "SQUAD_ASSIGNED"       -> "Asignado a Cuadrilla"
        "MEMBER_ADDED"         -> "Agregado a Cuadrilla"
        else -> type.replace("_", " ")
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    }

    /** Crea el canal de notificaciones (llamar en MainActivity.onCreate). */
    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Notificaciones de Fix My City" }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    /**
     * Consulta la API y muestra en la barra del sistema las notificaciones
     * no leídas que no hayan sido mostradas antes.
     * El historial se persiste en SharedPreferences: borrar la notificación
     * del panel del sistema NO hace que vuelva a aparecer.
     */
    suspend fun pollAndShowNew(context: Context) {
        if (!SessionManager.isLoggedIn()) return
        try {
            val response = NotificationRepository().getMyNotifications()
            if (!response.isSuccessful) return
            val body = response.body() ?: return
            val data = body["data"] ?: return

            val gson = Gson()
            val json = gson.toJson(data)
            val listJson = if (json.trimStart().startsWith("[")) json
            else {
                val map = gson.fromJson<Map<String, Any>>(
                    json, object : TypeToken<Map<String, Any>>() {}.type
                )
                gson.toJson(map["content"])
            }

            val rawType = object : TypeToken<List<RawNotification>>() {}.type
            val items: List<RawNotification> = gson.fromJson(listJson, rawType) ?: return

            items
                .filter { !it.read && !SessionManager.isNotificationShown(it.id) }
                .forEach { item ->
                    // Marcar ANTES de publicar para evitar duplicados si se llama
                    // desde Home y Cuadrillas al mismo tiempo
                    SessionManager.markNotificationShown(item.id)
                    showRaw(context, item.id, typeToSpanish(item.type), item.message)
                }
        } catch (_: Exception) { }
    }

    /** Publica la notificación nativa sin comprobación de historial. */
    private fun showRaw(context: Context, notificationId: Long, title: String, body: String) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId.toInt(), notification)
    }
}
