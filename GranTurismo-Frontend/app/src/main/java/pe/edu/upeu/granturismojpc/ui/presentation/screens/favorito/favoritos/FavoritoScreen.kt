package pe.edu.upeu.granturismojpc.ui.presentation.screens.favorito.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.FavoritoResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.ui.presentation.components.PaqueteCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.AlimentacionCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.ServicioAlimentacionMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania.ArtesaniaCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania.ServicioArtesaniaMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera.HoteleriaCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera.ServicioHoteleraMainViewModel

@Composable
fun FavoritoScreen(
    viewModel: FavoritoViewModel = hiltViewModel(),
    viewModelAct: ActividadMainViewModel= hiltViewModel(),
    viewModelAli: ServicioAlimentacionMainViewModel= hiltViewModel(),
    viewModelArt: ServicioArtesaniaMainViewModel= hiltViewModel(),
    viewModelHot: ServicioHoteleraMainViewModel= hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    val searchQuery = remember { mutableStateOf("") }
    val tipoSeleccionado = remember { mutableStateOf("actividad") }
    val favoritos by viewModel.favoritosFiltrados.collectAsState()
    var favoritosFiltrados = remember { mutableStateOf<List<Favorito>>(emptyList()) }

    LaunchedEffect(tipoSeleccionado.value) {
        viewModel.cargarFavoritosPorTipo(tipoSeleccionado.value)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row (
            modifier = Modifier
                .padding(top = 65.dp, end = 22.dp, start = 22.dp),
        ){
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Text(
                text = "Favoritos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top=16.dp, end = 22.dp, start = 22.dp, bottom = 5.dp)
            )
        }

        // Barra de búsqueda
        /*
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                //viewModel.filtrarFavoritos(it)
            },
            label = { Text("Buscar favorito") },
            modifier = Modifier.fillMaxWidth()
                .padding(top = 35.dp, end = 22.dp, start = 22.dp),
            shape = RoundedCornerShape(20.dp),
            singleLine = true
        )*/
        favoritosFiltrados.value = favoritos.filter {
                    it.tipo.contains(searchQuery.value, ignoreCase = true) ||
                    it.referenciaId.toString().contains(searchQuery.value)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botones de filtro por tipo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val tipos = listOf(
                "actividad" to "Actividad",
                "servicioAlimentacion" to "Alimentación",
                "servicioArtesania" to "Artesanía",
                "servicioHoteleria" to "Hospedaje"
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                // Primera fila: actividad y alimentacion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tipos.subList(0, 2).forEach { (tipo, label) ->
                        Button(
                            onClick = {
                                tipoSeleccionado.value = tipo
                                searchQuery.value = ""
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tipoSeleccionado.value == tipo) Color.Gray else Color.LightGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = label)
                        }
                    }
                }

                // Segunda fila: artesania y hoteleria
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tipos.subList(2, 4).forEach { (tipo, label) ->
                        Button(
                            onClick = {
                                tipoSeleccionado.value = tipo
                                searchQuery.value = ""
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tipoSeleccionado.value == tipo) Color.Gray else Color.LightGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = label)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lista de favoritos
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favoritosFiltrados.value.size) { favorito ->
                val favoritox = favoritos.get(favorito)

                when (favoritox.tipo) {
                    /*
                    "paquete" -> {
                        val paquete = produceState<PaqueteResp?>(initialValue = null) {
                            value = viewModel.getPaqueteById(favorito.referenciaId)
                        }.value
                        paquete?.let {
                            PaqueteCard(paquete = it, navController = navController)
                        }
                    }*/

                    "actividad" -> {
                        val actividad = produceState<ActividadResp?>(initialValue = null) {
                            value = viewModel.getActividadById(favoritox.referenciaId)
                        }.value
                        actividad?.let {
                            ActividadCard(navController = navController, actividadx = it, context = context, favoritos = favoritos, viewModel = viewModelAct )
                        }
                    }

                    "servicioAlimentacion" -> {
                        val almuerzo = produceState<ServicioAlimentacionResp?>(initialValue = null) {
                            value = viewModel.getAlimentacionById(favoritox.referenciaId)
                        }.value
                        almuerzo?.let {
                            AlimentacionCard(navController = navController, servicioAlimentacionx = it, context = context, favoritos = favoritos, viewModel = viewModelAli)
                        }
                    }

                    "servicioArtesania" -> {
                        val artesania = produceState<ServicioArtesaniaResp?>(initialValue = null) {
                            value = viewModel.getArtesaniaById(favoritox.referenciaId)
                        }.value
                        artesania?.let {
                            ArtesaniaCard(navController = navController, servicioArtesaniax = it, context = context, favoritos = favoritos, viewModel = viewModelArt)
                        }
                    }

                    "servicioHoteleria" -> {
                        val hospedaje = produceState<ServicioHoteleraResp?>(initialValue = null) {
                            value = viewModel.getHoteleriaById(favoritox.referenciaId)
                        }.value
                        hospedaje?.let {
                            HoteleriaCard(navController = navController, servicioHotelerax = it, context = context, favoritos = favoritos, viewModel = viewModelHot)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FavoritoItem(favorito: Favorito, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier
            .padding(16.dp)
            .clickable {
                if (favorito.tipo == "paquete") {
                    navController.navigate("detallePaquete/${favorito.referenciaId}")
                }
                // Agregar navegación para otros tipos si es necesario
            }) {
            Text("ID: ${favorito.referenciaId} - Tipo: ${favorito.tipo}")
        }
    }
}

