package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.ActividadDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleResp
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.repository.ActividadDetalleRepository
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ActividadDetalleFormViewModel @Inject constructor(
    private val detalleRepo: ActividadDetalleRepository,
    private val actvRepo: ActividadRepository,
    private val packRepo: PaqueteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _actividadDetalle = MutableStateFlow<ActividadDetalleResp?>(null)
    val actividadDetalle: StateFlow<ActividadDetalleResp?> = _actividadDetalle

    private val _actvs = MutableStateFlow<List<ActividadResp>>(emptyList())
    val actvs: StateFlow<List<ActividadResp>> = _actvs

    private val _packs = MutableStateFlow<List<PaqueteResp>>(emptyList())
    val packs: StateFlow<List<PaqueteResp>> = _packs

    // Estados para manejo de imágenes
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _selectedImageFile = MutableLiveData<File?>()
    val selectedImageFile: LiveData<File?> = _selectedImageFile

    private val _currentImageUrl = MutableLiveData<String?>()
    val currentImageUrl: LiveData<String?> = _currentImageUrl

    fun getActividadDetalle(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _actividadDetalle.value = detalleRepo.buscarActividadDetalleId(idX)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _actvs.value = actvRepo.reportarActividades()
            Log.i("REAL", "Actvs: ${_actvs.value}")
            if (!SincronizacionControl.paquetesSincronizados) {
                val paquetesLocales = packRepo.reportarPaquetes().first()
                if (paquetesLocales.isEmpty()) {
                    packRepo.sincronizarPaquetes()
                    SincronizacionControl.paquetesSincronizados = true
                }
            }

            _packs.value = packRepo.reportarPaquetes().first()
            Log.i("REAL", "Packs: ${_packs.value}")
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

    fun addActividadDetalle(actividadDetalle: ActividadDetalleDto, context: Context? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir ActividadDetalleDto a ActividadDetalleCreateDto para excluir el idActividadDetalle
            val actividadDetalleCreateDto = ActividadDetalleCreateDto(
                titulo = actividadDetalle.titulo,
                descripcion = actividadDetalle.descripcion,
                //imagenUrl = actividadDetalle.imagenUrl,
                orden = actividadDetalle.orden,
                paquete = actividadDetalle.paquete,
                actividad = actividadDetalle.actividad,
                )

            Log.i("REAL", "Creando actividadDetalle: $actividadDetalleCreateDto")
            val insertSuccess = when {
                _selectedImageFile.value != null -> {
                    Log.i("ActividadDetalleViewModel", "Creando con archivo de imagen")
                    detalleRepo.insertarActividadDetalleConImagen(actividadDetalleCreateDto, _selectedImageFile.value!!)
                }
                _selectedImageUri.value != null -> {
                    Log.i("ActividadDetalleViewModel", "Creando con URI de imagen")
                    detalleRepo.insertarActividadDetalleConImagenUri(actividadDetalleCreateDto, _selectedImageUri.value!!, context)
                }
                else -> {
                    Log.i("ActividadDetalleViewModel", "Creando sin imagen")
                    // Necesitarías implementar este método en el repositorio
                    detalleRepo.insertarActividadDetalleSinImagen(actividadDetalleCreateDto, context)
                }
            }
            if (!insertSuccess) {
                //_operationResult.postValue(OperationResult.Error("Error al crear el paquete"))
                Log.e("ActividadDetalleViewModel", "Error al crear el detalle de actividad")
                return@launch
            }
            _isLoading.value = false
        }
    }

    fun editActividadDetalle(actividadDetalle: ActividadDetalleDto, context: Context? = null){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            val updateSuccess = when {
                _selectedImageFile.value != null -> {
                    Log.i("ActividadDetalleViewModel", "Actualizando con archivo de imagen")
                    detalleRepo.modificarActividadDetalleConImagen(actividadDetalle, _selectedImageFile.value!!)
                }
                _selectedImageUri.value != null -> {
                    Log.i("ActividadDetalleViewModel", "Actualizando con URI de imagen")
                    detalleRepo.modificarActividadDetalleConImagenUri(actividadDetalle, _selectedImageUri.value!!, context)
                }
                else -> {
                    Log.i("ActividadDetalleViewModel", "Actualizando sin cambiar imagen")
                    detalleRepo.modificarActividadDetalleSinImagen(actividadDetalle)
                }
            }

            if (!updateSuccess) {
                //_operationResult.postValue(OperationResult.Error("Error al actualizar el actividadDetalle"))
                Log.e("ActividadDetalleViewModel", "Error al actualizar el actividadDetalle")
                return@launch
            }
            _isLoading.value = false
        }
    }


}