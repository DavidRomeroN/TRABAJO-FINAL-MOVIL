package pe.edu.upeu.granturismojpc.ui.presentation.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.first
import pe.edu.upeu.granturismojpc.model.ActividadClimaResp
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.screens.home.HomeViewModel
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun ActividadDtoCard(
    actividad: ActividadClimaResp,
    esFavorito: Boolean,
    onFavoritoClick: (ActividadClimaResp, Boolean) -> Unit,
    onAñadirClick: (ActividadClimaResp) -> Unit, // ← Se cambió el nombre aquí
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }
    LaunchedEffect(actividad.idActividad) {
        try {
            val result = viewModel.buscarCarritoItemPorTipo(actividad.idActividad,"actividad")
            carritoItem = result.first()
        } catch (e: Exception) {
            // Si lanza "CarritoItem no encontrado", simplemente mantenemos null
            carritoItem = null
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.height(220.dp)) {
            // Imagen de fondo
            AsyncImage(
                model = actividad.imagenUrl,
                contentDescription = actividad.titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay oscuro para el texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
            )

            // Contenido sobre la imagen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Título y descripción
                Column {
                    Text(
                        text = actividad.titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    actividad.descripcion?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                            maxLines = 2
                        )
                    }
                }

                // Fila inferior con precio, favorito y botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "S/ ${actividad.precioBase}",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${actividad.duracionHoras} horas",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if(TokenUtils.USER_ROLE=="USER") {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (esFavorito) Color.Red else Color.White,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { onFavoritoClick(actividad, esFavorito) }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if(TokenUtils.USER_ROLE=="USER"){
                        val label = if (carritoItem != null) "Ver carrito" else "Agregar al carrito"
                        Button(
                            onClick = {
                                if (carritoItem != null)
                                    navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
                                else
                                    onAñadirClick(actividad)
                                      }, // ← Aquí se pasa el objeto
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFA726),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = label)
                        }}
                    }
                }
            }
        }
    }
}