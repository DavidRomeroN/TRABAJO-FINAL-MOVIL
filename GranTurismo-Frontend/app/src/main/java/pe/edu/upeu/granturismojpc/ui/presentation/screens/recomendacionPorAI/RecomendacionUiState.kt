package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI


import pe.edu.upeu.granturismojpc.model.ActividadClimaResp
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.DestinoResp

data class RecomendacionUiState(
    val isLoading: Boolean = false,
    val actividades: List<ActividadClimaResp> = emptyList(),
    val destinos: List<DestinoResp> = emptyList(),
    val mensajeIA: String = "",
    val error: String? = null
)
