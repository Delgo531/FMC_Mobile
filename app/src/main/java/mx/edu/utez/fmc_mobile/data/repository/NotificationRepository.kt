package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient

class NotificationRepository {
    private val api = RetrofitClient.notificationApi

    suspend fun getMyNotifications() = api.getMyNotifications()
    suspend fun getUnreadCount() = api.getUnreadCount()
    suspend fun markAsRead(id: Long) = api.markAsRead(id)
    suspend fun markAllAsRead() = api.markAllAsRead()
}
