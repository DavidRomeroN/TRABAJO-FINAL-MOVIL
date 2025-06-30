package pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.CanjePuntosRequestDTO
import pe.edu.upeu.granturismojpc.model.PuntoDTO
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.repository.PuntosRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuntosScreen(navController: NavController, servicioRepository: ServicioRepository) {
    var puntosDisponibles by remember { mutableStateOf<Int?>(null) }
    var historialPuntos by remember { mutableStateOf<List<PuntoDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var canjeExitoso by remember { mutableStateOf(false) }
    var mostrarDialogoCanje by remember { mutableStateOf(false) }
    var mostrarSnackbar by remember { mutableStateOf(false) }

    val repository = remember { PuntosRepository() }
    val scope = rememberCoroutineScope()

    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }

    // Función para cargar puntos disponibles
    val cargarPuntosDisponibles = {
        scope.launch {
            isLoading = true
            val token = TokenUtils.TOKEN
            if (token.isNotEmpty()) {
                repository.getPuntosDisponibles(token).fold(
                    onSuccess = { response ->
                        puntosDisponibles = response.puntosDisponibles
                        errorMessage = null
                    },
                    onFailure = { error ->
                        errorMessage = error.message
                    }
                )
            }
            isLoading = false
        }
    }

    // Función para cargar historial
    val cargarHistorialPuntos = {
        scope.launch {
            isLoading = true
            val token = TokenUtils.TOKEN
            if (token.isNotEmpty()) {
                repository.getHistorialPuntos(token).fold(
                    onSuccess = { historial ->
                        historialPuntos = historial
                        errorMessage = null
                    },
                    onFailure = { error ->
                        errorMessage = error.message
                    }
                )
            }
            isLoading = false
        }
    }

    // ⭐ FUNCIÓN ACTUALIZADA: Canjear puntos con idServicio
    val canjearPuntos = { cantidad: Int, descripcion: String, idServicio: Long? ->
        scope.launch {
            isLoading = true
            val token = TokenUtils.TOKEN
            if (token.isNotEmpty()) {
                val request = CanjePuntosRequestDTO(cantidad, descripcion, idServicio)
                repository.canjearPuntos(token, request).fold(
                    onSuccess = { punto ->
                        canjeExitoso = true
                        mostrarSnackbar = true
                        cargarPuntosDisponibles()
                        cargarHistorialPuntos()
                        errorMessage = null
                    },
                    onFailure = { error ->
                        errorMessage = error.message
                        canjeExitoso = false
                        mostrarSnackbar = true
                    }
                )
            }
            isLoading = false
        }
    }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        cargarPuntosDisponibles()
        cargarHistorialPuntos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.pointMyPoints) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Card de puntos disponibles
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Puntos",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = strings.pointPointsAvailableLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoading && puntosDisponibles == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "${puntosDisponibles ?: 0}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { mostrarDialogoCanje = true },
                        enabled = (puntosDisponibles ?: 0) > 0 && !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.pointRedeemPoints)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Historial
            Text(
                text = strings.pointTransactionHistory,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading && historialPuntos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (historialPuntos.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = strings.pointTransactionNull,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historialPuntos) { punto ->
                        PuntoItemCard(punto = punto)
                    }
                }
            }
        }

        // Snackbar para mostrar mensajes
        if (mostrarSnackbar) {
            LaunchedEffect(mostrarSnackbar) {
                kotlinx.coroutines.delay(3000)
                mostrarSnackbar = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (canjeExitoso) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                ) {
                    Text(
                        text = if (canjeExitoso) "¡Puntos canjeados exitosamente!" else errorMessage ?: "Error",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
            }
        }
    }

    // ⭐ DIÁLOGO ACTUALIZADO con servicios y precios
    if (mostrarDialogoCanje) {
        DialogoCanjePuntos(
            onDismiss = { mostrarDialogoCanje = false },
            onCanjear = { cantidad, descripcion, idServicio ->
                canjearPuntos(cantidad, descripcion, idServicio)
                mostrarDialogoCanje = false
            },
            puntosDisponibles = puntosDisponibles ?: 0,
            servicioRepository = servicioRepository
        )
    }

    // Mostrar errores
    errorMessage?.let { mensaje ->
        LaunchedEffect(mensaje) {
            if (!mostrarSnackbar) {
                mostrarSnackbar = true
                kotlinx.coroutines.delay(3000)
                errorMessage = null
                mostrarSnackbar = false
            }
        }
    }
}

@Composable
fun PuntoItemCard(punto: PuntoDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = punto.tipoTransaccion,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    punto.descripcion?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = formatearFecha(punto.fechaTransaccion),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${if (punto.cantidadPuntos > 0) "+" else ""}${punto.cantidadPuntos}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (punto.cantidadPuntos > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoCanjePuntos(
    onDismiss: () -> Unit,
    onCanjear: (Int, String, Long?) -> Unit,
    puntosDisponibles: Int,
    servicioRepository: ServicioRepository
) {
    var cantidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var servicioSeleccionado by remember { mutableStateOf<ServicioResp?>(null) }
    var servicios by remember { mutableStateOf<List<ServicioResp>>(emptyList()) }
    var expandedServicio by remember { mutableStateOf(false) }
    var isLoadingServicios by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // 🎯 FUNCIÓN PARA OBTENER PRECIO EN PUNTOS - BASADO EN TUS SERVICIOS REALES
    fun obtenerPrecioPuntos(servicio: ServicioResp): Int {
        return when (servicio.idServicio) {
            3L -> 30   // Taller de Artesanía - 30 puntos
            4L -> 50   // Alimentación - 50 puntos
            5L -> 20   // Guía Turístico - 20 puntos
            6L -> 60   // Hospedaje Rural - 60 puntos
            else -> 25 // Otros servicios - 25 puntos por defecto
        }
    }

    // 🎯 FUNCIÓN PARA AUTO-LLENAR CANTIDAD RECOMENDADA
    fun establecerCantidadRecomendada(servicio: ServicioResp) {
        val puntosRequeridos = obtenerPrecioPuntos(servicio)
        val cantidadRecomendada = if (puntosDisponibles >= puntosRequeridos) {
            puntosRequeridos
        } else {
            puntosDisponibles
        }
        cantidad = cantidadRecomendada.toString()
    }

    // Cargar servicios al abrir el diálogo
    LaunchedEffect(Unit) {
        isLoadingServicios = true
        scope.launch {
            try {
                val listaServicios = servicioRepository.reportarServicios()
                servicios = listaServicios.filter { it.estado == "ACTIVO" }
            } catch (error: Exception) {
                println("Error cargando servicios: ${error.message}")
            }
            isLoadingServicios = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "💎 Canjear Puntos por Servicios",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(550.dp)
            ) {
                item {
                    // Info de puntos disponibles
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "💰 Puntos disponibles: $puntosDisponibles",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de servicio
                    Text(
                        text = "🎯 Selecciona un servicio:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedServicio,
                        onExpandedChange = { expandedServicio = !expandedServicio }
                    ) {
                        OutlinedTextField(
                            value = servicioSeleccionado?.nombreServicio ?: "Seleccionar servicio...",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Servicio") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicio)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedServicio,
                            onDismissRequest = { expandedServicio = false }
                        ) {
                            if (isLoadingServicios) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Cargando servicios...")
                                        }
                                    },
                                    onClick = { }
                                )
                            } else {
                                servicios.forEach { servicio ->
                                    val precioPuntos = obtenerPrecioPuntos(servicio)
                                    val emoji = when (servicio.idServicio) {
                                        3L -> "🎭" // Taller de Artesanía
                                        4L -> "🍽️" // Alimentación
                                        5L -> "👨‍🦯" // Guía Turístico
                                        6L -> "🏠" // Hospedaje Rural
                                        else -> "🎯"
                                    }

                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = emoji,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    )
                                                    Text(
                                                        text = servicio.nombreServicio,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "${servicio.tipo.nombre} • S/ ${servicio.precioBase}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = "💎 $precioPuntos pts",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            servicioSeleccionado = servicio
                                            expandedServicio = false
                                            descripcion = "Canje para: ${servicio.nombreServicio}"
                                            establecerCantidadRecomendada(servicio)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Mostrar detalles del servicio seleccionado
                    servicioSeleccionado?.let { servicio ->
                        val precioPuntos = obtenerPrecioPuntos(servicio)
                        val cantidadActual = cantidad.toIntOrNull() ?: 0

                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📋 ${servicio.descripcion}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "💵 Precio: S/ ${servicio.precioBase}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "💎 Costo: $precioPuntos puntos",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Mostrar información del canje
                                if (cantidadActual > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val porcentaje = (cantidadActual.toFloat() / precioPuntos * 100).toInt()
                                    val esCanjeCompleto = cantidadActual >= precioPuntos

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (esCanjeCompleto)
                                                Color(0xFF4CAF50).copy(alpha = 0.2f)
                                            else
                                                Color(0xFFFF9800).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            if (esCanjeCompleto) {
                                                Text(
                                                    text = "✅ Canje completo disponible",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF4CAF50)
                                                )
                                                Text(
                                                    text = "Obtienes el servicio completo",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            } else {
                                                Text(
                                                    text = "💡 Cubre ${porcentaje}% del servicio",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFF9800)
                                                )
                                                val descuentoPrecio = ((servicio.precioBase * (100 - porcentaje)) / 100).toInt()
                                                Text(
                                                    text = "Descuento del ${100 - porcentaje}% (Pagas S/ $descuentoPrecio)",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de cantidad sugerida
                    servicioSeleccionado?.let { servicio ->
                        val precioPuntos = obtenerPrecioPuntos(servicio)

                        Text(
                            text = "💎 Cantidad de puntos a canjear:",
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón para 25%
                            val puntos25 = (precioPuntos * 0.25).toInt()
                            if (puntosDisponibles >= puntos25 && puntos25 > 0) {
                                OutlinedButton(
                                    onClick = { cantidad = puntos25.toString() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("25%\n($puntos25)", fontSize = 10.sp, textAlign = TextAlign.Center)
                                }
                            }

                            // Botón para 50%
                            val puntos50 = (precioPuntos * 0.5).toInt()
                            if (puntosDisponibles >= puntos50 && puntos50 > 0) {
                                OutlinedButton(
                                    onClick = { cantidad = puntos50.toString() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("50%\n($puntos50)", fontSize = 10.sp, textAlign = TextAlign.Center)
                                }
                            }

                            // Botón para 100%
                            if (puntosDisponibles >= precioPuntos) {
                                Button(
                                    onClick = { cantidad = precioPuntos.toString() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("100%\n($precioPuntos)", fontSize = 10.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Campo de cantidad
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                                cantidad = newValue
                            }
                        },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = cantidad.toIntOrNull()?.let { it > puntosDisponibles || it <= 0 } == true,
                        supportingText = {
                            val cantidadInt = cantidad.toIntOrNull()
                            when {
                                cantidadInt != null && cantidadInt > puntosDisponibles -> {
                                    Text(
                                        text = "⚠️ No puedes canjear más puntos de los disponibles",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                cantidadInt != null && cantidadInt <= 0 -> {
                                    Text(
                                        text = "⚠️ La cantidad debe ser mayor a 0",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo de descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("📝 Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cantidadInt = cantidad.toIntOrNull()
                    if (cantidadInt != null && cantidadInt > 0 && cantidadInt <= puntosDisponibles) {
                        onCanjear(
                            cantidadInt,
                            descripcion.ifEmpty { "Canje de puntos" },
                            servicioSeleccionado?.idServicio
                        )
                    }
                },
                enabled = cantidad.toIntOrNull()?.let { it > 0 && it <= puntosDisponibles } == true
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Canjear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatearFecha(fechaTransaccion: Any): String {
    return try {
        when (fechaTransaccion) {
            is String -> {
                // Si es string, tomar solo los primeros 10 caracteres
                fechaTransaccion.take(10)
            }
            is List<*> -> {
                // Si es array [2025, 6, 28, 17, 0, 52, 493000000]
                val lista = fechaTransaccion as List<Int>
                if (lista.size >= 3) {
                    val year = lista[0]
                    val month = lista[1]
                    val day = lista[2]
                    String.format("%04d-%02d-%02d", year, month, day)
                } else {
                    "Fecha inválida"
                }
            }
            else -> {
                fechaTransaccion.toString().take(10)
            }
        }
    } catch (e: Exception) {
        "Fecha inválida"
    }
}