package pe.edu.upeu.granturismojpc.ui.presentation.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.firstOrNull
import pe.edu.upeu.granturismojpc.ui.presentation.components.ReservaCarritoCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.ActividadDetalleMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito.CarritoMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.ServicioMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.ServicioAlimentacionMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania.ServicioArtesaniaMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera.ServicioHoteleraMainViewModel
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaCarritoScreen(
    idCarrito: Long,
    navController: NavHostController,
    viewModel: CarritoMainViewModel = hiltViewModel(),
    viewModelActividad: ActividadMainViewModel = hiltViewModel(),
    viewModelAlimentacion: ServicioAlimentacionMainViewModel = hiltViewModel(),
    viewModelArtesania: ServicioArtesaniaMainViewModel = hiltViewModel(),
    viewModelHoteleria: ServicioHoteleraMainViewModel = hiltViewModel()
    ) {
    val carritoItems by viewModel.provs.collectAsState()
    val context = LocalContext.current

    // Filtrar solo los ítems del carrito actual
    val itemsFiltrados = carritoItems.filter { it.carrito.usuario?.email == TokenUtils.USER_LOGIN }
    /*Log.i("idcarrito", "${idCarrito}")
    Log.i("Suma", "${itemsFiltrados}")
    // Calcular precio total sumando los precioBase
    val precioTotal = itemsFiltrados.sumOf {
        Log.i("Suma", "${it.servicio?.precioBase}")
        Log.i("Suma", "${it.actividad?.precioBase}")
        it.servicio?.precioBase ?: it.actividad?.precioBase ?: 0.0
    }*/
    var precioTotal by remember { mutableStateOf(0.0) }

    LaunchedEffect(itemsFiltrados) {
        var suma = 0.0
        for (item in itemsFiltrados) {
            suma += obtenerPrecioBase(
                cantidad = item.cantidadPersonas?:0,
                tipo = item.tipo,
                referenciaId = item.referenciaId,
                viewModelActividad = viewModelActividad,
                viewModelAlimentacion = viewModelAlimentacion,
                viewModelHoteleria = viewModelHoteleria,
                viewModelArtesania = viewModelArtesania
            )
        }
        precioTotal = suma
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Reservas") })
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F0))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(count = itemsFiltrados.size) { index ->

                val item = itemsFiltrados[index]
                Log.i("Carrito", "${item}")
                var infoElemento by remember(item.referenciaId, item.tipo) {
                    mutableStateOf<InfoElemento?>(null)
                }

                LaunchedEffect(item.referenciaId, item.tipo) {
                    val elemento = when (item.tipo) {
                        "actividad" -> viewModelActividad.buscarPorId(item.referenciaId).firstOrNull()
                            ?.let {
                                InfoElemento(
                                    nombre = it.titulo,
                                    descripcion = "Actividad",
                                    precio = it.precioBase,
                                    icono = "🎯",
                                    cantidadPersonas = item.cantidadPersonas?:0
                                )
                            }

                        "servicioAlimentacion" -> viewModelAlimentacion.buscarPorId(item.referenciaId).firstOrNull()
                            ?.let {
                                InfoElemento(
                                    nombre = it.servicio.nombreServicio,
                                    descripcion = "Servicio de Alimentación",
                                    precio = it.servicio.precioBase,
                                    icono = "🍽️",
                                    cantidadPersonas = item.cantidadPersonas?:0
                                )
                            }

                        "servicioHoteleria" -> viewModelHoteleria.buscarPorId(item.referenciaId).firstOrNull()
                            ?.let {
                                InfoElemento(
                                    nombre = it.servicio.nombreServicio,
                                    descripcion = "Servicio de Hotelería",
                                    precio = it.servicio.precioBase,
                                    icono = "🏨",
                                    cantidadPersonas = item.cantidadPersonas?:0
                                )
                            }

                        "servicioArtesania" -> viewModelArtesania.buscarPorId(item.referenciaId).firstOrNull()
                            ?.let {
                                InfoElemento(
                                    nombre = it.servicio.nombreServicio,
                                    descripcion = "Servicio de Artesanía",
                                    precio = it.servicio.precioBase,
                                    icono = "🛍️",
                                    cantidadPersonas = item.cantidadPersonas?:0
                                )
                            }

                        else -> null
                    }

                    infoElemento = elemento
                }

                if (infoElemento != null) {
                    ReservaCarritoCard(
                        time = "Horario no definido",
                        title = infoElemento!!.nombre,
                        description = infoElemento!!.descripcion,
                        price = "S/.${infoElemento!!.precio}",
                        unitPrice = infoElemento!!.precio,
                        personAmount = infoElemento!!.cantidadPersonas,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE1F5FE), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(infoElemento!!.icono, fontSize = 20.sp)
                            }
                        }
                    )
                } else {
                    // Mostrar placeholder mientras se carga
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            item {
                // Tarjeta resumen de reserva
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
                            Icon(Icons.Filled.Timeline, contentDescription = "Duración", tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Duración no definida", color = Color.Gray, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación", tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ubicación no definida", color = Color.Gray, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "S/.${"%.2f".format(precioTotal)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFE67E22),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            item {
                // Botón WhatsApp
                val mensaje = "Hola, deseo confirmar mi reserva del carrito ID: $idCarrito. Total: S/.${"%.2f".format(precioTotal)}"
                val numeroWhatsapp = "123456789" // Reemplazar con número real

                Button(
                    onClick = {
                        val numeroLimpio = numeroWhatsapp.replace("+", "").replace(" ", "")
                        val mensajeCodificado = Uri.encode(mensaje)
                        val url = "https://wa.me/$numeroLimpio?text=$mensajeCodificado"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
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
                            painter = painterResource(id = android.R.drawable.stat_sys_phone_call),
                            contentDescription = "Contactar",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contactar al anfitrión", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
suspend fun obtenerPrecioBase(
    cantidad: Int,
    tipo: String,
    referenciaId: Long,
    viewModelActividad: ActividadMainViewModel,
    viewModelAlimentacion: ServicioAlimentacionMainViewModel,
    viewModelHoteleria: ServicioHoteleraMainViewModel,
    viewModelArtesania: ServicioArtesaniaMainViewModel
): Double {
    val precioUnitario = when (tipo) {
        "actividad" -> viewModelActividad.buscarPorId(referenciaId).firstOrNull()?.precioBase ?: 0.0
        "servicioAlimentacion" -> viewModelAlimentacion.buscarPorId(referenciaId).firstOrNull()?.servicio?.precioBase ?: 0.0
        "servicioHoteleria" -> viewModelHoteleria.buscarPorId(referenciaId).firstOrNull()?.servicio?.precioBase ?: 0.0
        "servicioArtesania" -> viewModelArtesania.buscarPorId(referenciaId).firstOrNull()?.servicio?.precioBase ?: 0.0
        else -> 0.0
    }

    return precioUnitario * cantidad
}
data class InfoElemento(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val icono: String,
    val cantidadPersonas: Int
)