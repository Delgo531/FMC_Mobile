package mx.edu.utez.fmc_mobile.data.remote.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationApiService {

    @GET("api/notifications")
    suspend fun getMyNotifications(): Response<Map<String, Any>>

    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(): Response<Map<String, Any>>

    @PATCH("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long): Response<Map<String, Any>>

    @PATCH("api/notifications/read-all")
    suspend fun markAllAsRead(): Response<Map<String, Any>>
}
