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
                    // Try to get assigned reports - if we get them, user is MEMBER
                    try {
                        val assignedResponse = assignmentRepository.getAssignedReports()
                        if (assignedResponse.isSuccessful) {
                            val body = assignedResponse.body()
                            val data = body?.get("data")
                            if (data != null) {
                                val json = gson.toJson(data)
                                val type = object : TypeToken<List<AssignedReportResponse>>() {}.type
                                _assignedReports.value = gson.fromJson(json, type) ?: emptyList()
                            }
                            _userStatus.value = "MEMBER"

                            // Get squad info
                            loadSquadInfo()
                        } else {
                            _userStatus.value = "NONE"
                        }
                    } catch (_: Exception) {
                        _userStatus.value = "NONE"
                    }
                } else {
                    // Check if user has a pending application
                    try {
                        val appResponse = applicationRepository.getAllApplications()
                        if (appResponse.isSuccessful) {
                            val body = appResponse.body()
                            val data = body?.get("data")
                            if (data != null) {
                                val json = gson.toJson(data)
                                val apps = gson.fromJson<List<Map<String, Any>>>(json,
                                    object : TypeToken<List<Map<String, Any>>>() {}.type) ?: emptyList()
                                val username = SessionManager.getUsername()
                                val myApp = apps.find { it["username"] == username }
                                if (myApp != null && myApp["status"] == "PENDING") {
                                    _userStatus.value = "PENDING"
                                } else {
                                    _userStatus.value = "NONE"
                                }
                            }
                        }
                    } catch (_: Exception) {
                        _userStatus.value = "NONE"
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
            val response = squadRepository.getAllSquads()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.get("data")
                if (data != null) {
                    val json = gson.toJson(data)
                    val squads = gson.fromJson<List<Map<String, Any>>>(json,
                        object : TypeToken<List<Map<String, Any>>>() {}.type) ?: emptyList()
                    val username = SessionManager.getUsername()
                    // Find squad where user is a member
                    val mySquad = squads.find { squad ->
                        val members = squad["members"] as? List<*>
                        members?.any { member ->
                            val m = member as? Map<*, *>
                            m?.get("username") == username
                        } == true
                    }
                    if (mySquad != null) {
                        val members = mySquad["members"] as? List<*>
                        val myMember = members?.find { member ->
                            val m = member as? Map<*, *>
                            m?.get("username") == username
                        } as? Map<*, *>
                        val myRole = myMember?.get("role")?.toString() ?: "MEMBER"
                        @Suppress("UNCHECKED_CAST")
                        _squadInfo.value = (mySquad as Map<String, Any?>).toMutableMap().apply {
                            put("userRole", myRole)
                        }
                    }
                }
            }
        } catch (_: Exception) { }
    }

    fun applyAsVolunteer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = applicationRepository.applyAsVolunteer()
                if (response.isSuccessful) {
                    _userStatus.value = "PENDING"
                    _actionSuccess.value = "Solicitud enviada exitosamente"
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Error al enviar solicitud"
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
                    loadUserStatus() // Refresh
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

    fun clearMessages() {
        _errorMessage.value = null
        _actionSuccess.value = null
    }
}
