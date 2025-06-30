package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion

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
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class ServicioAlimentacionMainViewModel  @Inject constructor(
    private val servaliRepo: ServicioAlimentacionRepository,
    private val favRepo: FavoritoRepository,
    private val carItemRepo: CarritoItemRepository,

    ): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _servalis = MutableStateFlow<List<ServicioAlimentacionResp>>(emptyList())
    val servalis: StateFlow<List<ServicioAlimentacionResp>> = _servalis

    private val _favs = MutableStateFlow<List<Favorito>>(emptyList())
    val favs: StateFlow<List<Favorito>> = _favs

    init {
        cargarServicioAlimentaciones()
        cargarFavoritos()
    }

    fun cargarServicioAlimentaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            _servalis.value = servaliRepo.reportarServicioAlimentaciones()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ServicioAlimentacionResp> = flow {
        emit(servaliRepo.buscarServicioAlimentacionId(id))
    }

    fun buscarCarritoItemPorTipo(id: Long, tipo: String): Flow<CarritoItemResp?> = flow {
        emit(carItemRepo.buscarCarritoItemPorTipo(id,tipo))
    }

    fun eliminar(servicioAlimentacion: ServicioAlimentacionDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = servaliRepo.deleteServicioAlimentacion(servicioAlimentacion)
            if (success) {
                //eliminarServicioAlimentacionDeLista(servicioAlimentacion.idServicioAlimentacion)
                cargarServicioAlimentaciones()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("ServicioAlimentacionVM", "Error al eliminar servicioAlimentacion", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarServicioAlimentacionDeLista(id: Long) {
        _servalis.value = _servalis.value.filterNot { it.idAlimentacion == id }
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favs.value = favRepo.obtenerFavoritosPorTipo("servicioAlimentacion")
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
                    tipo = "servicioAlimentacion"
                )
            favRepo.saveFavorito(favorito)
            cargarFavoritos() // Actualiza la lista en pantalla
        }
    }

    fun eliminarFavorito(idServicio: Long) {
        viewModelScope.launch {
            val favorito = favs.value.find { it.referenciaId == idServicio && it.tipo == "servicioAlimentacion"}
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos()
            }
        }
    }
}