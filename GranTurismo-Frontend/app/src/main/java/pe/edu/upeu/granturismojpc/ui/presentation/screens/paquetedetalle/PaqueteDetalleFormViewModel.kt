package pe.edu.upeu.granturismojpc.ui.presentation.screens.paquetedetalle

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.repository.PaqueteDetalleRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import javax.inject.Inject

@HiltViewModel
class PaqueteDetalleFormViewModel @Inject constructor(
    private val detalleRepo: PaqueteDetalleRepository,
    private val packRepo: PaqueteRepository,
    private val servRepo: ServicioRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _paqueteDetalle = MutableStateFlow<PaqueteDetalleResp?>(null)
    val paqueteDetalle: StateFlow<PaqueteDetalleResp?> = _paqueteDetalle

    private val _packs = MutableStateFlow<List<PaqueteResp>>(emptyList())
    val packs: StateFlow<List<PaqueteResp>> = _packs

    private val _servs = MutableStateFlow<List<ServicioResp>>(emptyList())
    val servs: StateFlow<List<ServicioResp>> = _servs

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getPaqueteDetalle(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _paqueteDetalle.value = detalleRepo.buscarPaqueteDetalleId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _servs.value = servRepo.reportarServicios()
            Log.i("REAL", "Servs: ${_servs.value}")
            // Verificar si ya se sincronizó antes
            if (!SincronizacionControl.paquetesSincronizados) {
                val paquetesLocales = packRepo.reportarPaquetes().first()
                if (paquetesLocales.isEmpty()) {
                    packRepo.sincronizarPaquetes()
                    SincronizacionControl.paquetesSincronizados = true
                }
            }

            _packs.value = packRepo.reportarPaquetes().first()
            Log.i("REAL", "Packs: ${_packs.value}")
        }
    }

    fun addPaqueteDetalle(paqueteDetalle: PaqueteDetalleDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir PaqueteDetalleDto a PaqueteDetalleCreateDto para excluir el idPaqueteDetalle
            val paqueteDetalleCreateDto = PaqueteDetalleCreateDto(
                cantidad = paqueteDetalle.cantidad,
                precioEspecial = paqueteDetalle.precioEspecial,
                paquete = paqueteDetalle.paquete,
                servicio = paqueteDetalle.servicio,
                )

            Log.i("REAL", "Creando paqueteDetalle: $paqueteDetalleCreateDto")
            detalleRepo.insertarPaqueteDetalle(paqueteDetalleCreateDto)
            _isLoading.value = false
        }
    }

    fun editPaqueteDetalle(paqueteDetalle: PaqueteDetalleDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            detalleRepo.modificarPaqueteDetalle(paqueteDetalle)
            _isLoading.value = false
        }
    }


}