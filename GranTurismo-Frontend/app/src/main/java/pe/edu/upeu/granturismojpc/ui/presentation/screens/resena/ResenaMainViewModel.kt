package pe.edu.upeu.granturismojpc.ui.presentation.screens.resena

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ResenaDto
import pe.edu.upeu.granturismojpc.model.ResenaResp
import pe.edu.upeu.granturismojpc.repository.ResenaRepository
import javax.inject.Inject

@HiltViewModel
class ResenaMainViewModel  @Inject constructor(
    private val provRepo: ResenaRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _provs = MutableStateFlow<List<ResenaResp>>(emptyList())
    val provs: StateFlow<List<ResenaResp>> = _provs

    init {
        cargarResenas()
    }

    fun cargarResenas() {
        viewModelScope.launch {
            _isLoading.value = true
            _provs.value = provRepo.reportarResenas()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ResenaResp> = flow {
        emit(provRepo.buscarResenaId(id))
    }

    fun eliminar(resena: ResenaDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = provRepo.deleteResena(resena)
            if (success) {
                //eliminarResenaDeLista(resena.idResena)
                cargarResenas()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("ResenaVM", "Error al eliminar resena", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarResenaDeLista(id: Long) {
        _provs.value = _provs.value.filterNot { it.idResena == id }
    }
}