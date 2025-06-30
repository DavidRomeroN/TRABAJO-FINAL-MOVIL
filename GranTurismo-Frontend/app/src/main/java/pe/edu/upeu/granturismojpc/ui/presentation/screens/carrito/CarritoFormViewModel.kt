package pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.CarritoCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoDto
import pe.edu.upeu.granturismojpc.model.CarritoItemCreateDto
import pe.edu.upeu.granturismojpc.model.CarritoItemDto
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.CarritoResp
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.CarritoRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CarritoFormViewModel @Inject constructor(
    private val provRepo: CarritoItemRepository,
    private val carRepo: CarritoRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _carritoItem = MutableStateFlow<CarritoItemResp?>(null)
    val carritoItem: StateFlow<CarritoItemResp?> = _carritoItem

    private val _carrito = MutableStateFlow<List<CarritoResp>>(emptyList())
    val carritos: StateFlow<List<CarritoResp>> = _carrito

    var carritoId: Long=0

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getCarritoItem(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _carritoItem.value = provRepo.buscarCarritoItemId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _carrito.value = carRepo.reportarCarritos()
            //_categors.value = cateRepo.findAll()
            //_unidMeds.value = umRepo.findAll()
        }
    }

    suspend fun crearCarritoYObtenerId(): Long {
        val nuevo = CarritoCreateDto(0, "", "").apply {
            usuario = TokenUtils.USER_ID
            fechaCreacion = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            estado = "PENDIENTE"
        }
        carRepo.insertarCarrito(nuevo)
        delay(500)
        val carritos = carRepo.reportarCarritos()
        return carritos.filter { it.usuario?.idUsuario == TokenUtils.USER_ID }
            .maxByOrNull { it.idCarrito }?.idCarrito ?: 0
    }

    fun addCarritoItem(carritoItem: CarritoItemDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir CarritoItemDto a CarritoItemCreateDto para excluir el idCarritoItem
            val carritoItemCreateDto = CarritoItemCreateDto(
                carrito=carritoItem.carrito,
                referenciaId = carritoItem.referenciaId,
                tipo=carritoItem.tipo,
                cantidadPersonas=carritoItem.cantidadPersonas,
                fechaReserva=carritoItem.fechaReserva,
                notas=carritoItem.notas
            )

            Log.i("REAL", "Creando carritoItem: $carritoItemCreateDto")
            provRepo.insertarCarritoItem(carritoItemCreateDto)
            _isLoading.value = false
        }
    }

    fun addCarrito(carrito: CarritoDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir CarritoItemDto a CarritoItemCreateDto para excluir el idCarritoItem
            val carritoCreateDto = CarritoCreateDto(
                usuario = carrito.usuario,
                estado=carrito.estado,
                fechaCreacion = carrito.fechaCreacion,
            )

            Log.i("REAL", "Creando carrito: $carritoCreateDto")
            carRepo.insertarCarrito(carritoCreateDto)
            _isLoading.value = false
        }
    }

    fun editCarritoItem(carritoItem: CarritoItemDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            provRepo.modificarCarritoItem(carritoItem)
            _isLoading.value = false
        }
    }
}