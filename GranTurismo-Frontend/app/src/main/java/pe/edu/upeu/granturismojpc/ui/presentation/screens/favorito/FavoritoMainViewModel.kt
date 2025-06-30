package pe.edu.upeu.granturismojpc.ui.presentation.screens.favorito

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoCreateDto
import pe.edu.upeu.granturismojpc.model.FavoritoDto
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteRespBuscar
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class FavoritoMainViewModel  @Inject constructor(
    private val favRepo: FavoritoRepository,
    private val packRepo: PaqueteRepository,
    ): ViewModel() {
    private var sincronizado = false
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> get() = _deleteSuccess

    private val _provs = MutableStateFlow<List<Favorito>>(emptyList())
    val provs: StateFlow<List<Favorito>> = _provs

    private val _packs = MutableStateFlow<List<PaqueteResp>>(emptyList())
    val packs: StateFlow<List<PaqueteResp>> = _packs

    val paquetes: StateFlow<List<PaqueteResp>> = packRepo.reportarPaquetes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        cargarFavoritos()
        sincronizarUnaVez()
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            _provs.value = favRepo.findAllR().first()
            _isLoading.value = false
        }
    }

    fun cargarPaquetes() {
        viewModelScope.launch {
            _isLoading.value = true
            _packs.value = packRepo.reportarPaquetes().first()
            _isLoading.value = false
        }
    }

    fun sincronizarUnaVez() {
        Log.i("HOME", SincronizacionControl.paquetesSincronizados.toString())
        if (SincronizacionControl.paquetesSincronizados) return

        viewModelScope.launch {
            // Forzamos sincronización siempre que no esté sincronizado
            packRepo.sincronizarPaquetes()
            SincronizacionControl.paquetesSincronizados = true
        }
    }

    fun buscarPaquetePorId(id: Long): Flow<PaqueteRespBuscar> = flow {
        emit(packRepo.buscarPaqueteId(id))
    }


    fun eliminar(favorito: FavoritoDto) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val success = favRepo.deleteFavorito(favorito)
            if (success) {
                //eliminarFavoritoDeLista(favorito.idFavorito)
                cargarFavoritos()
                _deleteSuccess.value = true
            }else{ _deleteSuccess.value = false }
        } catch (e: Exception) {
            Log.e("FavoritoVM", "Error al eliminar favorito", e)
            _deleteSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearDeleteResult() {
        _deleteSuccess.value = null
    }
    fun eliminarFavoritoDeLista(id: Long) {
        _provs.value = _provs.value.filterNot { it.idFavorito == id }
    }

    fun agregarFavorito(idPaquete: Long, tipo: String) {
        viewModelScope.launch {
            val favorito =
                FavoritoCreateDto(
                    usuario = TokenUtils.USER_ID,
                    referenciaId = idPaquete,
                    tipo = tipo
                )
            favRepo.saveFavorito(favorito)
            cargarFavoritos() // Actualiza la lista en pantalla
        }
    }

    fun eliminarFavorito(idPaquete: Long, tipo: String) {
        viewModelScope.launch {
            val favorito = provs.value.find { it.referenciaId == idPaquete && it.tipo == tipo }
            favorito?.let {
                favRepo.deleteFavorito(it.toDto())
                cargarFavoritos()
            }
        }
    }
}