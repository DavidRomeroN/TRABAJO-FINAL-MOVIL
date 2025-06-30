package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ServicioCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioDto
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import pe.edu.upeu.granturismojpc.repository.TipoServicioRepository
import javax.inject.Inject

@HiltViewModel
class ServicioFormViewModel @Inject constructor(
    private val servRepo: ServicioRepository,
    private val tipoRepo: TipoServicioRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _servicio = MutableStateFlow<ServicioResp?>(null)
    val servicio: StateFlow<ServicioResp?> = _servicio

    private val _tipos = MutableStateFlow<List<TipoServicioResp>>(emptyList())
    val tipos: StateFlow<List<TipoServicioResp>> = _tipos

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getServicio(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _servicio.value = servRepo.buscarServicioId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _tipos.value = tipoRepo.findAll()
            Log.i("REAL", "Tipos: ${_tipos.value}")
            //_categors.value = cateRepo.findAll()
            //_unidMeds.value = umRepo.findAll()
        }
    }

    fun addServicio(servicio: ServicioDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ServicioDto a ServicioCreateDto para excluir el idServicio
            val servicioCreateDto = ServicioCreateDto(
                nombreServicio = servicio.nombreServicio,
                descripcion = servicio.descripcion,
                precioBase = servicio.precioBase,
                estado = servicio.estado,
                tipo = servicio.tipo,
                )

            Log.i("REAL", "Creando servicio: $servicioCreateDto")
            servRepo.insertarServicio(servicioCreateDto)
            _isLoading.value = false
        }
    }

    fun editServicio(servicio: ServicioDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            servRepo.modificarServicio(servicio)
            _isLoading.value = false
        }
    }


}