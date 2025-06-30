package pe.edu.upeu.granturismojpc.ui.presentation.screens.proveedor

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ProveedorCreateDto
import pe.edu.upeu.granturismojpc.model.ProveedorDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.repository.ProveedorRepository
import javax.inject.Inject

@HiltViewModel
class ProveedorFormViewModel @Inject constructor(
    private val provRepo: ProveedorRepository,
    private val userRepo: ProveedorRepository,
    /*private val cateRepo: CategoriaRepository,
    private val umRepo: UnidadMedidaRepository,*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _proveedor = MutableStateFlow<ProveedorResp?>(null)
    val proveedor: StateFlow<ProveedorResp?> = _proveedor

    private val _users = MutableStateFlow<List<ProveedorResp>>(emptyList())
    val users: StateFlow<List<ProveedorResp>> = _users

    /*private val _categors = MutableStateFlow<List<Categoria>>(emptyList())
    val categors: StateFlow<List<Categoria>> = _categors

    private val _unidMeds = MutableStateFlow<List<UnidadMedida>>(emptyList())
    val unidMeds: StateFlow<List<UnidadMedida>> = _unidMeds*/

    fun getProveedor(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _proveedor.value = provRepo.buscarProveedorId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _users.value = userRepo.reportarProveedores()
            //_categors.value = cateRepo.findAll()
            //_unidMeds.value = umRepo.findAll()
        }
    }

    fun addProveedor(proveedor: ProveedorDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ProveedorDto a ProveedorCreateDto para excluir el idProveedor
            val proveedorCreateDto = ProveedorCreateDto(
                nombreCompleto = proveedor.nombreCompleto,
                email = proveedor.email,
                telefono = proveedor.telefono,
                fechaRegistro = proveedor.fechaRegistro,
                usuario = proveedor.usuario,
            )

            Log.i("REAL", "Creando proveedor: $proveedorCreateDto")
            provRepo.insertarProveedor(proveedorCreateDto)
            _isLoading.value = false
        }
    }

    fun editProveedor(proveedor: ProveedorDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            provRepo.modificarProveedor(proveedor)
            _isLoading.value = false
        }
    }
}