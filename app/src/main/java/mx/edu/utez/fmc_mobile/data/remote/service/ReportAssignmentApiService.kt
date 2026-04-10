package mx.edu.utez.fmc_mobile.data.remote.service

import mx.edu.utez.fmc_mobile.data.remote.dto.request.CloseReportRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateReportStatusRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.VoteReportAssignmentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportAssignmentApiService {

    @GET("api/assignments/assigned-reports")
    suspend fun getAssignedReports(): Response<Map<String, Any>>

    @GET("api/assignments")
    suspend fun getAllAssignments(): Response<Map<String, Any>>

    @PATCH("api/assignments/{assignmentId}/status")
    suspend fun changeReportStatus(
        @Path("assignmentId") assignmentId: Long,
        @Body request: UpdateReportStatusRequest
    ): Response<Map<String, Any>>

    @POST("api/assignments/{assignmentId}/close")
    suspend fun closeReportWithEvidence(
        @Path("assignmentId") assignmentId: Long,
        @Body request: CloseReportRequest
    ): Response<Map<String, Any>>

    @POST("api/assignments/{assignmentId}/vote")
    suspend fun voteAssignment(
        @Path("assignmentId") assignmentId: Long,
        @Body request: VoteReportAssignmentRequest
    ): Response<Map<String, Any>>

    @GET("api/assignments/{assignmentId}/vote-status")
    suspend fun getVoteStatus(@Path("assignmentId") assignmentId: Long): Response<Map<String, Any>>

    @GET("api/squads/my-squad-role")
    suspend fun getMySquadRole(): Response<Map<String, Any>>
}
