package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.detalle

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.ServicioMainViewModel
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun ServicioDetalle(
    idServicio: Long,
    navController: NavHostController,
    viewModel: ServicioMainViewModel = hiltViewModel()
) {
    var servicio: Any? by remember { mutableStateOf(null) }
    var tipoDetectado by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(idServicio) {
        viewModel.buscarAlimentacionPorId(idServicio).firstOrNull()?.let {
            servicio = it
            tipoDetectado = "Alimentación"
            return@LaunchedEffect
        }
        viewModel.buscarArtesaniaPorId(idServicio).firstOrNull()?.let {
            servicio = it
            tipoDetectado = "Cultural"
            return@LaunchedEffect
        }
        viewModel.buscarHoteleraPorId(idServicio).firstOrNull()?.let {
            servicio = it
            tipoDetectado = "Alojamiento"
        }
    }

    when (tipoDetectado) {
        "Alimentación" -> DetalleAlimentacionView(servicio as ServicioAlimentacionResp, navController, viewModel)
        "Cultural" -> DetalleArtesaniaView(servicio as ServicioArtesaniaResp, navController, viewModel)
        "Alojamiento" -> DetalleHoteleriaView(servicio as ServicioHoteleraResp, navController, viewModel)
        null -> Text("Cargando servicio...")
        else -> Text("No se encontró el servicio.")
    }
}
private fun onFavoritoClick(
    viewModel: ServicioMainViewModel,
    id: Long,
    tipo: String,
    yaEsFavorito: Boolean
) {
    if (yaEsFavorito) {
        viewModel.eliminarFavorito(id,tipo)
    } else {
        viewModel.agregarFavorito(id,tipo)
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetalleAlimentacionView(
    serv: ServicioAlimentacionResp,
    navController: NavHostController,
    viewModel: ServicioMainViewModel
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val favoritos by viewModel.favs.collectAsState()
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos("servicioAlimentacion")
        try {
            val result = viewModel.buscarCarritoItemPorTipo(serv.idAlimentacion,"servicioAlimentacion")
            carritoItem = result.first()
        } catch (e: Exception) {
            // Si lanza "CarritoItem no encontrado", simplemente mantenemos null
            carritoItem = null
        }
    }

    val esFavorito = favoritos.any {
        it.referenciaId == serv.idAlimentacion && it.tipo == "servicioAlimentacion"
                && it.usuario == TokenUtils.USER_ID
    }
    Scaffold(
        bottomBar = {
            SimpleBottomNavigationBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navBarPadding)
                    .height(56.dp))
        },
        modifier = Modifier,
        floatingActionButtonPosition = FabPosition.End,
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Detalle del Servicio de Alimentación",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 80.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoItem(label = "Nombre", value = serv.servicio.nombreServicio, icon = Icons.Default.Restaurant)
                InfoItem(label = "Precio base", value = "S/.${serv.servicio.precioBase}", icon = Icons.Default.AttachMoney)
                InfoItem(label = "Estilo gastronómico", value = serv.estiloGastronomico, icon = Icons.Default.LocalDining)
                InfoItem(label = "Tipo de comida", value = serv.tipoComida, icon = Icons.Default.Fastfood)
                InfoItem(label = "Incluye bebidas", value = if (serv.incluyeBebidas=="SI") "Sí" else "No", icon = Icons.Default.LocalDrink)
            }
                Spacer()
                if(TokenUtils.USER_ROLE=="USER"){
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clickable {
                            onFavoritoClick(viewModel, serv.idAlimentacion, "servicioAlimentacion", esFavorito)
                        }
                )}
            }
            if(TokenUtils.USER_ROLE=="USER"){
            val label = if (carritoItem != null) "Ver carrito" else "Agregar al carrito"
            TextButton(
                onClick = {
                    if (carritoItem != null)
                        navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
                    else
                        navController.navigate(Destinations.CarritoFormSC.passId("0",serv.idAlimentacion.toString(),"servicioAlimentacion"))

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }}
        }

    }
}}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetalleArtesaniaView(
    serv: ServicioArtesaniaResp,
    navController: NavHostController,
    viewModel: ServicioMainViewModel
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val favoritos by viewModel.favs.collectAsState()
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos("servicioArtesania")
        try {
            val result = viewModel.buscarCarritoItemPorTipo(serv.idArtesania,"servicioArtesania")
            carritoItem = result.first()
        } catch (e: Exception) {
            // Si lanza "CarritoItem no encontrado", simplemente mantenemos null
            carritoItem = null
        }
    }

    val esFavorito = favoritos.any {
        it.referenciaId == serv.idArtesania && it.tipo == "servicioArtesania"
                && it.usuario == TokenUtils.USER_ID
    }
    Scaffold(
        bottomBar = {
            SimpleBottomNavigationBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navBarPadding)
                    .height(56.dp))
        },
        modifier = Modifier,
        floatingActionButtonPosition = FabPosition.End,
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Detalle del Servicio de Artesanía",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 80.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoItem(label = "Nombre", value = serv.servicio.nombreServicio, icon = Icons.Default.Handyman)
                InfoItem(label = "Precio base", value = "S/.${serv.servicio.precioBase}", icon = Icons.Default.AttachMoney)
                InfoItem(label = "Tipo de artesanía", value = serv.tipoArtesania, icon = Icons.Default.Category)
                InfoItem(label = "Nivel de dificultad", value = serv.nivelDificultad, icon = Icons.Default.BarChart)
                InfoItem(label = "Origen cultural", value = serv.origenCultural, icon = Icons.Default.Public)
            }
                Spacer()
                if(TokenUtils.USER_ROLE=="USER"){
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clickable {
                            onFavoritoClick(viewModel, serv.idArtesania, "servicioArtesania", esFavorito)
                        }
                )}
            }
            if(TokenUtils.USER_ROLE=="USER"){
            val label = if (carritoItem != null) "Ver carrito" else "Agregar al carrito"
            TextButton(
                onClick = {
                    if (carritoItem != null)
                        navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
                    else
                        navController.navigate(Destinations.CarritoFormSC.passId("0",serv.idArtesania.toString(),"servicioArtesania"))

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }}
        }
    }}
}

@Composable
fun DetalleHoteleriaView(
    serv: ServicioHoteleraResp,
    navController: NavHostController,
    viewModel: ServicioMainViewModel
    ) {
    val favoritos by viewModel.favs.collectAsState()
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos("servicioHoteleria")
        try {
            val result = viewModel.buscarCarritoItemPorTipo(serv.idHoteleria,"servicioHoteleria")
            carritoItem = result.first()
        } catch (e: Exception) {
            // Si lanza "CarritoItem no encontrado", simplemente mantenemos null
            carritoItem = null
        }
    }

    val esFavorito = favoritos.any {
        it.referenciaId == serv.idHoteleria && it.tipo == "servicioHoteleria"
                && it.usuario == TokenUtils.USER_ID
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Detalle del Servicio Hotelero",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 80.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoItem(label = "Nombre", value = serv.servicio.nombreServicio, icon = Icons.Default.Hotel)
                InfoItem(label = "Precio base", value = "S/.${serv.servicio.precioBase}", icon = Icons.Default.AttachMoney)
                InfoItem(label = "Estrellas", value = "${serv.estrellas} ★", icon = Icons.Default.Star)
                InfoItem(label = "Tipo de habitación", value = serv.tipoHabitacion, icon = Icons.Default.KingBed)
                InfoItem(
                    label = "Incluye desayuno",
                    value = if (serv.incluyeDesayuno=="Si") "Sí" else "No",
                    icon = Icons.Default.FreeBreakfast
                )
            }
                Spacer()
                if(TokenUtils.USER_ROLE=="USER"){
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clickable {
                            onFavoritoClick(viewModel, serv.idHoteleria, "servicioHoteleria", esFavorito)
                        }
                )}
            }
            if(TokenUtils.USER_ROLE=="USER"){
            val label = if (carritoItem != null) "Ver carrito" else "Agregar al carrito"
            TextButton(
                onClick = {
                    if (carritoItem != null)
                        navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
                    else
                        navController.navigate(Destinations.CarritoFormSC.passId("0",serv.idHoteleria.toString(),"servicioHoteleria"))

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }}
        }
    }
}


@Composable
fun InfoItem(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}