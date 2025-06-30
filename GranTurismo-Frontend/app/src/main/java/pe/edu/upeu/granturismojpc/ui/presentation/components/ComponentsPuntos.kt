package pe.edu.upeu.granturismojpc.ui.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.repository.PuntosRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import kotlin.fold
import kotlin.text.isNotEmpty

@Composable
fun ComponentePuntos(navController: NavController) {
    var puntosDisponibles by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val repository = remember { PuntosRepository() }
    val scope = rememberCoroutineScope()

    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }

    // Cargar puntos al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val token = TokenUtils.TOKEN
            if (token.isNotEmpty()) {
                repository.getPuntosDisponibles(token).fold(
                    onSuccess = { response ->
                        puntosDisponibles = response.puntosDisponibles
                    },
                    onFailure = { error ->
                        // Manejar error silenciosamente o mostrar 0 puntos
                        puntosDisponibles = 0
                    }
                )
            }
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                navController.navigate("puntos")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Puntos",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.pointMyPoints,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isLoading) {
                    Text(
                        text = "Cargando...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "${puntosDisponibles ?: 0} ${strings.pointPointsAvailable}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Arrow",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}