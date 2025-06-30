package pe.edu.upeu.granturismojpc.ui.presentation.screens.qr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.QrInfoDto
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp
import pe.edu.upeu.granturismojpc.repository.QrBlockchainRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import javax.inject.Inject

@Composable
fun QrManagementScreen(
    navController: NavHostController,
    viewModel: QrManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val artesaniasConQr by viewModel.artesaniasConQr.collectAsState()
    val qrInfo by viewModel.qrInfo.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Gestión de QRs",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { viewModel.cargarDatos() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar"
                    )
                }

                IconButton(onClick = {
                    navController.navigate(Destinations.QrScannerScreen.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear QR"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is QrManagementState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is QrManagementState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Información del servicio QR
                        qrInfo?.let { info ->
                            QrServiceInfoCard(info)
                        }
                    }

                    item {
                        // Estadísticas
                        estadisticas?.let { stats ->
                            QrStatisticsCard(stats)
                        }
                    }

                    item {
                        // Header de artesanías
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Artesanías con QR (${artesaniasConQr.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(onClick = {
                                // Navegar a pantalla de artesanías
                                navController.navigate(Destinations.ServicioArtesaniaMainSC.route)
                            }) {
                                Text("Ver todas")
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    items(artesaniasConQr) { artesania ->
                        ArtesaniaQrCard(
                            artesania = artesania,
                            onViewQr = {
                                navController.navigate(
                                    Destinations.QrGenerationScreen.createRoute(artesania.idArtesania)
                                )
                            },
                            onVerifyBlockchain = {
                                viewModel.verificarEstadoBlockchain(artesania.idArtesania)
                            }
                        )
                    }

                    if (artesaniasConQr.isEmpty()) {
                        item {
                            EmptyQrStateCard(
                                onCreateFirst = {
                                    navController.navigate(Destinations.ServicioArtesaniaMainSC.route)
                                }
                            )
                        }
                    }
                }
            }
            is QrManagementState.Error -> {
                ErrorStateCard(
                    message = (uiState as QrManagementState.Error).message,
                    onRetry = { viewModel.cargarDatos() }
                )
            }
        }
    }
}

@Composable
fun QrServiceInfoCard(qrInfo: QrInfoDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información del Servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    label = "Servicio",
                    value = qrInfo.servicio,
                    icon = Icons.Default.Build
                )
                InfoChip(
                    label = "Versión",
                    value = qrInfo.version,
                    icon = Icons.Default.Info
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    label = "Almacenamiento",
                    value = qrInfo.almacenamiento,
                    icon = Icons.Default.Storage
                )
                InfoChip(
                    label = "Cache",
                    value = if (qrInfo.cacheActivo) "Activo" else "Inactivo",
                    icon = Icons.Default.Memory
                )
            }
        }
    }
}

@Composable
fun QrStatisticsCard(estadisticas: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = estadisticas["tamaño_cache"]?.toString() ?: "0",
                    label = "QRs en Cache",
                    icon = Icons.Default.QrCode
                )

                StatisticItem(
                    value = estadisticas["qrs_generados"]?.toString() ?: "0",
                    label = "QRs Generados",
                    icon = Icons.Default.CheckCircle
                )
            }
        }
    }
}

@Composable
fun ArtesaniaQrCard(
    artesania: ServicioArtesaniaResp,
    onViewQr: () -> Unit,
    onVerifyBlockchain: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = artesania.tipoArtesania,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "por ${artesania.artesano}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = artesania.origenCultural,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Estado de verificación
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Green.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "QR Activo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Green
                        )
                    }
                }
            }

            if (artesania.hashBlockchain != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hash: ${artesania.hashBlockchain.take(16)}...",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerifyBlockchain,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verificar")
                }

                Button(
                    onClick = onViewQr,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver QR")
                }
            }
        }
    }
}

@Composable
fun EmptyQrStateCard(
    onCreateFirst: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No hay QRs generados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Crea tu primera artesanía y genera un QR para verificación de autenticidad",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onCreateFirst) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Artesanía")
            }
        }
    }
}

@Composable
fun ErrorStateCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Error al cargar datos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

sealed class QrManagementState {
    object Loading : QrManagementState()
    object Success : QrManagementState()
    data class Error(val message: String) : QrManagementState()
}

@HiltViewModel
class QrManagementViewModel @Inject constructor(
    private val qrBlockchainRepository: QrBlockchainRepository,
    private val servicioArtesaniaRepository: ServicioArtesaniaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QrManagementState>(QrManagementState.Loading)
    val uiState: StateFlow<QrManagementState> = _uiState

    private val _artesaniasConQr = MutableStateFlow<List<ServicioArtesaniaResp>>(emptyList())
    val artesaniasConQr: StateFlow<List<ServicioArtesaniaResp>> = _artesaniasConQr

    private val _qrInfo = MutableStateFlow<QrInfoDto?>(null)
    val qrInfo: StateFlow<QrInfoDto?> = _qrInfo

    private val _estadisticas = MutableStateFlow<Map<String, Any>>(emptyMap())
    val estadisticas: StateFlow<Map<String, Any>> = _estadisticas

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = QrManagementState.Loading

            try {
                // Cargar información del servicio QR
                qrBlockchainRepository.obtenerInfoQr()
                    .onSuccess { info ->
                        _qrInfo.value = info
                    }

                // Cargar estadísticas
                qrBlockchainRepository.obtenerEstadisticasQr()
                    .onSuccess { stats ->
                        _estadisticas.value = stats
                    }

                // Cargar artesanías con QR (filtrar las que tienen hash)
                val todasArtesanias = servicioArtesaniaRepository.reportarServicioArtesanias()
                val artesaniasConQr = todasArtesanias.filter {
                    !it.hashBlockchain.isNullOrEmpty()
                }
                _artesaniasConQr.value = artesaniasConQr

                _uiState.value = QrManagementState.Success

            } catch (e: Exception) {
                _uiState.value = QrManagementState.Error(
                    e.message ?: "Error al cargar datos"
                )
            }
        }
    }

    fun verificarEstadoBlockchain(idArtesania: Long) {
        viewModelScope.launch {
            qrBlockchainRepository.verificarEstadoBlockchain(idArtesania)
                .onSuccess { response ->
                    // Aquí podrías mostrar el resultado en un Snackbar o Dialog
                }
                .onFailure { exception ->
                    // Manejar error
                }
        }
    }
}