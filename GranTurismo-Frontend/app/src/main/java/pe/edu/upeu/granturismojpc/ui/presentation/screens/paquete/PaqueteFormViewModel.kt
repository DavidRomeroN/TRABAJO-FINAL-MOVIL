package pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
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
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.PaqueteCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleCreateDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleResp
import pe.edu.upeu.granturismojpc.model.PaqueteDto
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteRespBuscar
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.formatFechaP
import pe.edu.upeu.granturismojpc.repository.ActividadDetalleRepository
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import pe.edu.upeu.granturismojpc.repository.DestinoRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteDetalleRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.repository.ProveedorRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PaqueteFormViewModel @Inject constructor(
    private val packRepo: PaqueteRepository,
    private val provRepo: ProveedorRepository,
    private val destRepo: DestinoRepository,
    private val detalleRepo: PaqueteDetalleRepository,
    private val servicioRepo: ServicioRepository,
    private val actdetRepo: ActividadDetalleRepository,
    private val actividadRepo: ActividadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _paquete = MutableStateFlow<PaqueteRespBuscar?>(null)
    val paquete: StateFlow<PaqueteRespBuscar?> = _paquete

    private val _provs = MutableStateFlow<List<ProveedorResp>>(emptyList())
    val provs: StateFlow<List<ProveedorResp>> = _provs

    private val _dests = MutableStateFlow<List<DestinoResp>>(emptyList())
    val dests: StateFlow<List<DestinoResp>> = _dests

    private val _servicios = MutableStateFlow<List<ServicioResp>>(emptyList())
    val servicios: StateFlow<List<ServicioResp>> = _servicios

    private val _paqueteDetalles = MutableStateFlow<List<PaqueteDetalleResp>?>(null)
    val paqueteDetalles: StateFlow<List<PaqueteDetalleResp>?> = _paqueteDetalles

    private val _actividades = MutableStateFlow<List<ActividadResp>>(emptyList())
    val actividades: StateFlow<List<ActividadResp>> = _actividades

    private val _actividadDetalles = MutableStateFlow<List<ActividadDetalleResp>?>(null)
    val actividadDetalles: StateFlow<List<ActividadDetalleResp>?> = _actividadDetalles

    // Estados para manejo de imágenes
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _selectedImageFile = MutableLiveData<File?>()
    val selectedImageFile: LiveData<File?> = _selectedImageFile

    private val _currentImageUrl = MutableLiveData<String?>()
    val currentImageUrl: LiveData<String?> = _currentImageUrl

    // Estados para manejo de imágenes actividad
    private val _selectedImageUris = mutableStateMapOf<Int, Uri>()
    val selectedImageUris: Map<Int, Uri> get() = _selectedImageUris

    private val _selectedImageFiles = mutableStateMapOf<Int, File>()
    val selectedImageFiles: Map<Int, File> get() = _selectedImageFiles



    fun getPaquete(idX: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _paquete.value = packRepo.buscarPaqueteId(idX)
            _isLoading.value = false
        }
    }

    fun getPaqueteDetalles(idPaquete: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _paqueteDetalles.value = detalleRepo.buscarPaqueteDetallesByPaqueteId(idPaquete)
            _isLoading.value = false
        }
    }

    fun getActividadDetalles(idPaquete: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _actividadDetalles.value = actdetRepo.buscarActividadDetallesByPaqueteId(idPaquete)
            _isLoading.value = false
        }
    }

    fun getDatosPrevios() {
        viewModelScope.launch {
            _provs.value = provRepo.reportarProveedores()
            _dests.value = destRepo.reportarDestinos()
            _servicios.value = servicioRepo.reportarServicios()
            _actividades.value = actividadRepo.reportarActividades()
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

    fun setSelectedImageUriAct(index: Int, uri: Uri) {
        _selectedImageUris[index] = uri
        _selectedImageFiles.remove(index) // Evita conflictos
    }

    fun setSelectedImageFileAct(index: Int, file: File) {
        _selectedImageFiles[index] = file
        _selectedImageUris.remove(index)
    }

    fun clearSelectedImageAct(index: Int) {
        _selectedImageUris.remove(index)
        _selectedImageFiles.remove(index)
    }



    fun addPaqueteWithDetails(
        paquete: PaqueteDto,
        detalles: List<PaqueteDetalleDto>,
        actividaddetalles: List<ActividadDetalleDto>,
        context: Context? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val paqueteCreateDto = PaqueteCreateDto(
                titulo = paquete.titulo,
                descripcion = paquete.descripcion,
                //imagenUrl = paquete.imagenUrl,
                precioTotal = paquete.precioTotal,
                estado = paquete.estado,
                duracionDias = paquete.duracionDias,
                localidad = paquete.localidad,
                tipoActividad = paquete.tipoActividad,
                cuposMaximos = paquete.cuposMaximos,
                proveedor = paquete.proveedor,
                fechaInicio = paquete.fechaInicio,
                fechaFin = paquete.fechaFin,
                destino = paquete.destino
            )

            Log.i("REAL", "Creando paquete: $paqueteCreateDto")

            val insertSuccess = when {
                _selectedImageFile.value != null -> {
                    Log.i("PaqueteViewModel", "Creando con archivo de imagen")
                    packRepo.insertarPaqueteConImagen(paqueteCreateDto, _selectedImageFile.value!!)
                }
                _selectedImageUri.value != null -> {
                    Log.i("PaqueteViewModel", "Creando con URI de imagen")
                    packRepo.insertarPaqueteConImagenUri(paqueteCreateDto, _selectedImageUri.value!!, context)
                }
                else -> {
                    Log.i("PaqueteViewModel", "Creando sin imagen")
                    // Necesitarías implementar este metodo en el repositorio
                    packRepo.insertarPaqueteSinImagen(paqueteCreateDto, context)
                }
            }

            if (!insertSuccess) {
                //_operationResult.postValue(OperationResult.Error("Error al crear el paquete"))
                Log.e("PaqueteViewModel", "Error al crear el paquete")
                return@launch
            }

            val paquetesRecientes = packRepo.reportarPaquetes().first() // recoge la lista actual desde Room
            val paqueteCreado = paquetesRecientes.find {
                it.titulo == paquete.titulo &&
                        formatFechaP(it.fechaInicio)  == paquete.fechaInicio &&
                        formatFechaP(it.fechaFin) == paquete.fechaFin
            }

            // Si encontramos el paquete creado, guardar sus detalles
            paqueteCreado?.let { nuevoPaquete ->
                Log.i("REAL", "Paquete encontrado con ID: ${nuevoPaquete.idPaquete}")

                // Guardar detalles asociados al nuevo paquete
                detalles.forEach { detalle ->
                    val detalleCreateDto = PaqueteDetalleCreateDto(
                        cantidad = detalle.cantidad,
                        precioEspecial = detalle.precioEspecial,
                        paquete = nuevoPaquete.idPaquete,
                        servicio = detalle.servicio
                    )
                    detalleRepo.insertarPaqueteDetalle(detalleCreateDto)
                }
                actividaddetalles.forEachIndexed { index, detalle ->
                    val detalleCreateDto = ActividadDetalleCreateDto(
                        titulo = detalle.titulo,
                        descripcion = detalle.descripcion,
                        orden = detalle.orden,
                        paquete = nuevoPaquete.idPaquete,
                        actividad = detalle.actividad
                    )

                    val file = selectedImageFiles[index]
                    val uri = selectedImageUris[index]

                    val insertSuccess = when {
                        file != null -> {
                            actdetRepo.insertarActividadDetalleConImagen(detalleCreateDto, file)
                        }
                        uri != null -> {
                            actdetRepo.insertarActividadDetalleConImagenUri(detalleCreateDto, uri, context)
                        }
                        else -> {
                            actdetRepo.insertarActividadDetalleSinImagen(detalleCreateDto, context)
                        }
                    }

                    if (!insertSuccess) {
                        Log.e("ViewModel", "Error al crear el detalle de actividad")
                        return@launch
                    }
                }
            } ?: Log.e("ERROR", "No se pudo encontrar el paquete recién creado")

            _isLoading.value = false
        }
    }
    fun editPaqueteWithDetails(
        paquete: PaqueteDto,
        detalles: List<PaqueteDetalleDto>,
        actividaddetalles: List<ActividadDetalleDto>,
        context: Context? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            try {
                // Log para depuración
                Log.d("PaqueteViewModel", "Editando paquete: $paquete")
                Log.d("PaqueteViewModel", "Detalles a actualizar: ${detalles.size}")
                val hasNewImage = _selectedImageFile.value != null || _selectedImageUri.value != null
                // Verificar que los detalles tengan datos válidos
                val detallesValidos = detalles.filter { it.servicio > 0 }
                val actividadDetallesValidos = actividaddetalles.filter { it.actividad!! > 0 }
                Log.d("PaqueteViewModel", "Detalles válidos: ${detallesValidos.size}")

                // Actualizar el paquete principal
                val updateSuccess = when {
                    _selectedImageFile.value != null -> {
                        Log.i("PaqueteViewModel", "Actualizando con archivo de imagen")
                        packRepo.modificarPaqueteConImagen(paquete, _selectedImageFile.value!!)
                    }
                    _selectedImageUri.value != null -> {
                        Log.i("PaqueteViewModel", "Actualizando con URI de imagen")
                        packRepo.modificarPaqueteConImagenUri(paquete, _selectedImageUri.value!!, context)
                    }
                    else -> {
                        Log.i("PaqueteViewModel", "Actualizando sin cambiar imagen")
                        packRepo.modificarPaqueteSinImagen(paquete)
                    }
                }

                if (!updateSuccess) {
                    //_operationResult.postValue(OperationResult.Error("Error al actualizar el paquete"))
                    Log.e("PaqueteViewModel", "Error al actualizar el paquete")
                    return@launch
                }

                // Obtener detalles existentes
                val detallesExistentes = detalleRepo.buscarPaqueteDetallesByPaqueteId(paquete.idPaquete)
                Log.d("PaqueteViewModel", "Detalles existentes según la BD: ${detallesExistentes.size}")
                val actividadDetallesExistentes = actdetRepo.buscarActividadDetallesByPaqueteId(paquete.idPaquete)
                Log.d("PaqueteViewModel", "Actividades existentes según la BD: ${actividadDetallesExistentes.size}")

                // Procesamiento en dos fases para evitar problemas de concurrencia

                // FASE 1: Actualizar detalles existentes e insertar nuevos
                detallesValidos.forEach { detalle ->
                    val detalleConPaqueteId = detalle.copy(paquete = paquete.idPaquete)

                    if (detalle.idPaqueteDetalle == 0L) {
                        // Es un detalle nuevo
                        Log.d("PaqueteViewModel", "Insertando nuevo detalle: servicio=${detalle.servicio}")
                        addSinglePaqueteDetalle(detalleConPaqueteId)
                    } else {
                        // Es un detalle existente
                        // Verificar que realmente exista en la BD
                        val existe = detallesExistentes.any { it.idPaqueteDetalle == detalle.idPaqueteDetalle }

                        if (existe) {
                            Log.d("PaqueteViewModel", "Actualizando detalle existente ID=${detalle.idPaqueteDetalle}")
                            editSinglePaqueteDetalle(detalleConPaqueteId)
                        } else {
                            // Si tiene ID pero no existe en BD, es posible que sea un detalle que se eliminó y se está recreando
                            Log.d("PaqueteViewModel", "Detalle con ID=${detalle.idPaqueteDetalle} no existe, creando como nuevo")
                            addSinglePaqueteDetalle(detalleConPaqueteId.copy(idPaqueteDetalle = 0))
                        }
                    }
                }

                // FASE 2: Eliminar detalles que ya no existen en el formulario (solo después de procesar los detalles válidos)
                if (detallesExistentes.isNotEmpty()) {
                    val idsEnFormulario = detallesValidos
                        .filter { it.idPaqueteDetalle != 0L }
                        .map { it.idPaqueteDetalle }
                        .toSet()

                    // Solo eliminar detalles que existen en BD pero no en el formulario
                    detallesExistentes.forEach { detalleExistente ->
                        if (!idsEnFormulario.contains(detalleExistente.idPaqueteDetalle)) {
                            Log.d("PaqueteViewModel", "Eliminando detalle ID=${detalleExistente.idPaqueteDetalle}")
                            try {
                                detalleRepo.deletePaqueteDetalleId(detalleExistente.idPaqueteDetalle)
                            } catch (e: Exception) {
                                Log.e("PaqueteViewModel", "Error al eliminar detalle", e)
                            }
                        }
                    }
                }

                // Actividades

                // FASE 1: Actualizar detalles existentes e insertar nuevos
                actividadDetallesValidos.forEachIndexed { index,detalle ->
                    val detalleConPaqueteId = detalle.copy(paquete = paquete.idPaquete)

                    if (detalle.idActividadDetalle == 0L) {
                        // Es un detalle nuevo
                        Log.d("PaqueteViewModel", "Insertando nuevo detalle: actividad=${detalle.actividad}")
                        addSingleActividadDetalle(detalleConPaqueteId,index,context)
                    } else {
                        // Es un detalle existente
                        // Verificar que realmente exista en la BD
                        val existe = actividadDetallesExistentes.any { it.idActividadDetalle == detalle.idActividadDetalle }

                        if (existe) {
                            Log.d("PaqueteViewModel", "Actualizando detalle existente ID=${detalle.idActividadDetalle}")
                            editSingleActividadDetalle(detalleConPaqueteId,index,context)
                        } else {
                            // Si tiene ID pero no existe en BD, es posible que sea un detalle que se eliminó y se está recreando
                            Log.d("PaqueteViewModel", "Detalle con ID=${detalle.idActividadDetalle} no existe, creando como nuevo")
                            addSingleActividadDetalle(detalleConPaqueteId.copy(idActividadDetalle = 0),index,context)
                        }
                    }
                }

                // FASE 2: Eliminar detalles que ya no existen en el formulario (solo después de procesar los detalles válidos)
                if (actividadDetallesExistentes.isNotEmpty()) {
                    val idsEnFormulario = actividadDetallesValidos
                        .filter { it.idActividadDetalle != 0L }
                        .map { it.idActividadDetalle }
                        .toSet()

                    // Solo eliminar detalles que existen en BD pero no en el formulario
                    actividadDetallesExistentes.forEach { detalleExistente ->
                        if (!idsEnFormulario.contains(detalleExistente.idActividadDetalle)) {
                            Log.d("PaqueteViewModel", "Eliminando detalle ID=${detalleExistente.idActividadDetalle}")
                            try {
                                actdetRepo.deleteActividadDetalleId(detalleExistente.idActividadDetalle)
                            } catch (e: Exception) {
                                Log.e("PaqueteViewModel", "Error al eliminar detalle", e)
                            }
                        }
                    }
                }
                if (hasNewImage) {
                    clearSelectedImage()
                }
            } catch (e: Exception) {
                Log.e("PaqueteViewModel", "Error al editar paquete con detalles", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Método auxiliar para agregar un solo detalle (reutilizado de PaqueteDetalleViewModel)
    private suspend fun addSinglePaqueteDetalle(paqueteDetalle: PaqueteDetalleDto) {
        // Convertir PaqueteDetalleDto a PaqueteDetalleCreateDto para excluir el idPaqueteDetalle
        val paqueteDetalleCreateDto = PaqueteDetalleCreateDto(
            cantidad = paqueteDetalle.cantidad,
            precioEspecial = paqueteDetalle.precioEspecial,
            paquete = paqueteDetalle.paquete,
            servicio = paqueteDetalle.servicio,
        )

        Log.d("PaqueteViewModel", "Creando paqueteDetalle: $paqueteDetalleCreateDto")
        detalleRepo.insertarPaqueteDetalle(paqueteDetalleCreateDto)
    }

    // Método auxiliar para editar un solo detalle (reutilizado de PaqueteDetalleViewModel)
    private suspend fun editSinglePaqueteDetalle(paqueteDetalle: PaqueteDetalleDto) {
        Log.d("PaqueteViewModel", "Modificando paqueteDetalle: $paqueteDetalle")
        detalleRepo.modificarPaqueteDetalle(paqueteDetalle)
    }

    private suspend fun addSingleActividadDetalle(actividadDetalle: ActividadDetalleDto,index: Int,context: Context? = null) {
        // Convertir PaqueteDetalleDto a PaqueteDetalleCreateDto para excluir el idPaqueteDetalle
        val actividadDetalleCreateDto = ActividadDetalleCreateDto(
            titulo = actividadDetalle.titulo,
            descripcion = actividadDetalle.descripcion,
            //imagenUrl = actividadDetalle.imagenUrl,
            orden = actividadDetalle.orden,
            paquete = actividadDetalle.paquete,
            actividad = actividadDetalle.actividad,
        )

        Log.d("PaqueteViewModel", "Creando actividadDetalle: $actividadDetalleCreateDto")
        val file = selectedImageFiles[index]
        val uri = selectedImageUris[index]

        val insertSuccess = when {
            file != null -> {
                Log.i("ActividadDetalleViewModel", "Creando con archivo de imagen")
                actdetRepo.insertarActividadDetalleConImagen(actividadDetalleCreateDto, file)
            }
            uri != null -> {
                Log.i("ActividadDetalleViewModel", "Creando con URI de imagen")
                actdetRepo.insertarActividadDetalleConImagenUri(actividadDetalleCreateDto, uri, context)
            }
            else -> {
                Log.i("ActividadDetalleViewModel", "Creando sin imagen")
                actdetRepo.insertarActividadDetalleSinImagen(actividadDetalleCreateDto, context)
            }
        }

        if (!insertSuccess) {
            Log.e("ActividadDetalleViewModel", "Error al crear el detalle de actividad")
        }
    }

    // Método auxiliar para editar un solo detalle (reutilizado de PaqueteDetalleViewModel)
    private suspend fun editSingleActividadDetalle(actividadDetalle: ActividadDetalleDto,index: Int,context: Context? = null) {
        Log.d("PaqueteViewModel", "Modificando actividadDetalle: $actividadDetalle")
        val file = selectedImageFiles[index]
        val uri = selectedImageUris[index]

        val updateSuccess = when {
            file != null -> {
                Log.i("ActividadDetalleViewModel", "Actualizando con archivo de imagen")
                actdetRepo.modificarActividadDetalleConImagen(actividadDetalle, file)
            }
            uri != null -> {
                Log.i("ActividadDetalleViewModel", "Actualizando con URI de imagen")
                actdetRepo.modificarActividadDetalleConImagenUri(actividadDetalle, uri, context)
            }
            else -> {
                Log.i("ActividadDetalleViewModel", "Actualizando sin cambiar imagen")
                actdetRepo.modificarActividadDetalleSinImagen(actividadDetalle)
            }
        }

        if (!updateSuccess) {
            Log.e("ActividadDetalleViewModel", "Error al actualizar el actividadDetalle")
        }
    }


    fun addPaquete(paquete: PaqueteDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Convertir PaqueteDto a PaqueteCreateDto para excluir el idPaquete
            val paqueteCreateDto = PaqueteCreateDto(
                titulo = paquete.titulo,
                descripcion = paquete.descripcion,
                //imagenUrl = paquete.imagenUrl,
                precioTotal = paquete.precioTotal,
                estado = paquete.estado,
                duracionDias = paquete.duracionDias,
                localidad = paquete.localidad,
                tipoActividad = paquete.tipoActividad,
                cuposMaximos = paquete.cuposMaximos,
                proveedor = paquete.proveedor,
                fechaInicio = paquete.fechaInicio,
                fechaFin = paquete.fechaFin,
                destino = paquete.destino
            )

            Log.i("REAL", "Creando paquete: $paqueteCreateDto")
            //packRepo.insertarPaquete(paqueteCreateDto)
            _isLoading.value = false
        }
    }

    fun editPaquete(paquete: PaqueteDto){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.value = true
            //packRepo.modificarPaquete(paquete)
            _isLoading.value = false
        }
    }
}