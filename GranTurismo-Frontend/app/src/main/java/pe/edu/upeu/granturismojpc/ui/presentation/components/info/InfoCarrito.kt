package pe.edu.upeu.granturismojpc.ui.presentation.components.info

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito.CarritoMainViewModel

@Composable
fun InfoCarrito(
    viewModel: CarritoMainViewModel = hiltViewModel(),
    tipo: String,
    referenciaId: Long
){
    when(tipo){
        "actividad"->{
            referenciaId?.let { id ->
                val flow = remember(id) {
                    viewModel.buscarActividadPorId(id)
                }
                val actividad by flow.collectAsState(initial = null)
                InfoActividad(actividad)
            }

        }
        "servicioAlimentacion"->{
            referenciaId?.let { id ->
                val flow = remember(id) {
                    viewModel.buscarAlimentacionPorId(id)
                }
                val servicio by flow.collectAsState(initial = null)
                InfoAlimentacion(servicio)
            }

        }
        "servicioArtesania"->{
            referenciaId?.let { id ->
                val flow = remember(id) {
                    viewModel.buscarArtesaniaPorId(id)
                }
                val servicio by flow.collectAsState(initial = null)
                InfoArtesania(servicio)
            }

        }
        "servicioHoteleria"->{
            referenciaId?.let { id ->
                val flow = remember(id) {
                    viewModel.buscarHoteleraPorId(id)
                }
                val servicio by flow.collectAsState(initial = null)
                InfoHoteleria(servicio)
            }

        }
        else -> {
            Text("Servicio no válido")
        }
    }
}