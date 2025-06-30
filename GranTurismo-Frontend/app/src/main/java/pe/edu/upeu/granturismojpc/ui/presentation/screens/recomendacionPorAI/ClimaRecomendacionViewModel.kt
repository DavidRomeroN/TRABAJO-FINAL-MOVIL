package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import pe.edu.upeu.granturismojpc.model.ClimaInfo



@HiltViewModel
class ClimaRecomendacionViewModel @Inject constructor(
    private val repository: RecomendacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClimaUiState())
    val uiState: StateFlow<ClimaUiState> = _uiState


    fun cargarRecomendacionesPorClima(dia: Int = 0) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val response = repository.obtenerRecomendadasPorClima(dia) // ← ActividadesClimaWrapper
                println("Actividades recibidas del clima: ${response.actividades}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    actividades = response.actividades,
                    clima = ClimaInfo(
                        fecha = response.clima.fecha,
                        ideal = response.clima.ideal,
                        mensaje = response.clima.mensaje,
                        temperatura = response.clima.temperatura
                    )
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

