package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import javax.inject.Inject

@HiltViewModel
class ServicioAlimentacionFormViewModel @Inject constructor(
    private val servaliRepo: ServicioAlimentacionRepository,
    private val servRepo: ServicioRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _servicioAlimentacion = MutableStateFlow<ServicioAlimentacionResp?>(null)
    val servicioAlimentacion: StateFlow<ServicioAlimentacionResp?> = _servicioAlimentacion

    private val _servs = MutableStateFlow<List<ServicioResp>>(emptyList())
    val servs: StateFlow<List<ServicioResp>> = _servs

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getServicioAlimentacion(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _servicioAlimentacion.value = servaliRepo.buscarServicioAlimentacionId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _servs.value = servRepo.reportarServicios()
            //_categors.value = cateRepo.findAll()
            //_unidMeds.value = umRepo.findAll()
        }
    }

    fun addServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ServicioAlimentacionDto a ServicioAlimentacionCreateDto para excluir el idServicioAlimentacion
            val servicioAlimentacionCreateDto = ServicioAlimentacionCreateDto(
                tipoComida = servicioAlimentacion.tipoComida,
                estiloGastronomico = servicioAlimentacion.estiloGastronomico,
                incluyeBebidas = servicioAlimentacion.incluyeBebidas,
                servicio = servicioAlimentacion.servicio,
            )

            Log.i("REAL", "Creando servicioAlimentacion: $servicioAlimentacionCreateDto")
            servaliRepo.insertarServicioAlimentacion(servicioAlimentacionCreateDto)
            _isLoading.value = false
        }
    }

    fun editServicioAlimentacion(servicioAlimentacion: ServicioAlimentacionDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            servaliRepo.modificarServicioAlimentacion(servicioAlimentacion)
            _isLoading.value = false
        }
    }
}