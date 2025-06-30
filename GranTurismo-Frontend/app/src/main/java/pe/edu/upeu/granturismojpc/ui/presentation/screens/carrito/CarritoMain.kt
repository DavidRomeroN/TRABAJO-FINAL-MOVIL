package pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
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
fun CarritoMain(
    text:String,
    navegarEditarAct: (String, String?, String?) -> Unit,
                  viewModel: CarritoMainViewModel = hiltViewModel(),
                  navController: NavHostController) {
    val carritoItemes by viewModel.provs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var carritoId by remember { mutableStateOf(0L) }
    val usuarioId= TokenUtils.USER_ID
    LaunchedEffect(Unit) {
        viewModel.cargarCarritoItems()
    }
    LaunchedEffect(Unit) {
        viewModel.carritos.collect { listaCarrito ->
            val carritosDelUsuario = listaCarrito.filter { it.usuario?.idUsuario == usuarioId }
            // Usa carritosDelUsuario como necesites
            Log.i("Carritos", "Carritos encontrados con id ${usuarioId} = ${carritosDelUsuario.size}")
            if (carritosDelUsuario.size==1){
                val carrito=carritosDelUsuario.get(0)
                Log.i("Carrito", "${carrito}")
                Log.i("Carrito", "${carrito.idCarrito}")

                carritoId=carrito.idCarrito}
        }
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    CarritoItemGestion(
        text,
        navController, onAddClick = {
        //viewModel.addUser()
        navController.navigate("ReservaCarrito/$carritoId")
        }, onDeleteClick = {
            viewModel.eliminar(it.toDto())
        }, carritoItemes, isLoading,
        onEditClick = {
                val jsonString = Gson().toJson(it.toDto())
                navegarEditarAct(jsonString,it.referenciaId.toString(),it.tipo)
                    /*
                    if (it.servicio!=null){
                        it.servicio?.idServicio.toString()
                    }else{
                        "0"
                    }
                    ,
                    if (it.actividad!=null){
                        it.actividad?.idActividad.toString()
                    }else{
                        "0"
                    }
                    ,
                )
            if (it.servicio==null){

            }*/
        }
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CarritoItemGestion(
    user:String,
    navController: NavHostController,
           onAddClick: (() -> Unit)? = null,
           onDeleteClick: ((toDelete: CarritoItemResp) -> Unit)? = null,
           carritoItemes: List<CarritoItemResp>,
           isLoading: Boolean,
           onEditClick: ((toPersona: CarritoItemResp) -> Unit)? = null,
           viewModel: CarritoMainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    val navigationItems2 = listOf(
        //Destinations.CarritoItemMainSC,
        Destinations.Pantalla1,
        Destinations.Pantalla2,
        Destinations.Pantalla3
    )
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val searchQuery = remember { mutableStateOf("") }
    var carritoItemesFiltrados = remember { mutableStateOf<List<CarritoItemResp>>(emptyList()) }
    val fabItems = listOf(

        FabItem(
            Icons.Filled.ShoppingCart,
            "Realizar reserva"
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
            MultiFloatingActionButton(
                navController=navController,
                fabIcon = Icons.Filled.Add,
                items = fabItems,
                showLabels = true
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 60.dp)){
            Text(
                text = "Carrito",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 22.dp, start = 22.dp, bottom = 5.dp)
            )
               /*OutlinedTextField(
                   value = searchQuery.value,
                   onValueChange = { searchQuery.value = it },
                   label = { Text("Buscar carritoItem") },
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 16.dp, end = 22.dp, start = 22.dp),
                   singleLine = true
               )*/
            carritoItemesFiltrados.value = carritoItemes.filter {
                        //it.nombreCompleto.contains(searchQuery.value, ignoreCase = true) ||
                        it.carrito.usuario?.idUsuario.toString().contains(searchQuery.value, ignoreCase = true) //||
                        //it.marca.nombre.contains(searchQuery.value, ignoreCase = true)
            }
            LazyColumn(modifier = Modifier
                .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
                .align(alignment = Alignment.TopCenter),
                //.offset(x = (16).dp, y = (-32).dp),
                userScrollEnabled= true,
            ){
                var itemCount = carritoItemesFiltrados.value.size
                items(count = itemCount) { index ->
                    var auxIndex = index;
                    if (isLoading) {
                        if (auxIndex == 0)
                            return@items LoadingCard()
                        auxIndex--
                    }
                    val carritoItemx = carritoItemesFiltrados.value.get(index)
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        ),
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
                                            .data(carritoItemx.carrito.usuario?.email)
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
                                    label(
                                        carritoItemx.tipo,
                                        carritoItemx.referenciaId,
                                        carritoItemx,
                                        viewModel
                                    )
                                }
                                Spacer()
                                val showDialog = remember { mutableStateOf(false) }
                                IconButton(onClick = {
                                    showDialog.value = true
                                }) {
                                    Icon(
                                        Icons.Filled.Delete, "Remove", tint =
                                            MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (showDialog.value) {
                                    ConfirmDialog(
                                        message = "Esta seguro de eliminar?",
                                        onConfirm = {
                                            onDeleteClick?.invoke(carritoItemx)
                                            showDialog.value = false

                                        },
                                        onDimins = {
                                            showDialog.value = false
                                        }
                                    )
                                }
                                IconButton(onClick = {
                                    Log.i("VERTOKEN", TokenUtils.TOKEN_CONTENT)
                                    onEditClick?.invoke(carritoItemx)
                                }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
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

@Composable
fun label(tipo: String?, referenciaId:Long?, resp: CarritoItemResp, viewModel: CarritoMainViewModel) {

    when(tipo){
        "servicioAlimentacion" -> {
            referenciaId?.let { id ->
                val servicioFlow = remember(id) {
                    viewModel.buscarAlimentacionPorId(id)
                }

                val servicio by servicioFlow.collectAsState(initial = null)

                servicio?.let {
                    Text(
                        "${it.tipoComida} - ${it.estiloGastronomico}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${it.servicio?.nombreServicio} - ${it.servicio?.precioBase}",
                        color = MaterialTheme.colorScheme.primary
                    )
                } ?: Text("Cargando...")
            }
        }
        "servicioArtesania" -> {
            referenciaId?.let { id ->
                val servicioFlow = remember(id) {
                    viewModel.buscarArtesaniaPorId(id)
                }

                val servicio by servicioFlow.collectAsState(initial = null)

                servicio?.let {
                    Text(
                        "${it.tipoArtesania} - ${it.duracionTaller}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${it.servicio?.nombreServicio} - ${it.servicio?.precioBase}",
                        color = MaterialTheme.colorScheme.primary
                    )
                } ?: Text("Cargando...")
            }
        }
        "servicioHoteleria" -> {
            referenciaId?.let { id ->
                val servicioFlow = remember(id) {
                    viewModel.buscarHoteleraPorId(id)
                }

                val servicio by servicioFlow.collectAsState(initial = null)

                servicio?.let {
                    Text(
                        "${it.tipoHabitacion} - ${it.estrellas} estrellas",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${it.servicio.nombreServicio} - ${it.servicio.precioBase}",
                        color = MaterialTheme.colorScheme.primary
                    )
                } ?: Text("Cargando...")
            }
        }
        "actividad" -> {
            referenciaId?.let { id ->
                val flow = remember(id) {
                    viewModel.buscarActividadPorId(id)
                }

                val actividad by flow.collectAsState(initial = null)

                actividad?.let {
                    Text(
                        "${actividad?.titulo} - ${actividad?.precioBase}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${actividad?.tipo} - ${actividad?.duracionHoras}",
                        color = MaterialTheme.colorScheme.primary
                    )
                } ?: Text("Cargando...")
            }
        }
    }
    /*
    if (tipo == null) {
        Text(
            "${resp.servicio?.nombreServicio} - ${resp.servicio?.precioBase}",
            fontWeight = FontWeight.Bold
        )
        Text(
            "${resp.servicio?.tipo?.nombre} - ${resp.servicio?.estado}",
            color = MaterialTheme.colorScheme.primary
        )

    }else{
        Text(
            "${resp.actividad?.titulo} - ${resp.actividad?.precioBase}",
            fontWeight = FontWeight.Bold
        )
        Text(
            "${resp.actividad?.tipo} - ${resp.actividad?.duracionHoras}",
            color = MaterialTheme.colorScheme.primary
        )
    }*/
}