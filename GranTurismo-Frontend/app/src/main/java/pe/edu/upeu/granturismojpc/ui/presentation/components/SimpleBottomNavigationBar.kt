package pe.edu.upeu.granturismojpc.ui.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun SimpleBottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
    ) {

    // 1. Observamos la ruta actual
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Inicio
        BottomNavItem(
            icon = Icons.Filled.Home, // Usa tu propio icono
            label = "Inicio",
            selected = currentRoute == Destinations.HomeScreen.route
        ) {
            navController.navigate("pantallahome")
        }


        // Actividades
        BottomNavItem(
            icon = Icons.Filled.Map,// Usa tu propio icono
            label = "Actividades",
            selected = currentRoute == Destinations.ActividadMainSC.route
        ) {
            navController.navigate(Destinations.ActividadMainSC.route)
        }

        if(TokenUtils.USER_ROLE=="USER"){
        // Reservas
        BottomNavItem(
            icon = Icons.Filled.Favorite, // Usa tu propio icono
            label = "Favorito",
            selected = currentRoute == Destinations.FavoritoMainSC.route

        ) {
            navController.navigate(Destinations.FavoritoMainSC.route)
        }}

        // Reservas
        BottomNavItem(
            icon = Icons.Filled.DateRange, // Usa tu propio icono
            label = "Reservas",
            selected = currentRoute == Destinations.Pantalla4.route

        ) {
            navController.navigate(Destinations.Pantalla4.route)
        }

        // Perfil
        BottomNavItem(
            icon = Icons.Filled.Person, // Usa tu propio icono
            label = "Perfil",
            selected = currentRoute == Destinations.PerfilScreen.route
        ) {
            navController.navigate(Destinations.PerfilScreen.route)
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val contentColor = if (selected)
        Color(0xFFFFDEA8)
    else
        Color.Gray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}