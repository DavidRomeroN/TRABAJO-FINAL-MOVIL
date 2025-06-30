package pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.CarritoItemDto
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.CarritoResp
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.CarritoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class CarritoMainViewModel  @Inject constructor(
    private val provRepo: CarritoItemRepository,
    private val carRepo: CarritoRepository,

    private val actRepo: ActividadRepository,
    private val serAliRepo: ServicioAlimentacionRepository,
    private val serArtRepo: ServicioArtesaniaRepository,
    private val serHotRepo: ServicioHoteleraRepository

): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _provs = MutableStateFlow<List<CarritoItemResp>>(emptyList())
    val provs: StateFlow<List<CarritoItemResp>> = _provs

    private val _carrito = MutableStateFlow<List<CarritoResp>>(emptyList())
    val carritos: StateFlow<List<CarritoResp>> = _carrito

    init {
        cargarCarritoItems()
    }
    fun cargarCarritoItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _provs.value = provRepo.reportarCarritoItemsPorCarrito()
            _isLoading.value = false
        }
    }
    /*
    fun cargarCarritoItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _provs.value = provRepo.reportarCarritoItems()
            _carrito.value = carRepo.reportarCarritos()
            _isLoading.value = false
        }
    }*/

    fun buscarPorId(id: Long): Flow<CarritoItemResp> = flow {
        emit(provRepo.buscarCarritoItemId(id))
    }

    fun buscarActividadPorId(id: Long): Flow<ActividadResp> = flow {
        emit(actRepo.buscarActividadId(id))
    }

    fun buscarAlimentacionPorId(id: Long): Flow<ServicioAlimentacionResp> = flow {
        emit(serAliRepo.buscarServicioAlimentacionId(id))
    }

    fun buscarArtesaniaPorId(id: Long): Flow<ServicioArtesaniaResp> = flow {
        emit(serArtRepo.buscarServicioArtesaniaId(id))
    }

    fun buscarHoteleraPorId(id: Long): Flow<ServicioHoteleraResp> = flow {
        emit(serHotRepo.buscarServicioHoteleraId(id))
    }

    fun eliminar(carritoItem: CarritoItemDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = provRepo.deleteCarritoItem(carritoItem)
            if (success) {
                //eliminarCarritoItemDeLista(carritoItem.idCarritoItem)
                cargarCarritoItems()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("CarritoItemVM", "Error al eliminar carritoItem", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarCarritoItemDeLista(id: Long) {
        _provs.value = _provs.value.filterNot { it.idCarritoItem == id }
    }
}