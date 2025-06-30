package pe.edu.upeu.granturismojpc.ui.presentation.screens.favorito

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.PaqueteRespBuscar
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.LoadingCard
import pe.edu.upeu.granturismojpc.ui.presentation.components.PaqueteCard
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun FavoritoMain(navegarEditarAct: (String) -> Unit,
                 viewModel: FavoritoMainViewModel = hiltViewModel(),
                 navController: NavHostController) {
    val favoritos by viewModel.provs.collectAsState()
    val paquetes by viewModel.paquetes.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos()
        viewModel.sincronizarUnaVez()
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    FavoritoGestion(navController, onAddClick = {
        //viewModel.addUser()
        navegarEditarAct((0).toString())
    }, onDeleteClick = {
        viewModel.eliminar(it.toDto())
    }, favoritos, paquetes, isLoading,
    onEditClick = {
            val jsonString = Gson().toJson(it)
            navegarEditarAct(jsonString)
    }
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritoGestion(navController: NavHostController,
                    onAddClick: (() -> Unit)? = null,
                    onDeleteClick: ((toDelete: Favorito) -> Unit)? = null,
                    favoritos: List<Favorito>,
                    paquetes: List<PaqueteResp>,
                    isLoading: Boolean,
                    onEditClick: ((toPersona: Favorito) -> Unit)? = null,
                    viewModel: FavoritoMainViewModel = hiltViewModel()
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
    var favoritosFiltrados = remember { mutableStateOf<List<Favorito>>(emptyList()) }

    val favoritosUsuario = favoritos.filter { it.usuario == TokenUtils.USER_ID }
    val paquetesFavoritosIds = favoritosUsuario.map { it.referenciaId }
    val paquetesFavoritos = paquetes.filter { it.idPaquete in paquetesFavoritosIds }
    var paquetesFiltrados = remember { mutableStateOf<List<PaqueteResp>>(emptyList()) }

    /*val fabItems = listOf(
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
            "Add Favorito"
        ) { onAddClick?.invoke() })*/

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
        /*floatingActionButton = {
            MultiFloatingActionButton(
                navController=navController,
                fabIcon = Icons.Filled.Add,
                items = fabItems,
                showLabels = true
            )
        },*/
        floatingActionButtonPosition = FabPosition.End,
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 60.dp)){
            Text(
                text = "Favoritos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 22.dp, start = 22.dp, bottom = 5.dp)
            )
                OutlinedTextField(
                   value = searchQuery.value,
                   onValueChange = { searchQuery.value = it },
                   label = { Text("Buscar favorito") },
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 22.dp, end = 22.dp, start = 22.dp),
                   singleLine = true
               )
            paquetesFiltrados.value = paquetesFavoritos.filter {
                        it.titulo.contains(searchQuery.value, ignoreCase = true) //||
                        //it.tipo.nombre.contains(searchQuery.value, ignoreCase = true) //||
                        //it.marca.nombre.contains(searchQuery.value, ignoreCase = true)
            }

            LazyColumn(modifier = Modifier
                .padding(top = 90.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
                .align(alignment = Alignment.TopCenter),
                //.offset(x = (16).dp, y = (-32).dp),
                userScrollEnabled= true,
            ){
                item{
                    Text(
                        text = "Asociaciones favoritas",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)
                    )
                }
                item{
                    TextButton(
                        onClick = {
                            navController.navigate(Destinations.FavoritoServMainSC.route)

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Mostrar actividades y servicios",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                itemsIndexed(paquetesFiltrados.value) { _, paquete ->
                    val esFavorito = true // Siempre será favorito, ya fue filtrado
                    val paqueteBuscar: Flow<PaqueteRespBuscar> = remember(paquete.idPaquete) {
                        viewModel.buscarPaquetePorId(paquete.idPaquete)
                    }
                    val paqueteB by paqueteBuscar.collectAsState(initial = null)
                    paqueteB?.let {
                    PaqueteCard(
                        paquete = paquete,
                        paqueteBuscar = it,
                        esFavorito = esFavorito,
                        navController = navController,
                        onFavoritoClick = { paqueteSeleccionado, yaEsFavorito ->
                            if (yaEsFavorito) {
                                viewModel.eliminarFavorito(paqueteSeleccionado.idPaquete, "paquete")
                            } else {
                                viewModel.agregarFavorito(paqueteSeleccionado.idPaquete, "paquete")
                            }
                        }
                    )}
                }
                var itemCount = favoritosFiltrados.value.size
                items(count = itemCount) { index ->
                    var auxIndex = index;
                    if (isLoading) {
                        if (auxIndex == 0)
                            return@items LoadingCard()
                        auxIndex--
                    }
                    val favoritox = favoritosFiltrados.value.get(index)
                    /*Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                    ) {
                        Row(modifier = Modifier.padding(8.dp)) {
                            Image(modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .clip(RoundedCornerShape(8.dp)),
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(favoritox.tipo.nombre)
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
                                Text("${favoritox.nombreFavorito} - ${favoritox.precioBase}",
                                    fontWeight = FontWeight.Bold)
                                Text("${favoritox.tipo.nombre} - ${favoritox.estado}", color =MaterialTheme.colorScheme.primary)
                            }
                            Spacer()
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
                                        onDeleteClick?.invoke(favoritox)
                                        showDialog.value=false

                                    },
                                    onDimins = {
                                        showDialog.value=false
                                    }
                                )
                            }
                            IconButton(onClick = {
                                Log.i("VERTOKEN", TokenUtils.TOKEN_CONTENT)
                                onEditClick?.invoke(favoritox)
                            }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                navController.navigate(Destinations.CarritoFormSC.passId("0",favoritox.idFavorito.toString(),"0"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Agregar al carrito",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    */
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