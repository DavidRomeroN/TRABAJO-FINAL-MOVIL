package pe.edu.upeu.granturismojpc.ui.presentation.screens.proveedor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ProveedorDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.repository.ProveedorRepository
import javax.inject.Inject

@HiltViewModel
class ProveedorMainViewModel  @Inject constructor(
    private val provRepo: ProveedorRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _provs = MutableStateFlow<List<ProveedorResp>>(emptyList())
    val provs: StateFlow<List<ProveedorResp>> = _provs

    init {
        cargarProveedores()
    }

    fun cargarProveedores() {
        viewModelScope.launch {
            _isLoading.value = true
            _provs.value = provRepo.reportarProveedores()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ProveedorResp> = flow {
        emit(provRepo.buscarProveedorId(id))
    }

    fun eliminar(proveedor: ProveedorDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = provRepo.deleteProveedor(proveedor)
            if (success) {
                //eliminarProveedorDeLista(proveedor.idProveedor)
                cargarProveedores()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("ProveedorVM", "Error al eliminar proveedor", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarProveedorDeLista(id: Long) {
        _provs.value = _provs.value.filterNot { it.idProveedor == id }
    }
}