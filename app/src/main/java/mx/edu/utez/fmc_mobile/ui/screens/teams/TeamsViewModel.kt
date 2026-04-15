package mx.edu.utez.fmc_mobile.ui.screens.teams

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
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
import mx.edu.utez.fmc_mobile.utils.NotificationHelper
import mx.edu.utez.fmc_mobile.utils.SessionManager

class TeamsViewModel(application: Application) : AndroidViewModel(application) {

    private val squadRepository = SquadRepository()
    private val applicationRepository = ApplicationRepository()
    private val assignmentRepository = ReportAssignmentRepository()
    private val gson = Gson()

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

    // Error específico del flujo "salir de cuadrilla" — se muestra dentro del dialog
    private val _leaveSquadError = MutableStateFlow<String?>(null)
    val leaveSquadError: StateFlow<String?> = _leaveSquadError

    private val _hasPendingLeaderApp = MutableStateFlow(false)
    val hasPendingLeaderApp: StateFlow<Boolean> = _hasPendingLeaderApp

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
                detectAndSetStatus()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Detecta el estado real del usuario consultando el servidor en cascada,
     * sin depender del rol en caché local (que puede estar desactualizado si el
     * admin cambió el estado desde la última sesión).
     *
     * Orden de detección:
     * 1. getAssignedReports() → 200  → MEMBER (tiene cuadrilla y asignaciones)
     * 2. getMySquadRole()     → 200  → MEMBER (cuadrilla sin asignaciones) /
     *                                   VOLUNTEER_WAITING (voluntario sin cuadrilla)
     * 3. getMyApplicationStatus()    → PENDING o NONE
     */
    private suspend fun detectAndSetStatus() {
        // ── Paso 1: reportes asignados ─────────────────────────────────────
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
                        val pageMap = gson.fromJson<Map<String, Any>>(
                            json, object : TypeToken<Map<String, Any>>() {}.type
                        )
                        gson.toJson(pageMap["content"])
                    }
                    val reports: List<AssignedReportResponse> =
                        gson.fromJson(listJson, type) ?: emptyList()
                    _assignedReports.value = reports
                    loadVoteStatuses(reports)
                }
                SessionManager.setPendingApplication(false)
                _userStatus.value = "MEMBER"
                loadSquadInfo()
                checkLeaderApplicationStatus()
                return
            }
        } catch (_: Exception) { }

        // ── Paso 2: rol en cuadrilla ───────────────────────────────────────
        // Sirve para detectar voluntarios aprobados (con o sin cuadrilla asignada)
        // cuando getAssignedReports() regresó un error (403/404).
        try {
            val squadRoleResponse = assignmentRepository.getMySquadRole()
            if (squadRoleResponse.isSuccessful) {
                val data = squadRoleResponse.body()?.get("data") as? Map<*, *>
                val squadName = data?.get("squadName")?.toString() ?: ""
                SessionManager.setPendingApplication(false)
                if (squadName.isNotBlank()) {
                    // Tiene cuadrilla pero getAssignedReports falló transitoriamente
                    _squadInfo.value = mapOf(
                        "userRole"     to (data?.get("role")?.toString()         ?: "MEMBER"),
                        "name"         to squadName,
                        "municipality" to (data?.get("municipality")?.toString() ?: "")
                    )
                    _userStatus.value = "MEMBER"
                    checkLeaderApplicationStatus()
                } else {
                    // Voluntario aprobado sin cuadrilla asignada aún
                    _userStatus.value = "VOLUNTEER_WAITING"
                }
                return
            }
        } catch (_: Exception) { }

        // ── Paso 3: estado de solicitud (ciudadano) ────────────────────────
        try {
            val statusResponse = applicationRepository.getMyApplicationStatus()
            if (statusResponse.isSuccessful) {
                val body = statusResponse.body()
                val data = body?.get("data") as? Map<*, *>
                val hasPending = data?.get("hasPendingVolunteerApplication") as? Boolean ?: false
                SessionManager.setPendingApplication(hasPending)
                _userStatus.value = if (hasPending) "PENDING" else "NONE"
            } else {
                _userStatus.value = if (SessionManager.hasPendingApplication()) "PENDING" else "NONE"
            }
        } catch (_: Exception) {
            _userStatus.value = if (SessionManager.hasPendingApplication()) "PENDING" else "NONE"
        }
    }

    /**
     * Consulta si el usuario tiene una postulación de líder pendiente.
     * Si el usuario ya es LÍDER, limpia el flag local.
     * Intenta leer el campo hasPendingLeaderApplication de la API; si no existe,
     * usa el valor guardado localmente en SessionManager.
     */
    private suspend fun checkLeaderApplicationStatus() {
        val currentRole = _squadInfo.value?.get("userRole")?.toString()
        if (currentRole == "LEADER") {
            SessionManager.setPendingLeaderApplication(false)
            _hasPendingLeaderApp.value = false
            return
        }
        try {
            val response = applicationRepository.getMyApplicationStatus()
            if (response.isSuccessful) {
                val data = response.body()?.get("data") as? Map<*, *>
                val fromApi = data?.get("hasPendingLeaderApplication") as? Boolean
                if (fromApi != null) {
                    SessionManager.setPendingLeaderApplication(fromApi)
                    _hasPendingLeaderApp.value = fromApi
                    return
                }
            }
        } catch (_: Exception) { }
        // Fallback al flag local
        _hasPendingLeaderApp.value = SessionManager.hasPendingLeaderApplication()
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                detectAndSetStatus()
            } catch (_: Exception) { }
            finally {
                _isLoading.value = false
            }
            NotificationHelper.pollAndShowNew(getApplication())
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
        _leaveSquadError.value = null

        // Validación local: no permitir salir si hay denuncias activas en la cuadrilla.
        // Se excluyen reportes cuyo reportStatus ya sea CLOSED, pues el backend los considera
        // resueltos aunque el assignmentStatus aún figure como ACCEPTED.
        val hasPending = _assignedReports.value.any {
            val s = it.assignmentStatus.uppercase()
            val r = it.reportStatus.uppercase()
            r != "CLOSED" && (s == "PENDING_VOTE" || s == "ACCEPTED" || s == "ON_THE_WAY" || s == "IN_PROGRESS")
        }
        if (hasPending) {
            _leaveSquadError.value =
                "No puedes salir mientras la cuadrilla tiene denuncias pendientes de resolver."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = squadRepository.leaveSquad(LeaveSquadRequest(password))
                if (response.isSuccessful) {
                    SessionManager.setPendingApplication(false)
                    SessionManager.setPendingLeaderApplication(false)
                    _userStatus.value = "NONE"
                    _squadInfo.value = null
                    _assignedReports.value = emptyList()
                    _leaveSquadError.value = null
                    _actionSuccess.value = "leave_success"   // señal para cerrar el dialog
                } else {
                    val errorBody = response.errorBody()?.string()
                    _leaveSquadError.value = try {
                        org.json.JSONObject(errorBody ?: "").getString("message")
                    } catch (_: Exception) {
                        "Contraseña incorrecta o error al salir de la cuadrilla"
                    }
                }
            } catch (e: Exception) {
                _leaveSquadError.value = e.message ?: "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLeaveSquadError() { _leaveSquadError.value = null }

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
                        detectAndSetStatus()
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
                    SessionManager.setPendingLeaderApplication(true)
                    _hasPendingLeaderApp.value = true
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
                        SessionManager.setPendingLeaderApplication(true)
                        _hasPendingLeaderApp.value = true
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
