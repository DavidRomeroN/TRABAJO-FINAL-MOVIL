package pe.edu.upeu.granturismojpc.ui.presentation.screens.home


import android.util.Log
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.CategoryChips
import pe.edu.upeu.granturismojpc.ui.presentation.components.HeroSection
import pe.edu.upeu.granturismojpc.ui.presentation.components.PaqueteCard
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.TopBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ActividadDtoCard
import pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI.ClimaRecomendacionViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI.RecomendacionViewModel
import pe.edu.upeu.granturismojpc.ui.strings.Strings
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun HomeScreen(
    navegarPantalla2: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,

    ) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDialog = { println("Abriendo diálogo de filtros...") }
    val displaySnackBar = { println("Mostrando snackbar...") }
    val searchQuery = remember { mutableStateOf("") }
    val paquetes by viewModel.paquetes.collectAsState()
    val favoritos by viewModel.favs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }

    val datosListos = remember{mutableStateOf(isLoading)}
    var paquetesFiltrados = remember { mutableStateOf<List<PaqueteResp>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.sincronizarUnaVez()
        viewModel.cargarFavoritos()
    }

    var cargadoPorPrimeraVez by remember { mutableStateOf(false) }

    LaunchedEffect(paquetes) {
        if (!cargadoPorPrimeraVez && paquetes.isNotEmpty()) {
            cargadoPorPrimeraVez = true
            viewModel.cargarPaquetesBuscados(paquetes)
        }
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
                    .height(56.dp))


        }
    ) { innerPadding ->
        if (!datosListos.value) {
            Log.i("LOADING","Ya no está cargando")
        // Usar LazyColumn en lugar de Column con verticalScroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplicar el padding del Scaffold
        ) {
            item {
                HeroSection(
                    searchQuery = searchQuery.value,
                    onSearchChange = { searchQuery.value = it }
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text(text = strings.searchPaquetes) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
                paquetesFiltrados.value = paquetes.filter {
                    it.titulo.contains(searchQuery.value, ignoreCase = true) ||
                            it.proveedorNombre.contains(searchQuery.value, ignoreCase = true) == true //||
                    //it.marca.nombre.contains(searchQuery.value, ignoreCase = true)
                }
            }
            item {
                CategoryChips(navController = navController)
            }

            item {
                Row {
                    Text(
                        text = strings.touristAs,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
                    )
                    Button(
                        onClick = {
                            viewModel.sincronizarManual()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Autorenew,
                            contentDescription = "Refrescar",)
                    }
                }

            }

            // Muestra los paquetes
            itemsIndexed(paquetesFiltrados.value) { _, paquete ->
                val esFavorito = favoritos.any {
                    it.referenciaId == paquete.idPaquete && it.tipo == "paquete"
                }
                val paqueteBuscar = viewModel.paquetesPorId.value[paquete.idPaquete]
                paqueteBuscar?.let {
                PaqueteCard(
                    paquete = paquete,
                    paqueteBuscar = it,
                    esFavorito = esFavorito,
                    navController = navController,
                    onFavoritoClick = { paqueteSeleccionado, yaEsFavorito ->
                        if (yaEsFavorito) {
                            viewModel.eliminarFavorito(paqueteSeleccionado.idPaquete)
                        } else {
                            viewModel.agregarFavorito(paqueteSeleccionado.idPaquete)
                        }
                    }
                )}
            }



        }}else{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}



