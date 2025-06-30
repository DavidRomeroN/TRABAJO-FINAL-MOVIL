package pe.edu.upeu.granturismojpc.ui.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.first
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.TopBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ActividadDtoCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.home.HomeViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI.ClimaRecomendacionViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI.RecomendacionViewModel
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun RecomendacionScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDialog = { println("Abriendo diálogo de filtros...") }
    val displaySnackBar = { println("Mostrando snackbar...") }
    val favoritos by viewModel.favs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }
    val recomendacionViewModel: RecomendacionViewModel = hiltViewModel()
    val recomendacionState by recomendacionViewModel.uiState.collectAsState()
    val climaRecomendacionViewModel: ClimaRecomendacionViewModel = hiltViewModel()
    val climaState by climaRecomendacionViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos()
        recomendacionViewModel.cargarRecomendaciones()
        climaRecomendacionViewModel.cargarRecomendacionesPorClima()
    }

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
                    .height(56.dp)
            )


        }
    ) { innerPadding ->
        // Usar LazyColumn en lugar de Column con verticalScroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplicar el padding del Scaffold
        ) {
            item {
                Row {
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
                        text = "🌤 ${strings.recomTitle}",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)


                    )
                }
            }

            items(climaState.actividades) { actividad ->
                val esFavorito = favoritos.any {
                    it.referenciaId == actividad.idActividad && it.tipo == "actividad"
                }

                ActividadDtoCard(
                    actividad = actividad,
                    esFavorito = esFavorito,
                    onFavoritoClick = { actividad, yaEsFavorito ->
                        if (yaEsFavorito) {
                            viewModel.eliminarFavoritoActividad(actividad.idActividad)
                        } else {
                            viewModel.agregarFavoritoActividad(actividad.idActividad)
                        }
                    },
                    onAñadirClick = { actividad ->
                        navController.navigate(
                            Destinations.CarritoFormSC.passId(
                                servId = "0",
                                refId = actividad.idActividad.toString(),
                                tipo = "actividad"
                            )

                        )
                    },
                    navController = navController,
                    viewModel = viewModel
                )


            }

            item {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFe8f5e9)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🤖 ${strings.recomAI}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = recomendacionState.mensajeIA,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                color = Color(0xFF2E7D32)
                            )
                        )
                    }
                }
            }

// ✅ AQUÍ se corrige el error:
            items(recomendacionState.actividades) { actividad ->
                val esFavorito = favoritos.any {
                    it.referenciaId == actividad.idActividad && it.tipo == "actividad"
                }
                LaunchedEffect(actividad.idActividad) {
                    try {
                        val result = viewModel.buscarCarritoItemPorTipo(actividad.idActividad,"actividad")
                        carritoItem = result.first()
                    } catch (e: Exception) {
                        // Si lanza "CarritoItem no encontrado", simplemente mantenemos null
                        carritoItem = null
                    }
                }

                ActividadDtoCard(
                    actividad = actividad,
                    esFavorito = esFavorito,
                    onFavoritoClick = { actividad, yaEsFavorito ->
                        if (yaEsFavorito) {
                            viewModel.eliminarFavoritoActividad(actividad.idActividad)
                        } else {
                            viewModel.agregarFavoritoActividad(actividad.idActividad)
                        }
                    },
                    onAñadirClick = { actividad ->
                        navController.navigate(
                            Destinations.CarritoFormSC.passId(
                                servId = "0",
                                refId = actividad.idActividad.toString(),
                                tipo = "actividad"
                            )
                        )
                    },
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}