package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.ConfirmDialog
import pe.edu.upeu.granturismojpc.ui.presentation.components.FabItem
import pe.edu.upeu.granturismojpc.ui.presentation.components.LoadingCard
import pe.edu.upeu.granturismojpc.ui.presentation.components.MultiFloatingActionButton
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun ServicioArtesaniaMain(
    navegarEditarAct: (String) -> Unit,
    viewModel: ServicioArtesaniaMainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val servicioArtesaniaes by viewModel.servarts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoritos by viewModel.favs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarServicioArtesaniaes()
        viewModel.cargarFavoritos()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    ServicioArtesaniaGestion(
        navController,
        onAddClick = {
            navegarEditarAct((0).toString())
        },
        onDeleteClick = {
            viewModel.eliminar(it.toDto())
        },
        servicioArtesaniaes,
        favoritos,
        isLoading,
        onEditClick = {
            val jsonString = Gson().toJson(it.toDto())
            navegarEditarAct(jsonString)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServicioArtesaniaGestion(
    navController: NavHostController,
    onAddClick: (() -> Unit)? = null,
    onDeleteClick: ((toDelete: ServicioArtesaniaResp) -> Unit)? = null,
    servicioArtesaniaes: List<ServicioArtesaniaResp>,
    favoritos: List<Favorito>,
    isLoading: Boolean,
    onEditClick: ((toPersona: ServicioArtesaniaResp) -> Unit)? = null,
    viewModel: ServicioArtesaniaMainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    val navigationItems2 = listOf(
        Destinations.Pantalla1,
        Destinations.Pantalla2,
        Destinations.Pantalla3
    )
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val searchQuery = remember { mutableStateOf("") }
    var servicioArtesaniaesFiltrados = remember { mutableStateOf<List<ServicioArtesaniaResp>>(emptyList()) }

    // NUEVOS FAB ITEMS CON FUNCIONALIDAD QR
    val fabItems:List<FabItem>
    if(TokenUtils.USER_ROLE=="ADMIN"){
        fabItems = listOf(
            FabItem(
                Icons.Filled.QrCodeScanner,
                "Escanear QR"
            ) {
                // NUEVA FUNCIONALIDAD: Navegar al scanner QR
                navController.navigate(Destinations.QrScannerScreen.route)
            },
            FabItem(
                Icons.Filled.Favorite,
                "Add ServicioArtesania"
            ) {
                onAddClick?.invoke()
            }
        )
    }else{
        fabItems = listOf(
            FabItem(
                Icons.Filled.QrCodeScanner,
                "Escanear QR"
            ) {
                // NUEVA FUNCIONALIDAD: Navegar al scanner QR
                navController.navigate(Destinations.QrScannerScreen.route)
            }
        )
    }


    Scaffold(
        bottomBar = {
            SimpleBottomNavigationBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navBarPadding)
                    .height(56.dp)
            )
        },
        modifier = Modifier,
        floatingActionButton = {
            if(TokenUtils.USER_ROLE!="USER"){
            MultiFloatingActionButton(
                navController = navController,
                fabIcon = Icons.Filled.Add,
                items = fabItems,
                showLabels = true
            )}
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)) {

            // NUEVO HEADER CON BOTONES QR
            QrHeaderSection(
                navController = navController,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = { Text("Buscar servicioArtesania") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, end = 22.dp, start = 22.dp), // Ajustado padding top
                singleLine = true
            )

            servicioArtesaniaesFiltrados.value = servicioArtesaniaes.filter {
                it.tipoArtesania.contains(searchQuery.value, ignoreCase = true) ||
                        it.servicio.nombreServicio.contains(searchQuery.value, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .padding(top = 140.dp, start = 16.dp, end = 16.dp, bottom = 32.dp) // Ajustado padding top
                    .align(alignment = Alignment.TopCenter),
                userScrollEnabled = true,
            ) {
                var itemCount = servicioArtesaniaesFiltrados.value.size
                items(count = itemCount) { index ->
                    var auxIndex = index
                    if (isLoading) {
                        if (auxIndex == 0)
                            return@items LoadingCard()
                        auxIndex--
                    }
                    val servicioArtesaniax = servicioArtesaniaesFiltrados.value.get(index)

                    // NUEVA VERSIÓN DEL CARD CON FUNCIONALIDAD QR
                    ArtesaniaCardWithQr(
                        navController = navController,
                        context = context,
                        servicioArtesaniax = servicioArtesaniax,
                        favoritos = favoritos,
                        viewModel = viewModel,
                        onDeleteClick,
                        onEditClick
                    )
                }
            }
        }
    }

    deleteSuccess?.let { success ->
        if (success) {
            Toast.makeText(LocalContext.current, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(LocalContext.current, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
        viewModel.clearDeleteResult()
    }
}

// NUEVO COMPONENTE: Header con botones QR
@Composable
fun QrHeaderSection(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Servicios de Artesanía",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Row {
            // Botón para escanear QR
            IconButton(
                onClick = {
                    navController.navigate(Destinations.QrScannerScreen.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escanear QR",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Botón para gestión de QRs (solo para admins)
            if (TokenUtils.USER_ROLE == "ADMIN" || TokenUtils.USER_ROLE == "PROV") {
                IconButton(
                    onClick = {
                        navController.navigate(Destinations.QrManagementScreen.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ManageSearch,
                        contentDescription = "Gestión QR",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// COMPONENTE ACTUALIZADO: Card con funcionalidad QR
@Composable
fun ArtesaniaCardWithQr(
    navController: NavHostController,
    context: Context,
    servicioArtesaniax: ServicioArtesaniaResp,
    favoritos: List<Favorito>,
    viewModel: ServicioArtesaniaMainViewModel,
    onDeleteClick: ((toDelete: ServicioArtesaniaResp) -> Unit)? = null,
    onEditClick: ((toPersona: ServicioArtesaniaResp) -> Unit)? = null
    ) {
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }
    val esFavorito = favoritos.any {
        it.referenciaId == servicioArtesaniax.idArtesania && it.tipo == "servicioArtesania"
                && it.usuario == TokenUtils.USER_ID
    }

    LaunchedEffect(servicioArtesaniax.idArtesania) {
        try {
            val result = viewModel.buscarCarritoItemPorTipo(servicioArtesaniax.idArtesania, "servicioArtesania")
            carritoItem = result.first()
        } catch (e: Exception) {
            carritoItem = null
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
    ) {
        Column {
            Row(modifier = Modifier.padding(8.dp)) {
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clip(RoundedCornerShape(8.dp)),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(servicioArtesaniax.servicio.nombreServicio)
                            .placeholder(R.drawable.bg)
                            .error(R.drawable.bg)
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${servicioArtesaniax.tipoArtesania} - ${servicioArtesaniax.duracionTaller}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${servicioArtesaniax.servicio.nombreServicio} - ${servicioArtesaniax.servicio.precioBase}",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "por ${servicioArtesaniax.artesano}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // NUEVO: Indicador de verificación blockchain
                        if (servicioArtesaniax.hashBlockchain != null && servicioArtesaniax.hashBlockchain.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = androidx.compose.ui.graphics.Color.Green.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verificado en blockchain",
                                        tint = androidx.compose.ui.graphics.Color.Green,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Verificado",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = androidx.compose.ui.graphics.Color.Green
                                    )
                                }
                            }
                        }
                        if(TokenUtils.USER_ROLE=="USER"){
                        // Ícono de favorito
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (esFavorito) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp)
                                .clickable {
                                    onFavoritoClick(viewModel, servicioArtesaniax, esFavorito)
                                }
                        )}
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
                                    onDeleteClick?.invoke(servicioArtesaniax)
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
                            onEditClick?.invoke(servicioArtesaniax)
                        }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }}
                    }
                }
            }

            // NUEVA SECCIÓN: Botones de acción con QR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón de carrito (solo para usuarios)
                if (TokenUtils.USER_ROLE == "USER") {
                    val label = if (carritoItem != null) "Ver carrito" else "Agregar al carrito"
                    TextButton(
                        onClick = {
                            if (carritoItem != null)
                                navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
                            else
                                navController.navigate(Destinations.CarritoFormSC.passId("0", servicioArtesaniax.idArtesania.toString(), "servicioArtesania"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // NUEVO: Botón QR (para admins y proveedores)
                if (TokenUtils.USER_ROLE == "ADMIN" || TokenUtils.USER_ROLE == "PROV") {
                    Button(
                        onClick = {
                            navController.navigate(
                                Destinations.QrGenerationScreen.createRoute(servicioArtesaniax.idArtesania)
                            )
                        },
                        modifier = Modifier.weight(if (TokenUtils.USER_ROLE == "USER") 1f else 2f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (servicioArtesaniax.hashBlockchain != null && servicioArtesaniax.hashBlockchain.isNotEmpty()) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Icon(
                            imageVector = if (servicioArtesaniax.hashBlockchain != null && servicioArtesaniax.hashBlockchain.isNotEmpty()) {
                                Icons.Default.QrCode
                            } else {
                                Icons.Default.Security
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (servicioArtesaniax.hashBlockchain != null && servicioArtesaniax.hashBlockchain.isNotEmpty()) {
                                "Ver QR"
                            } else {
                                "Generar QR"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

// Función helper original mantenida
private fun onFavoritoClick(
    viewModel: ServicioArtesaniaMainViewModel,
    servicioSeleccionado: ServicioArtesaniaResp,
    yaEsFavorito: Boolean
) {
    if (yaEsFavorito) {
        viewModel.eliminarFavorito(servicioSeleccionado.idArtesania)
    } else {
        viewModel.agregarFavorito(servicioSeleccionado.idArtesania)
    }
}

// COMPONENTE ORIGINAL MANTENIDO PARA COMPATIBILIDAD
@Composable
fun ArtesaniaCard(
    navController: NavHostController,
    context: Context,
    servicioArtesaniax: ServicioArtesaniaResp,
    favoritos: List<Favorito>,
    viewModel: ServicioArtesaniaMainViewModel
) {
    // Redirecting to the new component
    ArtesaniaCardWithQr(navController, context, servicioArtesaniax, favoritos, viewModel)
}