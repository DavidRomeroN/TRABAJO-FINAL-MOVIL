package pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.data.remote.RestReserva

import pe.edu.upeu.granturismojpc.model.ReservaCreateDto
import pe.edu.upeu.granturismojpc.model.ReservaResp
import pe.edu.upeu.granturismojpc.repository.ReservaRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isCreated = MutableStateFlow<Boolean?>(null)
    val isCreated: StateFlow<Boolean?> get() = _isCreated

    private val _reservas = MutableStateFlow<List<ReservaResp>>(emptyList())
    val reservas: StateFlow<List<ReservaResp>> get() = _reservas

    fun crearReserva(reserva: ReservaCreateDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            Log.i("ReservaViewModel", "Creando reserva: $reserva")
            val resultado = reservaRepository.insertarReserva(reserva)
            _isCreated.value = resultado
            _isLoading.value = false
        }
    }

    init {
        cargarReservas()
    }


    //para mostrar mis reservas
    fun cargarReservas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Asumiendo que reportarReservas() trae todas las reservas
                val lista = reservaRepository.reportarReservas()
                // Filtramos solo las reservas del usuario actual
                _reservas.value = lista.filter { it.usuario?.idUsuario == TokenUtils.USER_ID }
            } catch (e: Exception) {
                Log.e("ReservaVM", "Error al cargar reservas", e)
                _reservas.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
