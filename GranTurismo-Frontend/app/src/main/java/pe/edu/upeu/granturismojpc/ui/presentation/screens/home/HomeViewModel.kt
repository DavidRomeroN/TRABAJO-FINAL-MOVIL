package pe.edu.upeu.granturismojpc.ui.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteRespBuscar
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prodRepo: PaqueteRepository,
    private val favRepo: FavoritoRepository,
    private val carItemRepo: CarritoItemRepository,
    ) : ViewModel() {

    private var sincronizado = false
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _prods = MutableStateFlow<List<PaqueteResp>>(emptyList())
    val prods: StateFlow<List<PaqueteResp>> = _prods

    private val _favs = MutableStateFlow<List<Favorito>>(emptyList())
    val favs: StateFlow<List<Favorito>> = _favs

    private val _paquetesPorId = mutableStateOf<Map<Long, PaqueteRespBuscar>>(emptyMap())
    val paquetesPorId: State<Map<Long, PaqueteRespBuscar>> = _paquetesPorId

    val paquetes: StateFlow<List<PaqueteResp>> = prodRepo.reportarPaquetes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun cargarPaquetes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _prods.value = prodRepo.reportarPaquetes().first() // 👈 Asegúrate de que esta función existe en tu repositorio
            } catch (e: Exception) {
                // Manejo de errores
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun sincronizarUnaVez() {
        Log.i("HOME", SincronizacionControl.paquetesSincronizados.toString())
        if (SincronizacionControl.paquetesSincronizados) return

        viewModelScope.launch {
            // Forzamos sincronización siempre que no esté sincronizado
            prodRepo.sincronizarPaquetes()
            SincronizacionControl.paquetesSincronizados = true
        }
    }
    fun sincronizarManual() {
        viewModelScope.launch {
            _isLoading.value = true
            SincronizacionControl.paquetesSincronizados = false
            sincronizarUnaVez()
            _isLoading.value = false
        }
    }
    fun buscarPorId(id: Long): Flow<PaqueteRespBuscar> = flow {
        emit(prodRepo.buscarPaqueteId(id))
    }

    fun buscarCarritoItemPorTipo(id: Long, tipo: String): Flow<CarritoItemResp?> = flow {
        emit(carItemRepo.buscarCarritoItemPorTipo(id,tipo))
    }

    fun cargarPaquetesBuscados(paquetes: List<PaqueteResp>) {
        viewModelScope.launch {
            val nuevos = paquetes.associateNotNull { paquete ->
                try {
                    val buscado = buscarPorId(paquete.idPaquete).firstOrNull()
                    if (buscado != null) {
                        paquete.idPaquete to buscado
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    // Podrías loguearlo para saber qué falló
                    Log.e("PaqueteViewModel", "Error buscando paquete ID=${paquete.idPaquete}: ${e.message}")
                    null
                }
            }
            _paquetesPorId.value = nuevos
        }
    }

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

    fun agregarFavorito(idPaquete: Long) {
        viewModelScope.launch {
            val favorito =
                FavoritoCreateDto(
                    usuario = TokenUtils.USER_ID,
                    referenciaId = idPaquete,
                    tipo = "paquete"
                )
            favRepo.saveFavorito(favorito)
            cargarFavoritos() // Actualiza la lista en pantalla
        }
    }

    fun eliminarFavorito(idPaquete: Long) {
        viewModelScope.launch {
            val favorito = favs.value.find { it.referenciaId == idPaquete && it.tipo == "paquete"}
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos()
            }
        }
    }
    inline fun <T, K, V> Iterable<T>.associateNotNull(transform: (T) -> Pair<K, V>?): Map<K, V> {
        val result = LinkedHashMap<K, V>()
        for (element in this) {
            val pair = transform(element)
            if (pair != null) {
                result[pair.first] = pair.second
            }
        }
        return result
    }

    fun agregarFavoritoActividad(idActividad: Long) {
        viewModelScope.launch {
            val favorito = FavoritoCreateDto(
                usuario = TokenUtils.USER_ID,
                referenciaId = idActividad,
                tipo = "actividad"
            )
            favRepo.saveFavorito(favorito)
            cargarFavoritos()
        }
    }

    fun eliminarFavoritoActividad(idActividad: Long) {
        viewModelScope.launch {
            val favorito = favs.value.find { it.referenciaId == idActividad && it.tipo == "actividad" }
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos()
            }
        }
    }

}