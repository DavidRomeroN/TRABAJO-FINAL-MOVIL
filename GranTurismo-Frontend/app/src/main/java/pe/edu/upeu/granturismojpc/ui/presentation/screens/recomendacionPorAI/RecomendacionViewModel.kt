package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecomendacionViewModel @Inject constructor(
    private val recomendacionRepository: RecomendacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecomendacionUiState())
    val uiState: StateFlow<RecomendacionUiState> = _uiState

    fun cargarRecomendaciones(dia: Int = 0) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val recomendacion = recomendacionRepository.obtenerRecomendacionAsesor(dia)

                _uiState.value = RecomendacionUiState(
                    isLoading = false,
                    actividades = recomendacion.actividades,
                    mensajeIA = recomendacion.mensajeIA
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }


}
