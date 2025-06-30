package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI

import pe.edu.upeu.granturismojpc.model.ActividadClimaResp
import pe.edu.upeu.granturismojpc.model.ClimaInfo
import pe.edu.upeu.granturismojpc.model.DestinoResp

data class Clima(
    val fecha: String,
    val ideal: Boolean,
    val mensaje: String,
    val temperatura: Double
)

data class ClimaUiState(
    val isLoading: Boolean = false,
    val actividades: List<ActividadClimaResp> = emptyList(),
    val clima: ClimaInfo? = null,  // <--- aquí
    val error: String? = null
)

