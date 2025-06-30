package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ActividadDetalleDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleResp
import pe.edu.upeu.granturismojpc.repository.ActividadDetalleRepository
import javax.inject.Inject

@HiltViewModel
class ActividadDetalleMainViewModel  @Inject constructor(
    private val detalleRepo: ActividadDetalleRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _details = MutableStateFlow<List<ActividadDetalleResp>>(emptyList())
    val details: StateFlow<List<ActividadDetalleResp>> = _details

    init {
        cargarActividadDetalle()
    }

    fun cargarActividadDetalle() {
        viewModelScope.launch {
            _isLoading.value = true
            _details.value = detalleRepo.reportarActividadDetalles()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ActividadDetalleResp> = flow {
        emit(detalleRepo.buscarActividadDetalleId(id))
    }

    fun eliminar(actividadDetalle: ActividadDetalleDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = detalleRepo.deleteActividadDetalle(actividadDetalle)
            if (success) {
                cargarActividadDetalle()
                _deleteSuccess.value = true
            } else {
                _deleteSuccess.value = false
            }
        } catch (e: Exception) {
            Log.e("ActividadDetalleVM", "Error al eliminar actividadDetalle", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarActividadDetalleDeLista(id: Long) {
        _details.value = _details.value.filterNot { it.idActividadDetalle == id }
    }
    fun obtenerActividadesPorPaquete(paqueteId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val actividadesFiltradas = detalleRepo.buscarActividadDetallesByPaqueteId(paqueteId)
                _details.value = actividadesFiltradas
            } catch (e: Exception) {
                Log.e("ActividadDetalleVM", "Error al cargar actividades por paquete", e)
                _details.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

}