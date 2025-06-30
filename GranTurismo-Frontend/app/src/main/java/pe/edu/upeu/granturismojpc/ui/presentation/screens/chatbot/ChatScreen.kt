package pe.edu.upeu.granturismojpc.ui.presentation.screens.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.granturismojpc.ui.presentation.components.ChatBubble
// No necesita cambios aquí, ya que el ViewModel se encarga de la fuente de los mensajes
// import pe.edu.upeu.granturismojpc.ui.presentation.screens.chatbot.ChatViewModel // Ya está importado por @Composable
import pe.edu.upeu.granturismojpc.utils.ChatStateHolder // Importa para posible uso, aunque el ViewModel lo maneja
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val sesiones by viewModel.sesiones.collectAsState()
    val mensajes by viewModel.mensajes.collectAsState()
    val sesionSeleccionada by viewModel.sesionIdSeleccionada.collectAsState()
    var texto by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val haySesionActiva = sesionSeleccionada != null && sesiones.any { it.sesionId == sesionSeleccionada }






    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.lastIndex)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Text(
                    "Sesiones",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = {
                        viewModel.crearNuevaSesion()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Nuevo chat")
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(sesiones) { sesion ->
                        val isSelected = sesion.sesionId == sesionSeleccionada
                        val fechaTexto = sesion.fecha?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                            ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else
                                Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.seleccionarSesion(sesion.sesionId)
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botón eliminar a la izquierda
                                IconButton(
                                    onClick = {
                                        viewModel.eliminarSesion(sesion.sesionId)
                                        //viewModel.clearChatHistory()
                                    },
                                    modifier = Modifier.size(24.dp) // más compacto
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar sesión",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Texto a la derecha del botón
                                Column {
                                    Text(
                                        text = sesion.preview,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = fechaTexto,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    ) {
        // ░░░ ÁREA DE CHAT ░░░
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ChatBot") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState
                ) {
                    items(mensajes) { msg ->
                        ChatBubble(msg)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = texto,
                        onValueChange = { texto = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = { Text("Escribe un mensaje...") },
                        shape = RoundedCornerShape(12.dp),
                        enabled = haySesionActiva
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.enviarMensaje(texto)
                            texto = ""
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = texto.isNotBlank() && haySesionActiva
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}
