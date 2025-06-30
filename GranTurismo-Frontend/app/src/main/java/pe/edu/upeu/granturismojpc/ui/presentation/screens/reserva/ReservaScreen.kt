package pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.ReservaCreateDto

import pe.edu.upeu.granturismojpc.ui.presentation.screens.MapScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.ActividadDetalleMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.PaqueteMainViewModel
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ReservaScreen(
    idPaquete: Long,
    viewModel: PaqueteMainViewModel = hiltViewModel(),
    viewModelActividad: ActividadDetalleMainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val paqueteState = viewModel.buscarPorId(idPaquete).collectAsState(initial = null)
    val actividades by viewModelActividad.details.collectAsState()

    val paquete = paqueteState.value
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getLong("usuarioId", 0L)
    val reservaViewModel: ReservaViewModel = hiltViewModel()

    var cantidadPersonas by remember { mutableStateOf(1) }
    val aforoMaximo = paquete?.cuposMaximos ?: 1
    var expanded by remember { mutableStateOf(false) }
    // Dentro de tu Composable:
    val coroutineScope = rememberCoroutineScope()

    var botonHabilitado by remember { mutableStateOf(true)}


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
                        Text(text = "15", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = "Marzo de 2025", color = Color(0xFFE67E22), fontSize = 16.sp)
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text(text = paquete?.titulo ?: "", fontWeight = FontWeight.SemiBold, color = Color(0xFF4A4A4A))
                    Text(text = "Tu anfitrión de hoy ${paquete?.proveedor?.nombreCompleto}", color = Color.Gray, fontSize = 12.sp)
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
                    Text(text = "Detalles de la reserva", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF4A4A4A))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timeline, contentDescription = "Duración", tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${paquete?.duracionDias} Días", color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación", tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = paquete?.localidad ?: "", color = Color.Gray, fontSize = 14.sp)
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
            Column {
                Text("Seleccionar cantidad de personas:")
                Box {
                    Button(onClick = { expanded = true }) {
                        Text("Personas: $cantidadPersonas")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        (1..aforoMaximo).forEach { cantidad ->
                            DropdownMenuItem(
                                text = { Text("$cantidad") },
                                onClick = {
                                    cantidadPersonas = cantidad
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            val proveedor = paquete?.proveedor
            val numeroWhatsapp = proveedor?.telefono?.replace("+", "")?.replace(" ", "")
            val mensaje = "😊Hola ${proveedor?.nombreCompleto}, estoy interesado en tu paquete ${paquete?.titulo}" +
                    " con el precio ${paquete?.precioTotal}💵💵 ¿Hay cupos disponibles?🚌"
            Button(
                onClick = {
                    numeroWhatsapp?.let {
                        val mensajeCodificado = Uri.encode(mensaje)
                        val url = "https://wa.me/$it?text=$mensajeCodificado"
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
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val coroutineScope = rememberCoroutineScope()
            var botonHabilitado by remember { mutableStateOf(true) }

            val userId = TokenUtils.USER_ID  // Asegúrate de que este valor esté disponible
            val claveDesbloqueo = "horaDesbloqueo_$userId"

            LaunchedEffect(Unit) {
                val horaDesbloqueo = prefs.getLong(claveDesbloqueo, 0L)
                val ahora = System.currentTimeMillis()

                if (horaDesbloqueo > ahora) {
                    botonHabilitado = false
                    val tiempoRestante = horaDesbloqueo - ahora
                    delay(tiempoRestante)
                    botonHabilitado = true
                    prefs.edit().remove(claveDesbloqueo).apply()
                } else {
                    botonHabilitado = true
                    prefs.edit().remove(claveDesbloqueo).apply()
                }
            }

            Button(
                onClick = {
                    val ahora = LocalDateTime.now()
                    val fechaInicio = ahora
                    val fechaFin = ahora.plusDays(1)

                    val nuevaReserva = ReservaCreateDto(
                        fechaInicio = fechaInicio.toString(),
                        fechaFin = fechaFin.toString(),
                        estado = "PENDIENTE",
                        cantidadPersonas = cantidadPersonas,
                        observaciones = null,
                        usuario = userId,
                        paquete = paquete?.idPaquete
                    )

                    reservaViewModel.crearReserva(nuevaReserva)
                    Toast.makeText(context, "Reserva registrada", Toast.LENGTH_SHORT).show()

                    val bloqueoDuracionMs = 3 * 60 * 1000L // 3 minutos
                    val horaDesbloqueo = System.currentTimeMillis() + bloqueoDuracionMs
                    prefs.edit().putLong(claveDesbloqueo, horaDesbloqueo).apply()

                    botonHabilitado = false

                    coroutineScope.launch {
                        delay(bloqueoDuracionMs)
                        botonHabilitado = true
                        prefs.edit().remove(claveDesbloqueo).apply()
                    }
                },
                enabled = botonHabilitado,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (botonHabilitado) Color(0xFFE67E22) else Color.Gray
                )
            ) {
                Text(text = if (botonHabilitado) "Reservar" else "Espera...", color = Color.White, fontSize = 16.sp)
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
}

@Composable
fun ExperienceItem(
    icon: ImageVector,
    time: String,
    title: String,
    description: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

