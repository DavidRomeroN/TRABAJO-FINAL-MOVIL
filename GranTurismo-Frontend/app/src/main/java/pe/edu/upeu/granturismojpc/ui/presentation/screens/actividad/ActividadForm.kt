package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad

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
import pe.edu.upeu.granturismojpc.model.ActividadDto
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.splitCadena
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@Composable
fun ActividadForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ActividadFormViewModel= hiltViewModel()
) {
    val actividad by viewModel.actividad.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    //val tipos by viewModel.tipos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var actividadD: ActividadDto
    if (text!="0"){
        actividadD = Gson().fromJson(text, ActividadDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getActividad(actividadD.idActividad)
        }
        actividad?.let {
            actividadD=it.toDto()
            Log.i("DMPX","Actividad: ${actividadD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        actividadD= ActividadDto(
            0, "", "", "", 0, 0.0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        actividadD.idActividad!!,
        darkMode,
        navController,
        actividad,
        actividadD,
        viewModel,
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
    actividadR: ActividadResp?,
    actividad: ActividadDto,
    viewModel: ActividadFormViewModel,
){
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val actv= ActividadDto(
        0, "", "", "", 0, 0.0
    )
    val tipoActividadList = listOf(
        ComboModel("CULTURAL", "CULTURAL"),
        ComboModel("GASTRONOMICO", "GASTRONOMICO"),
        ComboModel("RELIGIOSO", "RELIGIOSO"),
        ComboModel("ENTRETENIMIENTO", "ENTRETENIMIENTO"),
        ComboModel("AVENTURA", "AVENTURA"),
        ComboModel("FOTOGRAFIA", "FOTOGRAFIA"),
        ComboModel("ACUATICO", "ACUATICO"),
        ComboModel("RELAJANTE", "RELAJANTE")
    )
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ImagePickerSection(
                    viewModel = viewModel,
                    currentImageUrl = actividadR?.imagenUrl
                )

                // Separador
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
                NameTextField(easyForms = easyForm, text=actividad?.titulo!!,"Título de la Actividad:", MyFormKeys.NAME )
                NameTextField(easyForms = easyForm, text=actividad?.descripcion.toString()!!,"Descripción:", MyFormKeys.DESCRIPTION )
                //DateTimePickerCustom(easyForm = easyForm, label = "Fecha de registro:", texts = actividad?.fechaRegistro ?: "", key = MyFormKeys.FECHA, formDP = "yyyy-MM-dd HH:mm:ss")
                NameTextField(easyForms = easyForm, text=actividad?.precioBase.toString()!!,"Precio Base:", MyFormKeys.PU_OLD )
                //NameTextField(easyForms = easyForm, text=actividad?.imagenUrl.toString()!!,"URL de Imagen:", MyFormKeys.URL )
                ComboBox(
                    easyForm = easyForm,
                    label = "Tipo:",
                    textv = actividad?.tipo ?: "",
                    list = tipoActividadList
                )

                NameTextField(easyForms = easyForm, text=actividad?.duracionHoras.toString()!!,"Duracion (en horas):", MyFormKeys.HORAREG )


                //NameTextField(easyForms = easyForm, text=actividad?.fechaInicio.toString()!!,"Fecha de inicio:", MyFormKeys.FECHA )
                //NameTextField(easyForms = easyForm, text=actividad?.fechaFin.toString()!!,"Fecha Fin:", MyFormKeys.DATE2 )
                //NameTextField(easyForms = easyForm, text=actividad?.destino.toString()!!,"ID Destino:", MyFormKeys.MODFH )

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        actv.titulo=(lista.get(0) as EasyFormsResult.StringResult).value
                        actv.descripcion=(lista.get(1) as EasyFormsResult.StringResult).value
                        //actv.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        actv.precioBase=((lista.get(2) as EasyFormsResult.StringResult).value).toDouble()
                        //actv.imagenUrl=((lista.get(3) as EasyFormsResult.StringResult).value)
                        actv.tipo=(splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value))
                        actv.duracionHoras=((lista.get(4) as EasyFormsResult.StringResult).value).toLong()

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ actv.tipo)
                            //Log.i("AGREGAR", "VI:"+ actv.stock)
                            viewModel.addActividad(actv,context)
                        }else{
                            actv.idActividad=id
                            Log.i("MODIFICAR", "M:"+actv)
                            viewModel.editActividad(actv,context)
                        }
                        navController.navigate(Destinations.ActividadMainSC.route){
                            popUpTo(Destinations.ActividadFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ActividadMainSC.route)
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
    viewModel: ActividadFormViewModel,
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