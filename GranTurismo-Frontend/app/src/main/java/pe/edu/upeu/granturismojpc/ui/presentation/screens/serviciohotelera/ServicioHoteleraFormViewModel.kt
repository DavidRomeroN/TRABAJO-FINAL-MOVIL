package pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import javax.inject.Inject

@HiltViewModel
class ServicioHoteleraFormViewModel @Inject constructor(
    private val servhotRepo: ServicioHoteleraRepository,
    private val servRepo: ServicioRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _servicioHotelera = MutableStateFlow<ServicioHoteleraResp?>(null)
    val servicioHotelera: StateFlow<ServicioHoteleraResp?> = _servicioHotelera

    private val _servs = MutableStateFlow<List<ServicioResp>>(emptyList())
    val servs: StateFlow<List<ServicioResp>> = _servs

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getServicioHotelera(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _servicioHotelera.value = servhotRepo.buscarServicioHoteleraId(idX)
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

    fun addServicioHotelera(servicioHotelera: ServicioHoteleraDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ServicioHoteleraDto a ServicioHoteleraCreateDto para excluir el idServicioHotelera
            val servicioHoteleraCreateDto = ServicioHoteleraCreateDto(
                servicio = servicioHotelera.servicio,
                tipoHabitacion = servicioHotelera.tipoHabitacion,
                estrellas = servicioHotelera.estrellas,
                incluyeDesayuno = servicioHotelera.incluyeDesayuno,
                maxPersonas = servicioHotelera.maxPersonas,
            )

            Log.i("REAL", "Creando servicioHotelera: $servicioHoteleraCreateDto")
            servhotRepo.insertarServicioHotelera(servicioHoteleraCreateDto)
            _isLoading.value = false
        }
    }

    fun editServicioHotelera(servicioHotelera: ServicioHoteleraDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            servhotRepo.modificarServicioHotelera(servicioHotelera)
            _isLoading.value = false
        }
    }
}