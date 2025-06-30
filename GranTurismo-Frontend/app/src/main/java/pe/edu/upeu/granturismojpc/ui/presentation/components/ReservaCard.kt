package pe.edu.upeu.granturismojpc.ui.presentation.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.granturismojpc.model.ReservaResp

@Composable
fun ReservaCard(reserva: ReservaResp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Reserva ${reserva.paquete?.titulo}  Para ${reserva.cantidadPersonas} personas", style = MaterialTheme.typography.titleMedium)
            Text(text = "Estado de confirmacion: ${reserva.estado}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
