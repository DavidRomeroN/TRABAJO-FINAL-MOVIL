package pe.edu.upeu.granturismojpc.ui.presentation.screens


//

//importaciones mapbox:

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.ui.presentation.components.CategoryChips
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.TopBar
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.PaqueteMainViewModel


@Composable
fun DetalleScreen(
    idPaquete: Long,
    viewModel: PaqueteMainViewModel = hiltViewModel(),
    navController: NavHostController,


    ) {
    // Observa el paquete desde el ViewModel
    val paqueteState = viewModel.buscarPorId(idPaquete).collectAsState(initial = null)

    //Valores
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDialog = { println("Abriendo diálogo de filtros...") }
    val displaySnackBar = { println("Mostrando snackbar...") }
    val searchQuery = remember { mutableStateOf("") }
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    // Interfaz simple
    /*Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        val paquete = paqueteState.value // Accedemos al valor real

        when {
            paquete == null -> {
                // Mientras se carga o si es null
                CircularProgressIndicator()
                Text("Cargando paquete...")
            }
            else -> {
                // Mostrar la información del paquete
                Text(text = "Título: ${paquete.titulo}", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Descripción: ${paquete.descripcion}")
                Text(text = "Precio: S/ ${paquete.precioTotal}")
                Text(text = "Descripcion: ${paquete.descripcion}")
                Text(text = "Imagen: ${paquete.imagenUrl}")
            }
        }
    }*/


    Scaffold(
        topBar = {
            TopBar(
                scope = scope,
                scaffoldState = drawerState,
                navController = navController,
                openDialog = openDialog,
                displaySnackBar = displaySnackBar
            )
        },
        bottomBar = {
            SimpleBottomNavigationBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navBarPadding)
                    .height(56.dp))
        },
    ) { innerPadding ->

        val paquete = paqueteState.value
        Log.i("TESTS",paquete.toString())

        // Usar LazyColumn en lugar de Column con verticalScroll
        LazyColumn(


            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplicar el padding del Scaffold
        ) {
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp) // un poco más de altura para dejar espacio a la barra
                ) {
                    // Imagen de fondo
                    AsyncImage(
                        model = paquete?.imagenUrl, // Asegúrate de que sea una URL válida desde Cloudinary
                        contentDescription = "Imagen del paquete turístico",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Gradiente
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    ),
                                    startY = 100f
                                )
                            )
                    )
// 🔙 Botón de retroceso
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    // Texto
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 56.dp
                            ) // deja espacio para la barra
                    ) {
                        when {
                            paquete == null -> {
                                CircularProgressIndicator()
                                Text("Cargando paquete...")
                            }

                            else -> {
                                Text(
                                    text = " ${paquete.titulo}",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                            }
                        }
                    }
                }
            }
            /*
            item {
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text("Buscar asociaciones") },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
            }*/
            item {
                CategoryChips(navController = navController)
            }

            item {
                Text(
                    text = "Descripción",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when {
                        paquete == null -> {
                            CircularProgressIndicator()
                            Text("Cargando paquete...")
                        }

                        else -> {
                            Row {
                                Text(text = "${paquete?.proveedor?.nombreCompleto ?: "Sin proveedor"}")
                                Text(text = " - ${paquete.estado}")
                                Text(text = " - ${paquete.duracionDias} dias")
                            }
                            Text(text = "${paquete.descripcion}")
                            Text(text = "Precio: ${paquete.precioTotal}")



                            Row {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "",
                                    tint = Color.Black,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = "${paquete?.destino?.ubicacion ?: "Ubicación no disponible"}")
                            }
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.Person4,
                                    contentDescription = "",
                                    tint = Color.Black,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = "${paquete.cuposMaximos} personas")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Ubicación",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
                )
            }
            item {
                paquete?.let {
                    MapScreen(
                        latitud = paquete?.destino?.latitud ?: 0.0,
                        longitud = paquete?.destino?.longitud ?: 0.0)
                } ?: run {
                    // Si el paquete es null o no tiene coordenadas, puedes mostrar un mensaje o un mapa por defecto
                    Text("Ubicación no disponible")
                }
            }


            item {
                LazyRow {
                    item {
                        Text(
                            text = "Ver las actividades",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
                        )

                        Button(
                            onClick = {
                                navController.navigate("actividades/${idPaquete}")

                            },
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFA726) // Color verde de WhatsApp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward, // Puedes usar otro ícono si deseas
                                contentDescription = "Actividades",
                                tint = Color.Black
                            )
                        }

                    }



                }
            }



        }
    }
}

@Composable
fun MapScreen(latitud: Double, longitud: Double) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                val punto = Point.fromLngLat(longitud, latitud)
                val mapboxMap = getMapboxMap()

                mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    // Establecer la cámara
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(punto)
                            .zoom(12.0)
                            .build()
                    )

                    // Cargar el bitmap desde drawable
                    val bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.red_marker // Asegúrate que exista este archivo en res/drawable
                    )

                    // Registrar el bitmap como imagen con nombre "custom-marker"
                    style.addImage("custom-marker", bitmap)

                    // Crear el manejador de anotaciones
                    val annotationApi = annotations
                    val pointAnnotationManager = annotationApi.createPointAnnotationManager()

                    // Configurar y crear el marcador
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(punto)
                        .withIconImage("custom-marker") // Usa el nombre registrado
                        .withIconSize(1.5)

                    pointAnnotationManager.create(pointAnnotationOptions)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}

