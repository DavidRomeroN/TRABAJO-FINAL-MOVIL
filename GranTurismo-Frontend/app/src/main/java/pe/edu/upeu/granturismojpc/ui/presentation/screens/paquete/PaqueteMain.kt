package pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.ConfirmDialog
import pe.edu.upeu.granturismojpc.ui.presentation.components.FabItem
import pe.edu.upeu.granturismojpc.ui.presentation.components.LoadingCard
import pe.edu.upeu.granturismojpc.ui.presentation.components.MultiFloatingActionButton
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun PaqueteMain(
    navegarEditarAct: (String) -> Unit,
    navegarDetalle: (String) -> Unit,
    navegarResena: (String) -> Unit,
    navegarActDetalle: (String) -> Unit,
                viewModel: PaqueteMainViewModel = hiltViewModel(),
                navController: NavHostController) {
    val paquetes by viewModel.paquetes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sincronizarUnaVez()
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    PaqueteGestion(navController,
        onDetClick = { paqueteId ->
            navegarDetalle(paqueteId)
        },
        onActDetClick = { paqueteId ->
            navegarActDetalle(paqueteId)
        },
        onResClick = { paqueteId ->
            navegarResena(paqueteId)
        },
        onAddClick = {
        //viewModel.addUser()
        navegarEditarAct((0).toString())
    }, onDeleteClick = {
        viewModel.eliminar(it.toDto())
    }, paquetes, isLoading,
        onEditClick = {
            val jsonString = Gson().toJson(it.toDto())
            navegarEditarAct(jsonString)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaqueteGestion( navController: NavHostController,
                    onDetClick: ((packId: String) -> Unit)? = null,
                    onActDetClick: ((packId: String) -> Unit)? = null,
                    onResClick: ((packId: String) -> Unit)? = null,
                    onAddClick: (() -> Unit)? = null,
                    onDeleteClick: ((toDelete: PaqueteResp) -> Unit)? = null,
                    paquetes: List<PaqueteResp>,
                    isLoading: Boolean,
                    onEditClick: ((toPersona: PaqueteResp) -> Unit)? = null,
                    viewModel: PaqueteMainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    val detalles by viewModel.detalles.collectAsState()
    val deleteDetalleSuccess by viewModel.deleteDetalleSuccess.collectAsState()
    val currentPaqueteId by viewModel.currentPaqueteId.collectAsState()

    val navigationItems2 = listOf(
        Destinations.PaqueteMainSC,
        Destinations.Pantalla1,
        Destinations.Pantalla2,
        Destinations.Pantalla3
    )
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val searchQuery = remember { mutableStateOf("") }
    var paquetesFiltrados = remember { mutableStateOf<List<PaqueteResp>>(emptyList()) }

    // Estado para controlar qué Card está expandido
    val expandedCardId = remember { mutableStateOf<Long?>(null) }

    val fabItems = listOf(
        FabItem(
            Icons.Filled.Favorite,
            "Add Paquete"
        ) { onAddClick?.invoke() })

    Scaffold(
        bottomBar = {
            SimpleBottomNavigationBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navBarPadding)
                    .height(56.dp))
        },
        modifier = Modifier,
        floatingActionButton = {
            if(TokenUtils.USER_ROLE=="ADMIN"){
            MultiFloatingActionButton(
                navController=navController,
                fabIcon = Icons.Filled.Add,
                items = fabItems,
                showLabels = true
            )}
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 60.dp)){
            Text(
                text = "Asociaciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 22.dp, start = 22.dp, bottom = 5.dp)
            )
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = { Text("Buscar asociación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp, end = 22.dp, start = 22.dp),
                singleLine = true
            )
            paquetesFiltrados.value = paquetes.filter {
                it.titulo.contains(searchQuery.value, ignoreCase = true) ||
                        it.proveedorNombre.contains(searchQuery.value, ignoreCase = true) == true //||
                //it.marca.nombre.contains(searchQuery.value, ignoreCase = true)
            }
            LazyColumn(modifier = Modifier
                .padding(top = 90.dp, start = 16.dp, end = 16.dp)
                .align(alignment = Alignment.TopCenter),
                //.offset(x = (16).dp, y = (-32).dp),
                userScrollEnabled= true,
            ){
                var itemCount = paquetesFiltrados.value.size
                items(count = itemCount) { index ->
                    var auxIndex = index;
                    if (isLoading) {
                        if (auxIndex == 0)
                            return@items LoadingCard()
                        auxIndex--
                    }
                    val paquetex = paquetesFiltrados.value.get(index)
                    val isExpanded = expandedCardId.value == paquetex.idPaquete

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                    ) {
                        Column {
                            // Contenido principal del Card
                            Row(modifier = Modifier.padding(8.dp)) {
                                Image(modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .clip(RoundedCornerShape(8.dp)),
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(context)
                                            .data(paquetex.imagenUrl)
                                            .placeholder(R.drawable.bg)
                                            .error(R.drawable.bg)
                                            .build()
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillHeight
                                )
                                Spacer()
                                Column(
                                    Modifier.weight(1f),
                                ) {
                                    Text("${paquetex.titulo} - ${paquetex.precioTotal}",
                                        fontWeight = FontWeight.Bold)
                                    Text("${paquetex.proveedorNombre} - ${paquetex.descripcion}", color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer()
                                if(TokenUtils.USER_ROLE=="ADMIN"){
                                val showDialog = remember { mutableStateOf(false) }
                                IconButton(onClick = {
                                    showDialog.value = true
                                }) {
                                    Icon(Icons.Filled.Delete, "Remove", tint =
                                        MaterialTheme.colorScheme.primary)
                                }
                                if (showDialog.value){
                                    ConfirmDialog(
                                        message = "Esta seguro de eliminar?",
                                        onConfirm = {
                                            onDeleteClick?.invoke(paquetex)
                                            showDialog.value=false

                                        },
                                        onDimins = {
                                            showDialog.value=false
                                        }
                                    )
                                }}
                                if(TokenUtils.USER_ROLE!="USER"){
                                IconButton(onClick = {
                                    Log.i("VERTOKEN", TokenUtils.TOKEN_CONTENT)
                                    onEditClick?.invoke(paquetex)
                                }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }}
                            }
                            TextButton(
                                onClick = {
                                    onDetClick?.invoke(paquetex.idPaquete.toString())
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Mostrar Detalles",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            TextButton(
                                onClick = {
                                    onActDetClick?.invoke(paquetex.idPaquete.toString())
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Mostrar Actividades",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            TextButton(
                                onClick = {
                                    onResClick?.invoke(paquetex.idPaquete.toString())
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Mostrar Reseñas",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            // Botón "Ver Detalles"
                            TextButton(
                                onClick = {
                                    if (isExpanded) {
                                        // Si ya está expandido, lo cerramos
                                        expandedCardId.value = null
                                        viewModel.clearDetalles()
                                    } else {
                                        // Si no está expandido, cargamos los detalles y expandimos
                                        expandedCardId.value = paquetex.idPaquete
                                        viewModel.cargarDetallesPorPaqueteId(paquetex.idPaquete)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (isExpanded) "Ocultar detalles" else "Ver detalles",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Contenido expandido (detalles)
                            if (isExpanded) {
                                if (currentPaqueteId == paquetex.idPaquete) {
                                    if (viewModel.isLoading.collectAsState().value) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    } else if (detalles.isEmpty()) {
                                        Text(
                                            "No hay detalles disponibles para este paquete",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                        ) {
                                            Text(
                                                "Servicios incluidos:",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )

                                            detalles.forEach { detalle ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Nombre del servicio
                                                    Text(
                                                        text = detalle.servicio.nombreServicio,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    // Cantidad
                                                    Text(
                                                        text = "Cant: ${detalle.cantidad}",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Spacer(16)
                                                    // Precio
                                                    Text(
                                                        text = "$${detalle.precioEspecial}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    /*
                                                    // Botón de eliminar
                                                    IconButton(
                                                        onClick = {
                                                            val detalleDto = PaqueteDetalleDto(
                                                                idPaqueteDetalle = detalle.idPaqueteDetalle,
                                                                cantidad = detalle.cantidad,
                                                                precioEspecial = detalle.precioEspecial,
                                                                // Añade los demás campos necesarios según tu estructura
                                                                servicio = detalle.servicio.idServicio,
                                                                paquete = detalle.paquete.idPaquete
                                                            )
                                                            viewModel.eliminarDetalle(detalleDto)
                                                        },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Clear,
                                                            contentDescription = "Eliminar servicio",
                                                            tint = MaterialTheme.colorScheme.error,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }*/
                                                }
                                                Divider(
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Mostrar mensaje después de eliminar un paquete
    deleteSuccess?.let { success ->
        if (success) {
            Toast.makeText(LocalContext.current, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(LocalContext.current, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
        viewModel.clearDeleteResult()
    }

    // Mostrar mensaje después de eliminar un detalle
    deleteDetalleSuccess?.let { success ->
        if (success) {
            Toast.makeText(LocalContext.current, "Detalle eliminado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(LocalContext.current, "Error al eliminar detalle", Toast.LENGTH_SHORT).show()
        }
        viewModel.clearDeleteDetalleResult()
    }
}