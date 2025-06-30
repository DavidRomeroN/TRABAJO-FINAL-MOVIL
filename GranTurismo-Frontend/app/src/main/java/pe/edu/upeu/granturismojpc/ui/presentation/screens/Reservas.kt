package pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.ActividadDetalleMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.PaqueteMainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun Reservas(
    idPaquete: Long,
    viewModel: PaqueteMainViewModel = hiltViewModel(),
    viewModelActividad: ActividadDetalleMainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val paqueteState = viewModel.buscarPorId(idPaquete).collectAsState(initial = null)
    val actividades by viewModelActividad.details.collectAsState()

    val paquete = paqueteState.value

    val datosListos = remember(paquete) {
        paquete != null
    }

    if (datosListos) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F0))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Reserva para el paquete: $idPaquete",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF4A4A4A)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE67E22), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "LUN", color = Color.White, fontSize = 12.sp)
                            Text(
                                text = "15",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(text = "Marzo de 2025", color = Color(0xFFE67E22), fontSize = 16.sp)
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Anfitrión",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = paquete?.titulo ?: "",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4A4A4A)
                        )
                        Text(
                            text = "Tu anfitrión de hoy ${paquete?.proveedor?.nombreCompleto}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Actividades",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF4A4A4A)
                )
            }

            items(actividades.filter { it.paquete?.idPaquete == idPaquete }) { actividad ->
                ExperienceItem(
                    icon = Icons.Filled.Star,
                    time = actividad.idActividadDetalle.toString(),
                    title = actividad.titulo,
                    description = actividad.descripcion ?: "Sin descripción",
                    backgroundColor = Color(0xFFFFF3E0)
                )
            }

            item {
                Text(
                    text = "Ubicación",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF4A4A4A)
                )
            }

            item {
                paquete?.destino?.latitud?.let { latitud ->
                    paquete.destino.longitud?.let { longitud ->
                        MapScreen(latitud = latitud, longitud = longitud)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Detalles de la reserva",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF4A4A4A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Timeline,
                                contentDescription = "Duración ",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${paquete?.duracionDias} Días",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Ubicación ",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = paquete?.localidad ?: "",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "S/.${paquete?.precioTotal}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFE67E22),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            item {
                val context = LocalContext.current
                val proveedor = paquete?.proveedor
                val numeroWhatsapp = proveedor?.telefono?.replace("+", "")?.replace(" ", "")
                Button(
                    onClick = {
                        numeroWhatsapp?.let {
                            val url = "https://wa.me/$it"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A085))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_dialog_email),
                            contentDescription = "Contactar",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Contactar al anfitrión", color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Volver")
                }
            }
        }
    } else {
        // Mientras se cargan los datos, puedes mostrar un loader
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
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
