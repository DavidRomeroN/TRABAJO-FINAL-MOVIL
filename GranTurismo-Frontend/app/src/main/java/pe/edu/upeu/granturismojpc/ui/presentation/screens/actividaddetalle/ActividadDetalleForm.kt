package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.ActividadDetalleDto
import pe.edu.upeu.granturismojpc.model.ActividadDetalleResp
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxTwo
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadFormViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@Composable
fun ActividadDetalleForm(
    text: String,
    packId: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ActividadDetalleFormViewModel= hiltViewModel()
) {
    val actividadDetalle by viewModel.actividadDetalle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val packs by viewModel.packs.collectAsState()
    val actvs by viewModel.actvs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var actividadDetalleD: ActividadDetalleDto
    if (text!="0"){
        actividadDetalleD = Gson().fromJson(text, ActividadDetalleDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getActividadDetalle(actividadDetalleD.idActividadDetalle)
        }
        actividadDetalle?.let {
            actividadDetalleD=it.toDto()
            Log.i("DMPX","ActividadDetalle: ${actividadDetalleD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        actividadDetalleD= ActividadDetalleDto(
            0, "", "", 0, packId.toLongOrNull() ?: 0L, 0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        actividadDetalleD.idActividadDetalle!!,
        darkMode,
        navController,
        actividadDetalle,
        actividadDetalleD,
        viewModel,
        actvs,
        packs,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission",
    "CoroutineCreationDuringComposition"
)
@Composable
fun formulario(id:Long,
               darkMode: MutableState<Boolean>,
               navController: NavHostController,
               detalle: ActividadDetalleResp?,
               actividadDetalle: ActividadDetalleDto,
               viewModel: ActividadDetalleFormViewModel,
               listActividad: List<ActividadResp>,
               listPaquete: List<PaqueteResp>,
){
    val context = LocalContext.current
    val paqueteId = actividadDetalle.paquete.toString()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val serv= ActividadDetalleDto(
        0, "", "", 0, actividadDetalle.paquete, 0
    )
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ImagePickerSection(
                    viewModel = viewModel,
                    currentImageUrl = detalle?.imagenUrl
                )

                // Separador
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
                val listA: List<ComboModel> = listActividad.map { actividad ->
                    ComboModel(code = actividad.idActividad.toString(), name = actividad.titulo)
                }
                ComboBox(easyForm = easyForm, "Actividad:", actividadDetalle?.actividad?.let { it.toString() } ?: "", listA)
                /*val listP: List<ComboModel> = listPaquete.map { paquete ->
                    ComboModel(code = paquete.idPaquete.toString(), name = paquete.titulo)
                }
                ComboBoxTwo(easyForm = easyForm, "Paquete:", actividadDetalle?.paquete?.let { it.toString() } ?: "", listP)
                */
                Text(
                    text = "Paquete: ${paqueteId}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                NameTextField(easyForms = easyForm, text=actividadDetalle?.titulo.toString()!!,"Título:", MyFormKeys.NAME )
                NameTextField(easyForms = easyForm, text=actividadDetalle?.descripcion.toString()!!,"Descripción:", MyFormKeys.PU_OLD )
                //NameTextField(easyForms = easyForm, text=actividadDetalle?.imagenUrl.toString()!!,"URL de Imagen:", MyFormKeys.URL )
                NameTextField(easyForms = easyForm, text=actividadDetalle?.orden.toString()!!,"Orden:", MyFormKeys.UTILIDAD )


                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        serv.actividad= (splitCadena((lista.get(0) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        serv.paquete= paqueteId.toLong()
                        //serv.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        serv.titulo=((lista.get(1) as EasyFormsResult.StringResult).value)
                        serv.descripcion=((lista.get(2) as EasyFormsResult.StringResult).value)
                        //serv.imagenUrl=((lista.get(3) as EasyFormsResult.StringResult).value)
                        serv.orden=((lista.get(3) as EasyFormsResult.StringResult).value).toInt()

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "A:"+ serv.actividad)
                            Log.i("AGREGAR", "P:"+ serv.paquete)
                            //Log.i("AGREGAR", "VI:"+ serv.stock)
                            viewModel.addActividadDetalle(serv,context)
                        }else{
                            serv.idActividadDetalle=id
                            Log.i("MODIFICAR", "M:"+serv)
                            viewModel.editActividadDetalle(serv,context)
                        }
                        navController.navigate(Destinations.ActividadDetalleMainSC.passId(paqueteId)){
                            popUpTo(Destinations.ActividadDetalleFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ActividadDetalleMainSC.passId(paqueteId))
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}
@Composable
fun ImagePickerSection(
    viewModel: ActividadDetalleFormViewModel,
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

// Función auxiliar para crear archivo de imagen temporal
private fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}