package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad

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
import pe.edu.upeu.granturismojpc.model.ActividadDto
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.CarritoRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class ActividadMainViewModel  @Inject constructor(
    private val servRepo: ActividadRepository,
    private val favRepo: FavoritoRepository,
    private val carItemRepo: CarritoItemRepository,
    private val carRepo: CarritoRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _actvs = MutableStateFlow<List<ActividadResp>>(emptyList())
    val actvs: StateFlow<List<ActividadResp>> = _actvs

    private val _favs = MutableStateFlow<List<Favorito>>(emptyList())
    val favs: StateFlow<List<Favorito>> = _favs

    init {
        cargarActividads()
        cargarFavoritos()
    }

    fun cargarActividads() {
        viewModelScope.launch {
            _isLoading.value = true
            _actvs.value = servRepo.reportarActividades()
            _isLoading.value = false
        }
    }

    fun buscarPorId(id: Long): Flow<ActividadResp> = flow {
        emit(servRepo.buscarActividadId(id))
    }

    fun buscarCarritoItemPorTipo(id: Long, tipo: String): Flow<CarritoItemResp?> = flow {
        emit(carItemRepo.buscarCarritoItemPorTipo(id,tipo))
    }

    fun eliminar(actividad: ActividadDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = servRepo.deleteActividad(actividad)
            if (success) {
                cargarActividads()
                _deleteSuccess.value = true
            }else{
                _deleteSuccess.value = false
            }
        } catch (e: Exception) {
            Log.e("ActividadVM", "Error al eliminar actividad", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarActividadDeLista(id: Long) {
        _actvs.value = _actvs.value.filterNot { it.idActividad == id }
    }
    fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favs.value = favRepo.obtenerFavoritosPorTipo("actividad")
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

    fun agregarFavorito(idActividad: Long) {
        viewModelScope.launch {
            val favorito =
                FavoritoCreateDto(
                    usuario = TokenUtils.USER_ID,
                    referenciaId = idActividad,
                    tipo = "actividad"
                )
            favRepo.saveFavorito(favorito)
            cargarFavoritos() // Actualiza la lista en pantalla
        }
    }

    fun eliminarFavorito(idActividad: Long) {
        viewModelScope.launch {
            val favorito = favs.value.find { it.referenciaId == idActividad && it.tipo == "actividad"}
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos()
            }
        }
    }
}