package mx.edu.utez.fmc_mobile.ui.screens.teams

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fmc_mobile.data.remote.dto.request.CloseReportRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.LeaveSquadRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.UpdateReportStatusRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.request.VoteReportAssignmentRequest
import mx.edu.utez.fmc_mobile.data.remote.dto.response.AssignedReportResponse
import mx.edu.utez.fmc_mobile.data.repository.ApplicationRepository
import mx.edu.utez.fmc_mobile.data.repository.ReportAssignmentRepository
import mx.edu.utez.fmc_mobile.data.repository.SquadRepository
import mx.edu.utez.fmc_mobile.utils.CloudinaryHelper
import mx.edu.utez.fmc_mobile.utils.SessionManager

class TeamsViewModel : ViewModel() {

    private val squadRepository = SquadRepository()
    private val applicationRepository = ApplicationRepository()
    private val assignmentRepository = ReportAssignmentRepository()
    private val gson = Gson()

    // User status: NONE, PENDING, MEMBER
    private val _userStatus = MutableStateFlow("NONE")
    val userStatus: StateFlow<String> = _userStatus

    private val _squadInfo = MutableStateFlow<Map<String, Any?>?>(null)
    val squadInfo: StateFlow<Map<String, Any?>?> = _squadInfo

    private val _assignedReports = MutableStateFlow<List<AssignedReportResponse>>(emptyList())
    val assignedReports: StateFlow<List<AssignedReportResponse>> = _assignedReports

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _actionSuccess = MutableStateFlow<String?>(null)
    val actionSuccess: StateFlow<String?> = _actionSuccess

    // Key: assignmentId
    private val _voteStatusMap = MutableStateFlow<Map<Long, VoteStatus>>(emptyMap())
    val voteStatusMap: StateFlow<Map<Long, VoteStatus>> = _voteStatusMap

    init {
        loadUserStatus()
    }

    fun loadUserStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Check if user is a volunteer/member by checking their role and squad info
                val isVolunteer = SessionManager.isVolunteer()
                val role = SessionManager.getRole()

                if (role == "VOLUNTEER" || role == "SQUAD_LEADER" || isVolunteer) {
                    // Try to get assigned reports - if we get them, user is assigned to a squad (MEMBER)
                    try {
                        val assignedResponse = assignmentRepository.getAssignedReports()
                        if (assignedResponse.isSuccessful) {
                            val body = assignedResponse.body()
                            val data = body?.get("data")
                            if (data != null) {
                                val json = gson.toJson(data)
                                val type = object : TypeToken<List<AssignedReportResponse>>() {}.type
                                val listJson = if (json.trimStart().startsWith("[")) json
                                else {
                                    val pageMap = gson.fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
                                    gson.toJson(pageMap["content"])
                                }
                                val reports: List<AssignedReportResponse> = gson.fromJson(listJson, type) ?: emptyList()
                                _assignedReports.value = reports
                                loadVoteStatuses(reports)
                            }
                            SessionManager.setPendingApplication(false)
                            _userStatus.value = "MEMBER"
                            // Squad info is best-effort for display only; failure does not affect status
                            loadSquadInfo()
                        } else {
                            // Volunteer approved but not yet assigned to a squad → waiting screen
                            _userStatus.value = if (isVolunteer) "VOLUNTEER_WAITING" else "NONE"
                        }
                    } catch (_: Exception) {
                        _userStatus.value = if (isVolunteer) "VOLUNTEER_WAITING" else "NONE"
                    }
                } else {
                    // Verify pending status from the server (SharedPreferences is cleared on reinstall)
                    try {
                        val statusResponse = applicationRepository.getMyApplicationStatus()
                        if (statusResponse.isSuccessful) {
                            val body = statusResponse.body()
                            val data = body?.get("data") as? Map<*, *>
                            val hasPending = data?.get("hasPendingVolunteerApplication") as? Boolean ?: false
                            SessionManager.setPendingApplication(hasPending)
                            _userStatus.value = if (hasPending) "PENDING" else "NONE"
                        } else {
                            // Fallback to local cache if request fails
                            _userStatus.value = if (SessionManager.hasPendingApplication()) "PENDING" else "NONE"
                        }
                    } catch (_: Exception) {
                        _userStatus.value = if (SessionManager.hasPendingApplication()) "PENDING" else "NONE"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadSquadInfo() {
        try {
            val response = assignmentRepository.getMySquadRole()
            if (response.isSuccessful) {
                val data = response.body()?.get("data") as? Map<*, *>
                if (data != null) {
                    _squadInfo.value = mapOf(
                        "userRole"     to (data["role"]?.toString()         ?: "MEMBER"),
                        "name"         to (data["squadName"]?.toString()    ?: ""),
                        "municipality" to (data["municipality"]?.toString() ?: "")
                    )
                }
            }
        } catch (_: Exception) { }
    }

    fun refreshData() {
        val role = SessionManager.getRole()
        val isVolunteer = SessionManager.isVolunteer()
        if (role == "VOLUNTEER" || role == "SQUAD_LEADER" || isVolunteer) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val assignedResponse = assignmentRepository.getAssignedReports()
                    if (assignedResponse.isSuccessful) {
                        val body = assignedResponse.body()
                        val data = body?.get("data")
                        if (data != null) {
                            val json = gson.toJson(data)
                            val type = object : TypeToken<List<AssignedReportResponse>>() {}.type
                            val listJson = if (json.trimStart().startsWith("[")) json
                            else {
                                val pageMap = gson.fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
                                gson.toJson(pageMap["content"])
                            }
                            val reports: List<AssignedReportResponse> = gson.fromJson(listJson, type) ?: emptyList()
                            _assignedReports.value = reports
                            loadVoteStatuses(reports)
                        }
                        SessionManager.setPendingApplication(false)
                        _userStatus.value = "MEMBER"
                        loadSquadInfo()
                    } else {
                        _userStatus.value = if (isVolunteer) "VOLUNTEER_WAITING" else "NONE"
                    }
                } catch (_: Exception) {
                    _userStatus.value = if (isVolunteer) "VOLUNTEER_WAITING" else "NONE"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            // CITIZEN users: re-check pending status from server on resume
            // This corrects stale state when switching accounts
            viewModelScope.launch {
                try {
                    val statusResponse = applicationRepository.getMyApplicationStatus()
                    if (statusResponse.isSuccessful) {
                        val body = statusResponse.body()
                        val data = body?.get("data") as? Map<*, *>
                        val hasPending = data?.get("hasPendingVolunteerApplication") as? Boolean ?: false
                        SessionManager.setPendingApplication(hasPending)
                        _userStatus.value = if (hasPending) "PENDING" else "NONE"
                    }
                } catch (_: Exception) { }
            }
        }
    }

    fun applyAsVolunteer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = applicationRepository.applyAsVolunteer()
                if (response.isSuccessful) {
                    SessionManager.setPendingApplication(true)
                    _userStatus.value = "PENDING"
                    _actionSuccess.value = "Solicitud enviada exitosamente"
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al enviar solicitud (Código: ${response.code()})"
                    }
                    if (errorMessage.contains("pendiente", ignoreCase = true)) {
                        SessionManager.setPendingApplication(true)
                        _userStatus.value = "PENDING"
                        _actionSuccess.value = "Ya tienes una solicitud pendiente"
                    } else {
                        _errorMessage.value = errorMessage
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun leaveSquad(password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = squadRepository.leaveSquad(LeaveSquadRequest(password))
                if (response.isSuccessful) {
                    SessionManager.setPendingApplication(false)
                    _userStatus.value = "NONE"
                    _squadInfo.value = null
                    _assignedReports.value = emptyList()
                    _actionSuccess.value = "Has abandonado la cuadrilla"
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al salir de la cuadrilla"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun voteReport(assignmentId: Long, vote: String) {
        viewModelScope.launch {
            try {
                val response = assignmentRepository.voteAssignment(
                    assignmentId, VoteReportAssignmentRequest(vote)
                )
                if (response.isSuccessful) {
                    _actionSuccess.value = "Voto registrado"
                    // Re-fetch vote status from the authoritative endpoint after voting
                    try {
                        val statusResponse = assignmentRepository.getVoteStatus(assignmentId)
                        if (statusResponse.isSuccessful) {
                            val body = statusResponse.body()
                            val data = body?.get("data") as? Map<*, *>
                            val rawAccept = data?.get("acceptVotes")
                                ?: data?.get("acceptCount")
                                ?: data?.get("accepts")
                                ?: data?.get("approveCount")
                            val accept = (rawAccept as? Number)?.toInt() ?: 0
                            val leaderAccepted = data?.get("leaderAccepted") as? Boolean ?: false
                            _voteStatusMap.value = _voteStatusMap.value.toMutableMap().apply {
                                put(assignmentId, VoteStatus(accept, leaderAccepted))
                            }
                        }
                    } catch (_: Exception) { }
                    // Full refresh when the vote resolved the assignment (left PENDING_VOTE)
                    val data = response.body()?.get("data") as? Map<*, *>
                    val resolvedStatus = (data?.get("assignmentStatus") as? String) ?: "PENDING_VOTE"
                    if (resolvedStatus != "PENDING_VOTE") {
                        loadUserStatus()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al votar"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    private suspend fun loadVoteStatuses(assignments: List<AssignedReportResponse>) {
        val pendingVote = assignments.filter {
            it.assignmentStatus.equals("PENDING_VOTE", ignoreCase = true)
        }
        val map = mutableMapOf<Long, VoteStatus>()
        for (assignment in pendingVote) {
            try {
                val response = assignmentRepository.getVoteStatus(assignment.assignmentId)
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    val rawAccept = data?.get("acceptVotes")
                        ?: data?.get("acceptCount")
                        ?: data?.get("accepts")
                        ?: data?.get("approveCount")
                    val accept = (rawAccept as? Number)?.toInt() ?: 0
                    val leaderAccepted = data?.get("leaderAccepted") as? Boolean ?: false
                    map[assignment.assignmentId] = VoteStatus(accept, leaderAccepted)
                }
            } catch (_: Exception) { }
        }
        _voteStatusMap.value = map
    }

    fun applyAsLeader() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = applicationRepository.applyAsLeader()
                if (response.isSuccessful) {
                    _actionSuccess.value = "Solicitud de liderazgo enviada. El administrador revisará tu solicitud."
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al enviar solicitud de liderazgo (Código: ${response.code()})"
                    }
                    if (errorMsg.contains("líder", ignoreCase = true) ||
                        errorMsg.contains("leader", ignoreCase = true) ||
                        errorMsg.contains("pendiente", ignoreCase = true)) {
                        _actionSuccess.value = "Ya tienes una solicitud de liderazgo pendiente"
                    } else {
                        _errorMessage.value = errorMsg
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _actionSuccess.value = null
    }
}

/** Estado de votación de un assignment: votos totales de aceptación y si el líder ya votó */
data class VoteStatus(val acceptVotes: Int, val leaderAccepted: Boolean)
