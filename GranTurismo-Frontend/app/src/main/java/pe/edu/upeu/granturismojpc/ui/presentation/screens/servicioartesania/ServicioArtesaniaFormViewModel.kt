package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import javax.inject.Inject

@HiltViewModel
class ServicioArtesaniaFormViewModel @Inject constructor(
    private val servartRepo: ServicioArtesaniaRepository,
    private val servRepo: ServicioRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _servicioArtesania = MutableStateFlow<ServicioArtesaniaResp?>(null)
    val servicioArtesania: StateFlow<ServicioArtesaniaResp?> = _servicioArtesania

    private val _servs = MutableStateFlow<List<ServicioResp>>(emptyList())
    val servs: StateFlow<List<ServicioResp>> = _servs

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getServicioArtesania(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _servicioArtesania.value = servartRepo.buscarServicioArtesaniaId(idX)
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

    fun addServicioArtesania(servicioArtesania: ServicioArtesaniaDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ServicioArtesaniaDto a ServicioArtesaniaCreateDto para excluir el idServicioArtesania
            val servicioArtesaniaCreateDto = ServicioArtesaniaCreateDto(
                servicio = servicioArtesania.servicio,
                tipoArtesania = servicioArtesania.tipoArtesania,
                nivelDificultad = servicioArtesania.nivelDificultad,
                duracionTaller = servicioArtesania.duracionTaller,
                incluyeMaterial = servicioArtesania.incluyeMaterial,
                artesania = servicioArtesania.artesania,
                origenCultural = servicioArtesania.origenCultural,
                maxParticipantes = servicioArtesania.maxParticipantes,
                visitaTaller = servicioArtesania.visitaTaller,
                artesano = servicioArtesania.artesano
            )

            Log.i("REAL", "Creando servicioArtesania: $servicioArtesaniaCreateDto")
            servartRepo.insertarServicioArtesania(servicioArtesaniaCreateDto)
            _isLoading.value = false
        }
    }

    fun editServicioArtesania(servicioArtesania: ServicioArtesaniaDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            servartRepo.modificarServicioArtesania(servicioArtesania)
            _isLoading.value = false
        }
    }
}