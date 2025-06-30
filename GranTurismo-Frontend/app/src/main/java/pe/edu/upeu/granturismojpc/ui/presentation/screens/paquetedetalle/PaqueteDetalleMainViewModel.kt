package pe.edu.upeu.granturismojpc.ui.presentation.screens.paquetedetalle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleResp
import pe.edu.upeu.granturismojpc.repository.PaqueteDetalleRepository
import javax.inject.Inject

@HiltViewModel
class PaqueteDetalleMainViewModel  @Inject constructor(
    private val detalleRepo: PaqueteDetalleRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _details = MutableStateFlow<List<PaqueteDetalleResp>>(emptyList())
    val details: StateFlow<List<PaqueteDetalleResp>> = _details

    init {
        cargarPaqueteDetalle()
    }

    fun cargarPaqueteDetalle() {
        viewModelScope.launch {
            _isLoading.value = true
            _details.value = detalleRepo.reportarPaqueteDetalles()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<PaqueteDetalleResp> = flow {
        emit(detalleRepo.buscarPaqueteDetalleId(id))
    }

    fun eliminar(paqueteDetalle: PaqueteDetalleDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = detalleRepo.deletePaqueteDetalle(paqueteDetalle)
            if (success) {
                //eliminarPaqueteDetalleDeLista(paqueteDetalle.idPaqueteDetalle)
                cargarPaqueteDetalle()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("PaqueteDetalleVM", "Error al eliminar paqueteDetalle", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarPaqueteDetalleDeLista(id: Long) {
        _details.value = _details.value.filterNot { it.idPaqueteDetalle == id }
    }
}