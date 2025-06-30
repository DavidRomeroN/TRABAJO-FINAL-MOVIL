package pe.edu.upeu.granturismojpc.ui.presentation.screens.destino

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.DestinoDto
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.repository.DestinoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import javax.inject.Inject

@HiltViewModel
class DestinoMainViewModel @Inject constructor(
    private val destRepo: DestinoRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _dest = MutableStateFlow<List<DestinoResp>>(emptyList())
    val dest: StateFlow<List<DestinoResp>> = _dest

    init {
        cargarDestinos()
    }
    fun cargarDestinos() {
        viewModelScope.launch {
            _isLoading.value = true
            _dest.value = destRepo.reportarDestinos()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<DestinoResp> = flow {
        emit(destRepo.buscarDestinoId(id))
    }

    fun eliminar(destino: DestinoDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = destRepo.deleteDestino(destino)
            if (success) {
                cargarDestinos()
                _deleteSuccess.value = success
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("DestinosVM", "Error al eliminar destino", e)
            _deleteSuccess.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarDestinoDeLista(id: Long) {
        _dest.value = _dest.value.filterNot { it.idDestino == id }
    }
}