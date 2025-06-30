package pe.edu.upeu.granturismojpc.ui.presentation.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReservaCarritoCard(
    time: String,
    title: String,
    description: String,
    price: String,
    unitPrice: Double,
    personAmount: Int,
    icon: @Composable (() -> Unit)? = {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFFFE0B2), shape = CircleShape), // Color naranja claro de fondo
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Coffee,
                contentDescription = null,
                tint = Color(0xFFE65100), // Color naranja oscuro del icono
                modifier = Modifier.size(20.dp)
            )
        }
    }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono (opcional, si no se proporciona se usa el predeterminado de café)
            icon?.invoke()
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(time, color = Color.Gray, fontSize = 12.sp)
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(description, color = Color.Gray, fontSize = 14.sp)
            }
            Column{
                val totalPrice=unitPrice*personAmount
                //Text(price, fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 16.sp)
                Text("Cantidad: ${personAmount}")
                Text(price)
                Text("Total: S/.${totalPrice.toString()}", fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 16.sp)
            }
        }
    }
}

// Ejemplo de cómo usar el componente
@Composable
fun EjemploReservaCarrito() {
    ReservaCarritoCard(
        time = "08:00 - 10:00 AM",
        title = "Nombre Servicio",
        description = "Descripcion",
        price = "S/.250",
        unitPrice = 250.0,
        personAmount = 1
    )
}

// Ejemplo con un icono personalizado (puedes pasar cualquier Composable como icono)
@Composable
fun EjemploReservaCarritoConIconoPersonalizado() {
    ReservaCarritoCard(
        time = "10:30 - 12:00 PM",
        title = "Taller de tejido",
        description = "Aprenda técnicas tradicionales de tejido",
        price = "S/.250",
        unitPrice = 250.0,
        personAmount = 2,
        icon = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFFF9C4), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🧶", fontSize = 20.sp) // Usando un emoji como icono
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun PreviewReservaCarritoCard() {
    Column(modifier = Modifier.padding(16.dp)) {
        EjemploReservaCarrito()
        EjemploReservaCarritoConIconoPersonalizado()
    }
}
