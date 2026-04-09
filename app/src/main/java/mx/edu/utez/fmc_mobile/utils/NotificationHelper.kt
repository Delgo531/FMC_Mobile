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

    /** Muestra una notificación del sistema si el permiso está concedido. */
    fun show(context: Context, notificationId: Long, title: String, body: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
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
