package pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import pe.edu.upeu.granturismojpc.ui.presentation.components.ReservaCard
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pantalla4(
    navController: NavHostController,
    reservaViewModel: ReservaViewModel = hiltViewModel()
) {
    val isLoading = reservaViewModel.isLoading.collectAsState()
    val reservas = reservaViewModel.reservas.collectAsState()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas") }
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading.value -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                reservas.value.isEmpty() -> {
                    Text(
                        text = "No tienes reservas registradas",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reservas.value) { reserva ->
                            ReservaCard(reserva = reserva)
                        }
                    }
                }
            }
        }
    }
}