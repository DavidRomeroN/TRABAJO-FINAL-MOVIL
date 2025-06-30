package pe.edu.upeu.granturismojpc.ui.presentation.screens.resena

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
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.ResenaCreateDto
import pe.edu.upeu.granturismojpc.model.ResenaDto
import pe.edu.upeu.granturismojpc.model.ResenaResp
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.repository.ResenaRepository
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class ResenaFormViewModel @Inject constructor(
    private val resRepo: ResenaRepository,
    private val packRepo: PaqueteRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _resena = MutableStateFlow<ResenaResp?>(null)
    val resena: StateFlow<ResenaResp?> = _resena

    private val _users = MutableStateFlow<List<ResenaResp>>(emptyList())
    val users: StateFlow<List<ResenaResp>> = _users

    private val _packs = MutableStateFlow<List<PaqueteResp>>(emptyList())
    val packs: StateFlow<List<PaqueteResp>> = _packs

    private val _user = MutableStateFlow(TokenUtils.USER_ID)
    val user: StateFlow<Long> = _user
    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getResena(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _resena.value = resRepo.buscarResenaId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _user.value = TokenUtils.USER_ID
            if (!SincronizacionControl.paquetesSincronizados) {
                val paquetesLocales = packRepo.reportarPaquetes().first()
                if (paquetesLocales.isEmpty()) {
                    packRepo.sincronizarPaquetes()
                    SincronizacionControl.paquetesSincronizados = true
                }
            }
            _packs.value = packRepo.reportarPaquetes().first()
        }
    }

    fun addResena(resena: ResenaDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ResenaDto a ResenaCreateDto para excluir el idResena
            val resenaCreateDto = ResenaCreateDto(
                calificacion = resena.calificacion,
                comentario = resena.comentario,
                fecha = resena.fecha,
                paquete = resena.paquete,
                usuario = resena.usuario,
            )

            Log.i("REAL", "Creando resena: $resenaCreateDto")
            resRepo.insertarResena(resenaCreateDto)
            _isLoading.value = false
        }
    }

    fun editResena(resena: ResenaDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            resRepo.modificarResena(resena)
            _isLoading.value = false
        }
    }
}