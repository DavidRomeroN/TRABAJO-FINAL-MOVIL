package pe.edu.upeu.granturismojpc.ui.presentation.screens.destino

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.DestinoDto
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.*
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadFormViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ImagePickerSection
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.splitCadena
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DestinoForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: DestinoFormViewModel = hiltViewModel()
) {
    val destino by viewModel.destino.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var destinoD: DestinoDto
    if (text != "0") {
        destinoD = Gson().fromJson(text, DestinoDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getDestino(destinoD.idDestino)
        }
        destino?.let {
            destinoD = it.toDto()
            Log.i("DestinoX", "Destino: ${destinoD.toString()}")
        }
    } else {
        destinoD = DestinoDto(0, "", "", "", 0.0, 0.0, 0, 0.0, 0.0)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formularioDestino(
        id = destinoD.idDestino,
        darkMode = darkMode,
        navController = navController,
        destinoR = destino,
        destino = destinoD,
        viewModel = viewModel

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun formularioDestino(
    id: Long,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    destinoR: DestinoResp?,
    destino: DestinoDto,
    viewModel: DestinoFormViewModel
) {
    val context = LocalContext.current
    val destinoForm = DestinoDto(0, "", "", "", 0.0, 0.0, 0, 0.0, 0.0)

    Scaffold(
        modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
    ) {
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {


                ImagePickerSection(
                    viewModel = viewModel,
                    currentImageUrl = destinoR?.imagenUrl
                )

                NameTextField(easyForms = easyForm, text = destino.nombre, "Nombre", MyFormKeys.NAME)
                NameTextField(easyForms = easyForm, text = destino.descripcion, "Descripción", MyFormKeys.DESCRIPTION)
                NameTextField(easyForms = easyForm, text = destino.ubicacion, "Ubicación", MyFormKeys.UTILIDAD)
                //NameTextField(easyForms = easyForm, text = destino.imagenUrl, "Imagen URL", MyFormKeys.URL)
                NameTextField(easyForms = easyForm, text = destino.latitud.toString(), "Latitud", MyFormKeys.PU)
                NameTextField(easyForms = easyForm, text = destino.longitud.toString(), "Longitud", MyFormKeys.PU_OLD)
                NameTextField(easyForms = easyForm, text = destino.popularidad.toString(), "Popularidad", MyFormKeys.STOCK)
                NameTextField(easyForms = easyForm, text = destino.precioMedio.toString(), "Precio Medio", MyFormKeys.SLIDER)
                NameTextField(easyForms = easyForm, text = destino.rating.toString(), "Rating", MyFormKeys.RANGE_SLIDER)

                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id) {
                        val lista = easyForm.formData()
                        val nuevoDestino = DestinoDto(
                            idDestino = if (id != 0L) id else 0,
                            nombre = (lista[0] as EasyFormsResult.StringResult).value,
                            descripcion = (lista[1] as EasyFormsResult.StringResult).value,
                            ubicacion = (lista[2] as EasyFormsResult.StringResult).value,
                            //imagenUrl = (lista[3] as EasyFormsResult.StringResult).value,
                            latitud = (lista[4] as EasyFormsResult.StringResult).value.toDoubleOrNull() ?: 0.0,
                            longitud = (lista[5] as EasyFormsResult.StringResult).value.toDoubleOrNull() ?: 0.0,
                            popularidad = (lista[6] as EasyFormsResult.StringResult).value.toIntOrNull() ?: 0,
                            precioMedio = (lista[7] as EasyFormsResult.StringResult).value.toDoubleOrNull() ?: 0.0,
                            rating = (lista[8] as EasyFormsResult.StringResult).value.toDoubleOrNull() ?: 0.0
                        )

                        if (id == 0L) {
                            viewModel.addDestino(nuevoDestino, context)
                        } else {
                            viewModel.editDestino(nuevoDestino)
                        }
                        navController.navigate(Destinations.DestinoMainSC.route){
                            popUpTo(Destinations.DestinoFormSC.route) { inclusive = true }
                        }
                    }

                    Spacer()

                    AccionButtonCancel(easyForms = easyForm, "Cancelar") {
                        navController.navigate(Destinations.DestinoMainSC.route)
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
    viewModel: DestinoFormViewModel,
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