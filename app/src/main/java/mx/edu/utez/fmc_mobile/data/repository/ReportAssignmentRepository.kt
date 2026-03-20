package mx.edu.utez.fmc_mobile.data.repository

import mx.edu.utez.fmc_mobile.data.remote.RetrofitClient
import mx.edu.utez.fmc_mobile.data.remote.dto.request.CloseReportRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateReportStatusRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.VoteReportAssignmentRequest

class ReportAssignmentRepository {
    private val api = RetrofitClient.reportAssignmentApi

    suspend fun getAssignedReports() = api.getAssignedReports()
    suspend fun getAllAssignments() = api.getAllAssignments()

    suspend fun changeReportStatus(assignmentId: Long, request: UpdateReportStatusRequest) =
        api.changeReportStatus(assignmentId, request)

    suspend fun closeReportWithEvidence(assignmentId: Long, request: CloseReportRequest) =
        api.closeReportWithEvidence(assignmentId, request)

    suspend fun voteAssignment(assignmentId: Long, request: VoteReportAssignmentRequest) =
        api.voteAssignment(assignmentId, request)

    suspend fun getVoteStatus(assignmentId: Long) = api.getVoteStatus(assignmentId)
}
