package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.ServicioDto
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class ServicioMainViewModel  @Inject constructor(
    private val servRepo: ServicioRepository,
    private val serAliRepo: ServicioAlimentacionRepository,
    private val serArtRepo: ServicioArtesaniaRepository,
    private val serHotRepo: ServicioHoteleraRepository,
    private val carItemRepo: CarritoItemRepository,
    private val favRepo: FavoritoRepository,
): ViewModel() {

    // ⭐ AGREGAR: Exponer el repository para PuntosScreen
    val repository: ServicioRepository get() = servRepo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _provs = MutableStateFlow<List<ServicioResp>>(emptyList())
    val provs: StateFlow<List<ServicioResp>> = _provs

    private val _favs = MutableStateFlow<List<Favorito>>(emptyList())
    val favs: StateFlow<List<Favorito>> = _favs

    init {
        cargarServicios()
    }

    fun cargarServicios() {
        viewModelScope.launch {
            _isLoading.value = true
            _provs.value = servRepo.reportarServicios()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ServicioResp> = flow {
        emit(servRepo.buscarServicioId(id))
    }

    fun buscarAlimentacionPorId(idServicio: Long): Flow<ServicioAlimentacionResp?> = flow {
        emit(serAliRepo.buscarServicioAlimentacionPorServicio(idServicio))
    }

    fun buscarArtesaniaPorId(idServicio: Long): Flow<ServicioArtesaniaResp?> = flow {
        emit(serArtRepo.buscarServicioArtesaniaPorServicio(idServicio))
    }

    fun buscarHoteleraPorId(idServicio: Long): Flow<ServicioHoteleraResp?> = flow {
        emit(serHotRepo.buscarServicioHoteleraPorServicio(idServicio))
    }

    fun buscarCarritoItemPorTipo(id: Long, tipo: String): Flow<CarritoItemResp?> = flow {
        emit(carItemRepo.buscarCarritoItemPorTipo(id,tipo))
    }

    fun cargarFavoritos(tipo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favs.value = favRepo.obtenerFavoritosPorTipo(tipo)
            } catch (e: Exception) {
                // Manejo de errores
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminar(servicio: ServicioDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = servRepo.deleteServicio(servicio)
            if (success) {
                //eliminarServicioDeLista(servicio.idServicio)
                cargarServicios()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("ServicioVM", "Error al eliminar servicio", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarServicioDeLista(id: Long) {
        _provs.value = _provs.value.filterNot { it.idServicio == id }
    }

    fun agregarFavorito(idServicio: Long, tipo: String) {
        viewModelScope.launch {
            val favorito =
                FavoritoCreateDto(
                    usuario = TokenUtils.USER_ID,
                    referenciaId = idServicio,
                    tipo = tipo
                )
            favRepo.saveFavorito(favorito)
            cargarFavoritos(tipo) // Actualiza la lista en pantalla
        }
    }

    fun eliminarFavorito(idServicio: Long, tipo: String) {
        viewModelScope.launch {
            val favorito = favs.value.find { it.referenciaId == idServicio && it.tipo == tipo}
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos(tipo)
            }
        }
    }
}