package pe.edu.upeu.granturismojpc.ui.presentation.screens.destino

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.DestinoCreateDto
import pe.edu.upeu.granturismojpc.model.DestinoDto
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.repository.DestinoRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DestinoFormViewModel @Inject constructor(
    private val destRepo: DestinoRepository,
    savedStateHandle: SavedStateHandle
): ViewModel()  {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _destino = MutableStateFlow<DestinoResp?>(null)
    val destino: StateFlow<DestinoResp?> = _destino

    // Estados para manejo de imágenes
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _selectedImageFile = MutableLiveData<File?>()
    val selectedImageFile: LiveData<File?> = _selectedImageFile

    private val _currentImageUrl = MutableLiveData<String?>()
    val currentImageUrl: LiveData<String?> = _currentImageUrl

    fun getDestino(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _destino.value = destRepo.buscarDestinoId(idX)
            _isLoading.value = false
        }
    }

    /*fun getDatosPrevios() {
        viewModelScope.launch {
            _servs.value = servRepo.reportarServicios()
            //_categors.value = cateRepo.findAll()
            //_unidMeds.value = umRepo.findAll()
        }
    }
*/
    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
        // Limpiar el archivo seleccionado si se selecciona una URI
        if (uri != null) {
            _selectedImageFile.value = null
        }
    }

    fun setSelectedImageFile(file: File?) {
        _selectedImageFile.value = file
        // Limpiar la URI seleccionada si se selecciona un archivo
        if (file != null) {
            _selectedImageUri.value = null
        }
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
        _selectedImageFile.value = null
    }

    fun setCurrentImageUrl(url: String?) {
        _currentImageUrl.value = url
    }


    fun addDestino(destino: DestinoDto,
                   context: Context? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ServicioHoteleraDto a ServicioHoteleraCreateDto para excluir el idServicioHotelera
            val destinoCreateDto = DestinoCreateDto(
                nombre = destino.nombre,
                descripcion = destino.descripcion,
                ubicacion = destino.ubicacion,
                //imagenUrl = destino.imagenUrl,
                latitud = destino.latitud,
                longitud = destino.longitud,
                popularidad = destino.popularidad,
                precioMedio = destino.precioMedio,
                rating = destino.rating,
            )

            Log.i("REAL", "Creando destino: $destinoCreateDto")
            destRepo.insertarDestinoSinImagen(destinoCreateDto, context)
            _isLoading.value = false
        }
    }

    fun editDestino(destino: DestinoDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            destRepo.modificarDestinoSinImagen(destino)
            _isLoading.value = false
        }
    }

}