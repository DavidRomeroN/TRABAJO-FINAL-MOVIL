package pe.edu.upeu.granturismojpc.ui.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteRespBuscar
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.utils.TokenUtils
//import pe.edu.upeu.granturismojpc.data.remote.response.ProveedorResp
import java.math.BigDecimal

data class PaqueteResp(
    val idPaquete: Long,
    val titulo: String,
    val descripcion: String,
    val precioTotal: BigDecimal,
    val imagenUrl: String,
    var estado: String,
    var duracionDias: Int,
    val localidad: String,
    val tipoActividad: String,
    val cuposMaximos: Int,
    val proveedor: ProveedorResp,
    val fechaInicio: String,
    val fechaFin: String,
    var destino: DestinoResp,
)

@Composable
fun PaqueteCard(
    paquete: PaqueteResp,
    paqueteBuscar: PaqueteRespBuscar,
    esFavorito: Boolean,
    onFavoritoClick: (PaqueteResp, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    AsyncImage(
                        model = paquete.imagenUrl,
                        contentDescription = "Imagen del paquete",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = paquete.titulo,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "${strings.place}: ${paquete.localidad}")
                        Text(text = "${strings.activity}: ${paquete.tipoActividad}")
                    }
                }

                if(TokenUtils.USER_ROLE=="USER"){
                // Ícono de favorito con acción de clic
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) Color.Red else Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clickable {
                            onFavoritoClick(paquete, esFavorito)
                        }
                )}
            }

            Spacer(modifier = Modifier.height(12.dp))


            // Botones estilo fila inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de Detalles
                Button (
                    onClick = {
                        navController.navigate("detallePaquete/${paquete.idPaquete}")
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726), // Naranja (opcional)
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Detalles",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = strings.details)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón de WhatsApp
                val context = LocalContext.current
                val proveedor = paqueteBuscar?.proveedor
                val numeroWhatsapp = proveedor?.telefono?.replace("+", "")?.replace(" ", "")
                Button(
                    onClick = {
                        numeroWhatsapp?.let {
                            val url = "https://wa.me/$it"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366) // Color verde de WhatsApp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone, // Puedes usar otro ícono si deseas
                        contentDescription = "WhatsApp",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
