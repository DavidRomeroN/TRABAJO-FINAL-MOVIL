package pe.edu.upeu.granturismojpc.ui.presentation.screens.favorito.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import javax.inject.Inject

@HiltViewModel
class FavoritoViewModel @Inject constructor(
    private val repository: FavoritoRepository,
    private val actividadRepository: ActividadRepository,
    private val alimentacionRepository: ServicioAlimentacionRepository,
    private val hoteleriaRepository: ServicioHoteleraRepository,
    private val artesaniaRepository: ServicioArtesaniaRepository
) : ViewModel() {

    private val _favoritos = MutableStateFlow<List<Favorito>>(emptyList())
    val favoritosFiltrados = _favoritos

    fun cargarFavoritosPorTipo(tipo: String) {
        viewModelScope.launch {
            val lista = repository.obtenerFavoritosPorTipo(tipo)
            _favoritos.value = lista
        }
    }

    fun filtrarFavoritos(query: String) {
        _favoritos.value = _favoritos.value.filter {
            it.tipo.contains(query, ignoreCase = true) || it.referenciaId.toString().contains(query)
        }
    }
    suspend fun getActividadById(id: Long) = actividadRepository.buscarActividadId(id)
    suspend fun getAlimentacionById(id: Long) = alimentacionRepository.buscarServicioAlimentacionId(id)
    suspend fun getHoteleriaById(id: Long) = hoteleriaRepository.buscarServicioHoteleraId(id)
    suspend fun getArtesaniaById(id: Long) = artesaniaRepository.buscarServicioArtesaniaId(id)
}