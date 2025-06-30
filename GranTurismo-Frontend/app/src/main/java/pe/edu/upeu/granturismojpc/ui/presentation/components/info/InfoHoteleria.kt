package pe.edu.upeu.granturismojpc.ui.presentation.components.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.ServicioAlimentacionResp
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraResp
import pe.edu.upeu.granturismojpc.ui.presentation.components.RatingBar

@Composable
fun InfoHoteleria(servicio: ServicioHoteleraResp?) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen de la parte superior
            /*
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(servicio?.servicio)
                        .placeholder(R.drawable.bg)
                        .error(R.drawable.bg)
                        .build()
                ), // Asegúrate de tener esta imagen en drawable
                contentDescription = "Sunset in the mountains",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )*/

            // Texto principal
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = servicio?.servicio?.nombreServicio?:"",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = servicio?.servicio?.descripcion?:"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Row {
                    Text(
                        text = "Estrellas: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    var estrellas: Int
                    RatingBar(servicio?.estrellas?:0,onRatingChanged = {
                        estrellas = it
                    })
                }
                Text(
                    text = "Tipo: ${servicio?.servicio?.tipo?.nombre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Tipo de habitación: ${servicio?.tipoHabitacion}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "N° máximo de personas: ${servicio?.maxPersonas}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = if(servicio?.incluyeDesayuno=="Si") "Incluye desayuno" else "No incluye desayuno",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
