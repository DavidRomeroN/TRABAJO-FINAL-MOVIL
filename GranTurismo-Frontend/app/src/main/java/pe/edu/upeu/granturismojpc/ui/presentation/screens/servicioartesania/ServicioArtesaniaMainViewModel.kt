package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class ServicioArtesaniaMainViewModel  @Inject constructor(
    private val servartRepo: ServicioArtesaniaRepository,
    private val favRepo: FavoritoRepository,
    private val carItemRepo: CarritoItemRepository,
    ): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _servarts = MutableStateFlow<List<ServicioArtesaniaResp>>(emptyList())
    val servarts: StateFlow<List<ServicioArtesaniaResp>> = _servarts

    private val _favs = MutableStateFlow<List<Favorito>>(emptyList())
    val favs: StateFlow<List<Favorito>> = _favs

    init {
        cargarServicioArtesaniaes()
        cargarFavoritos()
    }

    fun cargarServicioArtesaniaes() {
        viewModelScope.launch {
            _isLoading.value = true
            _servarts.value = servartRepo.reportarServicioArtesanias()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ServicioArtesaniaResp> = flow {
        emit(servartRepo.buscarServicioArtesaniaId(id))
    }

    fun buscarCarritoItemPorTipo(id: Long, tipo: String): Flow<CarritoItemResp?> = flow {
        emit(carItemRepo.buscarCarritoItemPorTipo(id,tipo))
    }

    fun eliminar(servicioArtesania: ServicioArtesaniaDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = servartRepo.deleteServicioArtesania(servicioArtesania)
            if (success) {
                //eliminarServicioArtesaniaDeLista(servicioArtesania.idServicioArtesania)
                cargarServicioArtesaniaes()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("ServicioArtesaniaVM", "Error al eliminar servicioArtesania", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarServicioArtesaniaDeLista(id: Long) {
        _servarts.value = _servarts.value.filterNot { it.idArtesania == id }
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favs.value = favRepo.obtenerFavoritosPorTipo("servicioArtesania")
            } catch (e: Exception) {
                // Manejo de errores
            } finally {
                _isLoading.value = false
            }
        }
    }
    /*
    fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favs.value = favRepo.findAllR().first()
            } catch (e: Exception) {
                // Manejo de errores
            } finally {
                _isLoading.value = false
            }
        }
    }

     */

    fun agregarFavorito(idServicio: Long) {
        viewModelScope.launch {
            val favorito =
                FavoritoCreateDto(
                    usuario = TokenUtils.USER_ID,
                    referenciaId = idServicio,
                    tipo = "servicioArtesania"
                )
            favRepo.saveFavorito(favorito)
            cargarFavoritos() // Actualiza la lista en pantalla
        }
    }

    fun eliminarFavorito(idServicio: Long) {
        viewModelScope.launch {
            val favorito = favs.value.find { it.referenciaId == idServicio && it.tipo == "servicioArtesania"}
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos()
            }
        }
    }
}