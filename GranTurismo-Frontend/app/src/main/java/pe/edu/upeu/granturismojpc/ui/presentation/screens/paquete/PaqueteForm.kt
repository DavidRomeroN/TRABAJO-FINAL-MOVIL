package pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.model.ActividadDetalleDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleResp
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteDto
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteRespBuscar
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxThre
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxTwo
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.DateTimePickerCustom
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyEasyFormsCustomStringResult
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyEasyFormsCustomStringState
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.ActividadDetalleFormViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@Composable
fun PaqueteForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: PaqueteFormViewModel= hiltViewModel()
) {
    val paquete by viewModel.paquete.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val proveedores by viewModel.provs.collectAsState()
    val destinos by viewModel.dests.collectAsState()
    val servicios by viewModel.servicios.collectAsState()
    val actividades by viewModel.actividades.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var paqueteD: PaqueteDto
    if (text!="0"){
        paqueteD = Gson().fromJson(text, PaqueteDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getPaquete(paqueteD.idPaquete)
        }
        paquete?.let {
            paqueteD=it.toDto()
            Log.i("DMPX","Paquete: ${paqueteD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        paqueteD= PaqueteDto(
            0, "", "", 0.0,  "", 0, "", "", 0, 0, ahora, ahora, 0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        paqueteD.idPaquete!!,
        darkMode,
        navController,
        paquete,
        paqueteD,
        viewModel,
        proveedores,
        destinos,
        servicios,
        actividades
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission",
    "CoroutineCreationDuringComposition"
)
@Composable
fun formulario(
    id: Long,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    paqueteR: PaqueteRespBuscar?,
    paquete: PaqueteDto,
    viewModel: PaqueteFormViewModel,
    listProveedor: List<ProveedorResp>,
    listDestino: List<DestinoResp>,
    listServicio: List<ServicioResp>,
    listActividad: List<ActividadResp>
){
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val pack= PaqueteDto(
        0, "", "", 0.0,  "", 0, "", "", 0, 0, ahora, ahora, 0
    )
    val detallesPaquete = remember { mutableStateListOf<PaqueteDetalleDto>() }
    val detallesActividad = remember { mutableStateListOf<ActividadDetalleDto>() }
    val detallesActividadR = remember { mutableStateListOf<ActividadDetalleResp>() }
    LaunchedEffect(key1 = id) {
        if (id != 0L) {
            viewModel.getPaqueteDetalles(id)
            viewModel.getActividadDetalles(id)
        }
    }
    val detallesExistentes by viewModel.paqueteDetalles.collectAsState()
    val actividadDetallesExistentes by viewModel.actividadDetalles.collectAsState()
    val actividadDetallesExistentesR by viewModel.actividadDetalles.collectAsState()

    LaunchedEffect(key1 = detallesExistentes) {
        detallesExistentes?.let { existingDetails ->
            detallesPaquete.clear()
            detallesPaquete.addAll(existingDetails.map {
                PaqueteDetalleDto(
                    idPaqueteDetalle = it.idPaqueteDetalle,
                    cantidad = it.cantidad,
                    precioEspecial = it.precioEspecial,
                    paquete = id,
                    servicio = it.servicio.idServicio
                )
            })
        }
    }

    LaunchedEffect(key1 = actividadDetallesExistentes) {
        actividadDetallesExistentes?.let { existingDetails ->
            detallesActividad.clear()
            detallesActividad.addAll(existingDetails.map {
                ActividadDetalleDto(
                    idActividadDetalle = it.idActividadDetalle,
                    titulo = it.titulo,
                    descripcion = it.descripcion,
                    //imagenUrl = it.imagenUrl,
                    orden = it.orden,
                    paquete = id,
                    actividad = it.actividad?.idActividad
                )
            })
        }
    }
    LaunchedEffect(key1 = actividadDetallesExistentesR) {
        actividadDetallesExistentesR?.let { existingDetails ->
            detallesActividadR.clear()
            detallesActividadR.addAll(existingDetails.map {
                ActividadDetalleResp(
                    idActividadDetalle = it.idActividadDetalle,
                    titulo = it.titulo,
                    descripcion = it.descripcion,
                    imagenUrl = it.imagenUrl,
                    orden = it.orden,
                    paquete = it.paquete,
                    actividad = it.actividad
                )
            })
        }
    }

    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ImagePickerSection(
                    viewModel = viewModel,
                    currentImageUrl = paqueteR?.imagenUrl
                )

                // Separador
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
                NameTextField(easyForms = easyForm, text=paquete?.titulo!!,"Nomb. Paquete:", MyFormKeys.NAME )
                val listM: List<ComboModel> = listProveedor.map { proveedor ->
                    ComboModel(code = proveedor.idProveedor.toString(), name = proveedor.nombreCompleto)
                }
                ComboBox(easyForm = easyForm, "Proveedor:", paquete?.proveedor?.let { it.toString() } ?: "", listM)
                val listD: List<ComboModel> = listDestino.map { destino ->
                    ComboModel(code = destino.idDestino.toString(), name = destino.nombre)
                }
                ComboBoxTwo(easyForm = easyForm, "Destino:", paquete?.destino?.let { it.toString() } ?: "", listD)

                NameTextField(easyForms = easyForm, text=paquete?.descripcion.toString()!!,"Descripción:", MyFormKeys.DESCRIPTION )
                NameTextField(easyForms = easyForm, text=paquete?.precioTotal.toString()!!,"Precio:", MyFormKeys.PU )
                //NameTextField(easyForms = easyForm, text=paquete?.imagenUrl.toString()!!,"ImagenURL:", MyFormKeys.URL )
                //NameTextField(easyForms = easyForm, text=paquete?.estado.toString()!!,"Estado:", MyFormKeys.STOCK )

                val listEstado: List<ComboModel> = listOf(
                    ComboModel(code = "DISPONIBLE", name = "Disponible"),
                    ComboModel(code = "AGOTADO", name = "Agotado")
                )
                val valorInicial = paquete?.estado?.let { it.toString() } ?:"DISPONIBLE"
                ComboBoxThre(
                    easyForm = easyForm, label = "Estado", textv = valorInicial,
                    list = listEstado
                )

                NameTextField(easyForms = easyForm, text=paquete?.duracionDias.toString()!!,"Duración en días:", MyFormKeys.UTILIDAD )

                NameTextField(easyForms = easyForm, text=paquete?.localidad.toString()!!,"Localidad:", MyFormKeys.LOCATION )
                NameTextField(easyForms = easyForm, text=paquete?.tipoActividad.toString()!!,"Tipo de Acividad:", MyFormKeys.ACTIVIDADID )
                NameTextField(easyForms = easyForm, text=paquete?.cuposMaximos.toString()!!,"Cupos Máximos:", MyFormKeys.PU_OLD )
                //NameTextField(easyForms = easyForm, text=paquete?.proveedorId.toString()!!,"Proveedor:", MyFormKeys.UTILIDAD )
                DateTimePickerCustom(easyForm = easyForm, label = "Fecha de inicio:", texts = paquete?.fechaInicio ?: "", key = MyFormKeys.FECHA, formDP = "yyyy-MM-dd HH:mm:ss")
                DateTimePickerCustom(easyForm = easyForm, label = "Fecha de Fin:", texts = paquete?.fechaFin ?: "", key = MyFormKeys.DATE2, formDP = "yyyy-MM-dd HH:mm:ss")
                //NameTextField(easyForms = easyForm, text=paquete?.fechaInicio.toString()!!,"Fecha de inicio:", MyFormKeys.FECHA )
                //NameTextField(easyForms = easyForm, text=paquete?.fechaFin.toString()!!,"Fecha Fin:", MyFormKeys.DATE2 )
                //NameTextField(easyForms = easyForm, text=paquete?.destino.toString()!!,"ID Destino:", MyFormKeys.MODFH )

                // Separador visual
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Sección para agregar detalles de paquete
                Text(
                    text = "Servicios incluidos en el paquete",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Lista de detalles de paquete
                detallesPaquete.forEachIndexed { index, detalle ->
                    PaqueteDetalleItem(
                        detalle = detalle,
                        index = index,
                        listServicio = listServicio,
                        onDetalleChanged = { newDetalle ->
                            detallesPaquete[index] = newDetalle
                        },
                        onRemove = {
                            detallesPaquete.removeAt(index)
                        }
                    )
                }

                // Botón para agregar nuevo detalle de paquete
                Button(
                    onClick = {
                        // Agregar un nuevo PaqueteDetalleDto vacío a la lista
                        detallesPaquete.add(
                            PaqueteDetalleDto(
                                idPaqueteDetalle = 0,
                                cantidad = 1,
                                precioEspecial = 0.0,
                                paquete = id,
                                servicio = 0
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar servicio",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer()
                    Text("Agregar servicio")
                }

                // Separador visual
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Sección para agregar actividad detalles de paquete
                Text(
                    text = "Actividades incluidos en el paquete",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Lista de actividad detalles de paquete
                val minSize = minOf(detallesActividad.size, detallesActividadR.size)

                for (index in 0 until minSize) {
                    ActividadDetalleItem(
                        detalle = detallesActividad[index],
                        detalleR = detallesActividadR[index],
                        index = index,
                        viewModel = viewModel,
                        listActividad = listActividad,
                        onDetalleChanged = { newDetalle ->
                            detallesActividad[index] = newDetalle
                        },
                        onRemove = {
                            detallesActividad.removeAt(index)
                            detallesActividadR.removeAt(index) // sincronización
                        }
                    )
                }

                // Botón para agregar nuevo detalle de paquete
                Button(
                    onClick = {
                        // Agregar un nuevo PaqueteDetalleDto vacío a la lista
                        detallesActividad.add(
                            ActividadDetalleDto(
                                idActividadDetalle = 0,
                                titulo = "",
                                descripcion = "",
                                //imagenUrl = "",
                                orden = 0,
                                paquete = id,
                                actividad = 0
                            )
                        )
                        detallesActividadR.add(
                            ActividadDetalleResp(
                                idActividadDetalle = 0,
                                titulo = "",
                                descripcion = "",
                                imagenUrl = "", // o null, si es aceptado
                                orden = 0,
                                paquete = null,
                                actividad = null
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar actividad",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer()
                    Text("Agregar actividad")
                }

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        pack.titulo=(lista.get(0) as EasyFormsResult.StringResult).value
                        pack.proveedor= (splitCadena((lista.get(1) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        pack.destino= (splitCadena((lista.get(2) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        //pack.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        pack.descripcion=((lista.get(3) as EasyFormsResult.StringResult).value)
                        pack.precioTotal=((lista.get(4) as EasyFormsResult.StringResult).value).toDouble()
                        pack.estado=(splitCadena((lista.get(5) as EasyFormsResult.GenericStateResult<String>).value))
                        pack.duracionDias=((lista.get(6) as EasyFormsResult.StringResult).value).toInt()
                        pack.localidad=((lista.get(7) as EasyFormsResult.StringResult).value)
                        pack.tipoActividad=((lista.get(8) as EasyFormsResult.StringResult).value)
                        pack.cuposMaximos=((lista.get(9) as EasyFormsResult.StringResult).value).toInt()
                        //pack.proveedorId=((lista.get(7) as EasyFormsResult.StringResult).value).toLong()
                        pack.fechaInicio = (lista.get(10) as MyEasyFormsCustomStringResult).value
                        pack.fechaFin = (lista.get(11) as MyEasyFormsCustomStringResult).value
                        //pack.destino=((lista.get(12) as EasyFormsResult.StringResult).value).toLong()
                        //pack.fechaInicio=((lista.get(8) as EasyFormsResult.StringResult).value)
                        //pack.fechaFin=((lista.get(9) as EasyFormsResult.StringResult).value)

                        if (!checkFormularioValido(detallesPaquete)) {
                            // Mostrar un mensaje de error al usuario
                            Log.i("ADV", "Selecciona un servicio para cada detalle")
                            return@AccionButtonSuccess
                        }

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ pack.proveedor)
                            //Log.i("AGREGAR", "VI:"+ pack.stock)
                            //viewModel.addPaquete(pack)
                            viewModel.addPaqueteWithDetails(pack, detallesPaquete.toList(), detallesActividad.toList(),context)
                        }else{
                            pack.idPaquete=id
                            Log.i("MODIFICAR", "M:"+pack)
                            //viewModel.editPaquete(pack)
                            val detallesCorregidos = detallesPaquete.map { it.copy(paquete = id) }
                            val actividadDetallesCorregidos = detallesActividad.map { it.copy(paquete = id) }
                            viewModel.editPaqueteWithDetails(pack, detallesCorregidos, actividadDetallesCorregidos,context)
                        }
                        navController.navigate(Destinations.PaqueteMainSC.route){
                            popUpTo(Destinations.PaqueteFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.PaqueteMainSC.route)
                    }
                }
            }
        }
    }

}
fun checkFormularioValido(detalles: List<PaqueteDetalleDto>): Boolean {
    // Verificar que todos los detalles tengan un servicio válido seleccionado
    val detallesInvalidos = detalles.filter { it.servicio <= 0 }

    if (detallesInvalidos.isNotEmpty()) {
        Log.w("PaqueteForm", "Hay ${detallesInvalidos.size} detalles sin servicio seleccionado")
        return false
    }

    return true
}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}

@Composable
fun PaqueteDetalleItem(
    detalle: PaqueteDetalleDto,
    index: Int,
    listServicio: List<ServicioResp>,
    onDetalleChanged: (PaqueteDetalleDto) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Servicio ${index + 1}" + if (detalle.idPaqueteDetalle != 0L) " (ID: ${detalle.idPaqueteDetalle})" else "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar servicio",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Crear lista de ComboModel para el dropdown
            val listComboModel: List<ComboModel> = listServicio.map { servicio ->
                ComboModel(code = servicio.idServicio.toString(), name = servicio.nombreServicio)
            }

            // ID del servicio actual
            val currentServicioId = detalle.servicio

            // Buscar el servicio correspondiente
            val currentServicio = listServicio.find { it.idServicio == currentServicioId }

            // Texto a mostrar en el dropdown
            val displayName = currentServicio?.nombreServicio ?: "Seleccionar servicio"

            // Valor formateado para MyEasyFormsCustomStringState
            val formattedValue = if (currentServicio != null) {
                val code = currentServicio.idServicio.toString()
                val name = currentServicio.nombreServicio
                if (code == name) code else "$code-$name"
            } else {
                ""
            }

            // Estado del dropdown usando la clase proporcionada
            val servicioDropdownState = remember(currentServicioId) {
                MyEasyFormsCustomStringState(
                    defaultValue = formattedValue,
                    validData = listComboModel
                )
            }

            // Log para depuración
            LaunchedEffect(servicioDropdownState.state.value) {
                Log.d("PaqueteDetalle", "Servicio state changed: ${servicioDropdownState.state.value}")
                Log.d("PaqueteDetalle", "Current detalle: $detalle")

                if (servicioDropdownState.state.value.isNotEmpty()) {
                    val servicioId = splitCadena(servicioDropdownState.state.value).toLongOrNull() ?: 0L
                    // Solo actualizar si realmente cambió
                    if (servicioId != detalle.servicio) {
                        Log.d("PaqueteDetalle", "Updating servicio from ${detalle.servicio} to $servicioId")
                        onDetalleChanged(detalle.copy(servicio = servicioId))
                    }
                }
            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Servicio:",
                    style = MaterialTheme.typography.labelSmall
                )

                OutlinedButton(
                    onClick = servicioDropdownState.onClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(displayName)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar opciones"
                    )
                }

                DropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    expanded = servicioDropdownState.isOpen.value,
                    onDismissRequest = servicioDropdownState.onDismissed
                ) {
                    listComboModel.forEach { comboItem ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = {
                                Text(
                                    text = comboItem.name,
                                    modifier = Modifier.wrapContentWidth()
                                )
                            },
                            onClick = {
                                val formattedValue = if (comboItem.code == comboItem.name)
                                    comboItem.code
                                else
                                    "${comboItem.code}-${comboItem.name}"

                                servicioDropdownState.onValueChangedCallback(formattedValue)
                            }
                        )
                    }
                }
            }

            var cantidadText by remember { mutableStateOf(detalle.cantidad.toString()) }
            OutlinedTextField(
                value = cantidadText,
                onValueChange = {
                    cantidadText = it
                    try {
                        val cantidad = it.toInt()
                        onDetalleChanged(detalle.copy(cantidad = cantidad.toLong()))
                    } catch (e: NumberFormatException) {
                        // Manejar error de conversión
                    }
                },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            var precioText by remember { mutableStateOf(detalle.precioEspecial.toString()) }
            OutlinedTextField(
                value = precioText,
                onValueChange = {
                    precioText = it
                    try {
                        val precio = it.toDouble()
                        onDetalleChanged(detalle.copy(precioEspecial = precio))
                    } catch (e: NumberFormatException) {
                        // Manejar error de conversión
                    }
                },
                label = { Text("Precio Especial") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}

@Composable
fun ActividadDetalleItem(
    detalle: ActividadDetalleDto,
    detalleR: ActividadDetalleResp,
    index: Int,
    viewModel: PaqueteFormViewModel,
    listActividad: List<ActividadResp>,
    onDetalleChanged: (ActividadDetalleDto) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actividad ${index + 1}" + if (detalle.idActividadDetalle != 0L) " (ID: ${detalle.idActividadDetalle})" else "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar actividad",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Crear lista de ComboModel para el dropdown
            val listComboModel: List<ComboModel> = listActividad.map { actividad ->
                ComboModel(code = actividad.idActividad.toString(), name = actividad.titulo)
            }

            // ID del actividad actual
            val currentActividadId = detalle.actividad

            // Buscar el actividad correspondiente
            val currentActividad = listActividad.find { it.idActividad == currentActividadId }

            // Texto a mostrar en el dropdown
            val displayName = currentActividad?.titulo ?: "Seleccionar actividad"

            // Valor formateado para MyEasyFormsCustomStringState
            val formattedValue = if (currentActividad != null) {
                val code = currentActividad.idActividad.toString()
                val name = currentActividad.titulo
                if (code == name) code else "$code-$name"
            } else {
                ""
            }

            // Estado del dropdown usando la clase proporcionada
            val actividadDropdownState = remember(currentActividadId) {
                MyEasyFormsCustomStringState(
                    defaultValue = formattedValue,
                    validData = listComboModel
                )
            }

            // Log para depuración
            LaunchedEffect(actividadDropdownState.state.value) {
                Log.d("ActividadDetalle", "Actividad state changed: ${actividadDropdownState.state.value}")
                Log.d("ActividadDetalle", "Current detalle: $detalle")

                if (actividadDropdownState.state.value.isNotEmpty()) {
                    val actividadId = splitCadena(actividadDropdownState.state.value).toLongOrNull() ?: 0L
                    // Solo actualizar si realmente cambió
                    if (actividadId != detalle.actividad) {
                        Log.d("ActividadDetalle", "Updating actividad from ${detalle.actividad} to $actividadId")
                        onDetalleChanged(detalle.copy(actividad = actividadId))
                    }
                }
            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Actividad:",
                    style = MaterialTheme.typography.labelSmall
                )

                OutlinedButton(
                    onClick = actividadDropdownState.onClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(displayName)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar opciones"
                    )
                }

                DropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    expanded = actividadDropdownState.isOpen.value,
                    onDismissRequest = actividadDropdownState.onDismissed
                ) {
                    listComboModel.forEach { comboItem ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = {
                                Text(
                                    text = comboItem.name,
                                    modifier = Modifier.wrapContentWidth()
                                )
                            },
                            onClick = {
                                val formattedValue = if (comboItem.code == comboItem.name)
                                    comboItem.code
                                else
                                    "${comboItem.code}-${comboItem.name}"

                                actividadDropdownState.onValueChangedCallback(formattedValue)
                            }
                        )
                    }
                }
            }
            ImagePickerSectionAct(
                index = index,
                viewModel = viewModel,
                currentImageUrl = detalleR.imagenUrl,
            )

            // Separador
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
            var ordenText by remember { mutableStateOf(detalle.orden.toString()) }
            OutlinedTextField(
                value = ordenText,
                onValueChange = {
                    ordenText = it
                    try {
                        val orden = it.toInt()
                        onDetalleChanged(detalle.copy(orden = orden))
                    } catch (e: NumberFormatException) {
                        // Manejar error de conversión
                    }
                },
                label = { Text("Orden") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            var tituloText by remember { mutableStateOf(detalle.titulo) }
            OutlinedTextField(
                value = tituloText,
                onValueChange = {
                    tituloText = it
                    try {
                        val titulo = it
                        onDetalleChanged(detalle.copy(titulo = titulo))
                    } catch (e: NumberFormatException) {
                        // Manejar error de conversión
                    }
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            var descripcionText by remember { mutableStateOf(detalle.descripcion) }
            OutlinedTextField(
                value = descripcionText,
                onValueChange = {
                    descripcionText = it
                    try {
                        val descripcion = it
                        onDetalleChanged(detalle.copy(descripcion = descripcion))
                    } catch (e: NumberFormatException) {
                        // Manejar error de conversión
                    }
                },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            /*var imagenText by remember { mutableStateOf(detalle.imagenUrl) }
            OutlinedTextField(
                value = imagenText,
                onValueChange = {
                    imagenText = it
                    try {
                        val imagenUrl = it
                        onDetalleChanged(detalle.copy(imagenUrl = imagenUrl))
                    } catch (e: NumberFormatException) {
                        // Manejar error de conversión
                    }
                },
                label = { Text("URL de Imagen") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )*/
        }
    }
}

@Composable
fun ImagePickerSection(
    viewModel: PaqueteFormViewModel,
    currentImageUrl: String? = null
) {
    val context = LocalContext.current
    val selectedImageUri by viewModel.selectedImageUri.observeAsState()
    val selectedImageFile by viewModel.selectedImageFile.observeAsState()

    // Launcher para seleccionar imagen de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImageUri(it) }
    }

    // Launcher para tomar foto con cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            viewModel.clearSelectedImage()
        }
    }

    // Launcher para permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Crear archivo temporal para la foto
            val photoFile = createImageFile(context)
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            viewModel.setSelectedImageFile(photoFile)
            cameraLauncher.launch(photoUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Imagen del paquete",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Mostrar imagen actual, seleccionada o placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImageUri != null -> {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                selectedImageFile != null -> {
                    AsyncImage(
                        model = selectedImageFile,
                        contentDescription = "Imagen capturada",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                currentImageUrl != null -> {
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "Imagen actual",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sin imagen",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón de galería
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer()
                Text("Galería")
            }
            // Botón de limpiar (solo si hay imagen seleccionada)
            if (selectedImageUri != null || selectedImageFile != null) {
                OutlinedButton(
                    onClick = { viewModel.clearSelectedImage() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ImagePickerSectionAct(
    index: Int,
    viewModel: PaqueteFormViewModel,
    currentImageUrl: String? = null
) {
    val context = LocalContext.current
    val selectedImageUri = viewModel.selectedImageUris[index]
    val selectedImageFile = viewModel.selectedImageFiles[index]

    // Launcher galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setSelectedImageUriAct(index, it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            viewModel.clearSelectedImageAct(index)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = createImageFile(context)
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            viewModel.setSelectedImageFileAct(index, photoFile)
            cameraLauncher.launch(photoUri)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Imagen del paquete",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Mostrar imagen actual, seleccionada o placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImageUri != null -> {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                selectedImageFile != null -> {
                    AsyncImage(
                        model = selectedImageFile,
                        contentDescription = "Imagen capturada",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                currentImageUrl != null -> {
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "Imagen actual",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sin imagen",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón de galería
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer()
                Text("Galería")
            }
            // Botón de limpiar (solo si hay imagen seleccionada)
            if (selectedImageUri != null || selectedImageFile != null) {
                OutlinedButton(
                    onClick = {
                        viewModel.clearSelectedImageAct(index)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Función auxiliar para crear archivo de imagen temporal
private fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}