package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad

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
import pe.edu.upeu.granturismojpc.model.ActividadCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDto
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ActividadFormViewModel @Inject constructor(
    private val actvRepo: ActividadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _actividad = MutableStateFlow<ActividadResp?>(null)
    val actividad: StateFlow<ActividadResp?> = _actividad

    // Estados para manejo de imágenes
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _selectedImageFile = MutableLiveData<File?>()
    val selectedImageFile: LiveData<File?> = _selectedImageFile

    private val _currentImageUrl = MutableLiveData<String?>()
    val currentImageUrl: LiveData<String?> = _currentImageUrl

    fun getActividad(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _actividad.value = actvRepo.buscarActividadId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            //_tipos.value = tipoRepo.findAll()
            //Log.i("REAL", "Tipos: ${_tipos.value}")
            //_categors.value = cateRepo.findAll()
            //_unidMeds.value = umRepo.findAll()
        }
    }

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

    fun addActividad(actividad: ActividadDto, context: Context? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ActividadDto a ActividadCreateDto para excluir el idActividad
            val actividadCreateDto = ActividadCreateDto(
                titulo = actividad.titulo,
                descripcion = actividad.descripcion,
                //imagenUrl = actividad.imagenUrl,
                tipo = actividad.tipo,
                duracionHoras = actividad.duracionHoras,
                precioBase = actividad.precioBase,
                )

            Log.i("REAL", "Creando actividad: $actividadCreateDto")

            val insertSuccess = when {
                _selectedImageFile.value != null -> {
                    Log.i("ActividadViewModel", "Creando con archivo de imagen")
                    actvRepo.insertarActividadConImagen(actividadCreateDto, _selectedImageFile.value!!)
                }
                _selectedImageUri.value != null -> {
                    Log.i("ActividadViewModel", "Creando con URI de imagen")
                    actvRepo.insertarActividadConImagenUri(actividadCreateDto, _selectedImageUri.value!!, context)
                }
                else -> {
                    Log.i("ActividadViewModel", "Creando sin imagen")
                    // Necesitarías implementar este método en el repositorio
                    actvRepo.insertarActividadSinImagen(actividadCreateDto, context)
                }
            }
            if (!insertSuccess) {
                //_operationResult.postValue(OperationResult.Error("Error al crear el paquete"))
                Log.e("ActividadViewModel", "Error al crear la actividad")
                return@launch
            }
            _isLoading.value = false
        }
    }

    fun editActividad(actividad: ActividadDto, context: Context? = null){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            val updateSuccess = when {
                _selectedImageFile.value != null -> {
                    Log.i("ActividadViewModel", "Actualizando con archivo de imagen")
                    actvRepo.modificarActividadConImagen(actividad, _selectedImageFile.value!!)
                }
                _selectedImageUri.value != null -> {
                    Log.i("ActividadViewModel", "Actualizando con URI de imagen")
                    actvRepo.modificarActividadConImagenUri(actividad, _selectedImageUri.value!!, context)
                }
                else -> {
                    Log.i("ActividadViewModel", "Actualizando sin cambiar imagen")
                    actvRepo.modificarActividadSinImagen(actividad)
                }
            }

            if (!updateSuccess) {
                //_operationResult.postValue(OperationResult.Error("Error al actualizar el actividad"))
                Log.e("ActividadViewModel", "Error al actualizar la actividad")
                return@launch
            }
            _isLoading.value = false
        }
    }


}