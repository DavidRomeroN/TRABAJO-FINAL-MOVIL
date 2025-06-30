package pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad

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
import kotlinx.coroutines.flow.first
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.CarritoItemResp
import pe.edu.upeu.granturismojpc.model.Favorito
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
fun ActividadMain(navegarEditarAct: (String) -> Unit,
                 viewModel: ActividadMainViewModel = hiltViewModel(),
                 navController: NavHostController) {
    val actividads by viewModel.actvs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoritos by viewModel.favs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarActividads()
        viewModel.cargarFavoritos()
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    ActividadGestion(navController, onAddClick = {
        //viewModel.addUser()
        navegarEditarAct((0).toString())
    }, onDeleteClick = {
        viewModel.eliminar(it.toDto())
    }, actividads, favoritos, isLoading,
    onEditClick = {
            val jsonString = Gson().toJson(it.toDto())
            navegarEditarAct(jsonString)
    }
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ActividadGestion(navController: NavHostController,
                     onAddClick: (() -> Unit)? = null,
                     onDeleteClick: ((toDelete: ActividadResp) -> Unit)? = null,
                     actividads: List<ActividadResp>,
                     favoritos: List<Favorito>,
                     isLoading: Boolean,
                     onEditClick: ((toPersona: ActividadResp) -> Unit)? = null,
                     viewModel: ActividadMainViewModel = hiltViewModel()
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
    var actividadsFiltrados = remember { mutableStateOf<List<ActividadResp>>(emptyList()) }
    val fabItems = listOf(
        FabItem(
            Icons.Filled.ShoppingCart,
            "Shopping Cart"
        ) {
            val toast = Toast.makeText(context, "Hola Mundo", Toast.LENGTH_LONG)
            toast.view!!.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN)
            toast.show()
        },
        FabItem(
            Icons.Filled.Favorite,
            "Add Actividad"
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
                text = "Actividades",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 22.dp, start = 22.dp, bottom = 5.dp)
            )
               OutlinedTextField(
                   value = searchQuery.value,
                   onValueChange = { searchQuery.value = it },
                   label = { Text("Buscar actividad") },
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 22.dp, end = 22.dp, start = 22.dp),
                   singleLine = true
               )
            actividadsFiltrados.value = actividads.filter {
                        it.titulo.contains(searchQuery.value, ignoreCase = true) ||
                        it.tipo.contains(searchQuery.value, ignoreCase = true) //||
                        //it.marca.nombre.contains(searchQuery.value, ignoreCase = true)
            }
            LazyColumn(modifier = Modifier
                .padding(top = 90.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
                .align(alignment = Alignment.TopCenter),
                //.offset(x = (16).dp, y = (-32).dp),
                userScrollEnabled= true,
            ){
                var itemCount = actividadsFiltrados.value.size
                items(count = itemCount) { index ->
                    var auxIndex = index;
                    if (isLoading) {
                        if (auxIndex == 0)
                            return@items LoadingCard()
                        auxIndex--
                    }
                    val actividadx = actividadsFiltrados.value.get(index)
                    val esFavorito = favoritos.any {
                        it.referenciaId == actividadx.idActividad && it.tipo == "actividad"
                    }
                    ActividadCard(navController,context, actividadx, favoritos, viewModel, onDeleteClick, onEditClick)
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

private fun onFavoritoClick(
    viewModel: ActividadMainViewModel,
    actividadSeleccionada: ActividadResp,
    yaEsFavorito: Boolean
) {
    if (yaEsFavorito) {
        viewModel.eliminarFavorito(actividadSeleccionada.idActividad)
    } else {
        viewModel.agregarFavorito(actividadSeleccionada.idActividad)
    }
}
@Composable
fun ActividadCard(
    navController: NavHostController,
    context: Context,
    actividadx: ActividadResp,
    favoritos: List<Favorito>,
    viewModel: ActividadMainViewModel,
    onDeleteClick: ((toDelete: ActividadResp) -> Unit)? = null,
    onEditClick: ((toPersona: ActividadResp) -> Unit)? = null,
){
    var carritoItem by remember { mutableStateOf<CarritoItemResp?>(null) }
    val esFavorito = favoritos.any {
        it.referenciaId == actividadx.idActividad && it.tipo == "actividad"
                && it.usuario == TokenUtils.USER_ID
    }
    LaunchedEffect(actividadx.idActividad) {
        try {
            val result = viewModel.buscarCarritoItemPorTipo(actividadx.idActividad,"actividad")
            carritoItem = result.first()
        } catch (e: Exception) {
            // Si lanza "CarritoItem no encontrado", simplemente mantenemos null
            carritoItem = null
        }
    }
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
                Image(modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clip(RoundedCornerShape(8.dp)),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(actividadx.imagenUrl)
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
                    Text("${actividadx.titulo} - ${actividadx.precioBase}",
                        fontWeight = FontWeight.Bold)
                    Text("${actividadx.tipo} - Duración: ${actividadx.duracionHoras}h", color =MaterialTheme.colorScheme.primary)
                }
                Spacer()
                // Ícono de favorito con acción de clic
                if(TokenUtils.USER_ROLE=="USER"){
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (esFavorito) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clickable {
                            onFavoritoClick(viewModel, actividadx, esFavorito)
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
                            onDeleteClick?.invoke(actividadx)
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
                    onEditClick?.invoke(actividadx)
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }}
            }
            if(TokenUtils.USER_ROLE=="USER"){
            val label = if (carritoItem != null) "Ver carrito" else "Agregar al carrito"
            TextButton(
                onClick = {
                    if (carritoItem != null)
                        navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
                    else
                        navController.navigate(Destinations.CarritoFormSC.passId("0",actividadx.idActividad.toString(),"actividad"))

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }}
        }

    }
}