package pe.edu.upeu.granturismojpc.ui.presentation.screens.qr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage // <-- THIS IS THE FIX
import coil3.request.ImageRequest
import coil3.request.crossfade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.QrBlockchainState
import pe.edu.upeu.granturismojpc.model.QrCodeResponseDto
import pe.edu.upeu.granturismojpc.repository.QrBlockchainRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import javax.inject.Inject

@Composable
fun QrGenerationScreen(
    idArtesania: Long,
    navController: NavHostController,
    viewModel: QrGenerationViewModel = hiltViewModel()
) {
    val qrState by viewModel.qrState.collectAsState()
    val artesaniaInfo by viewModel.artesaniaInfo.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(idArtesania) {
        viewModel.cargarInfoArtesania(idArtesania)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                text = "Generar QR + Blockchain",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { viewModel.cargarInfoArtesania(idArtesania) }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información de la artesanía
        artesaniaInfo?.let { artesania ->
            ArtesaniaInfoCard(artesania)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Estado del QR y Blockchain
        when (qrState) {
            is QrBlockchainState.Initial -> {
                InitialQrGenerationCard(
                    onGenerateQr = { viewModel.generarQrYBlockchain(idArtesania) }
                )
            }
            is QrBlockchainState.Loading -> {
                LoadingQrGenerationCard()
            }
            is QrBlockchainState.Success -> {
                SuccessfulQrGenerationCard(
                    qrResponse = (qrState as QrBlockchainState.Success).response,
                    onCopyToClipboard = { text ->
                        clipboardManager.setText(AnnotatedString(text))
                    },
                    onRegenerateQr = { viewModel.regenerarQr(idArtesania) },
                    onVerifyBlockchain = { viewModel.verificarEstadoBlockchain(idArtesania) }
                )
            }
            is QrBlockchainState.Error -> {
                ErrorQrGenerationCard(
                    message = (qrState as QrBlockchainState.Error).message,
                    onRetry = { viewModel.generarQrYBlockchain(idArtesania) }
                )
            }
            else -> { /* No action needed */ }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información sobre blockchain
        BlockchainInfoCard()
    }
}

@Composable
fun ArtesaniaInfoCard(artesania: pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información de la Artesanía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = artesania.tipoArtesania,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "por ${artesania.artesano}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Origen: ${artesania.origenCultural}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Nivel: ${artesania.nivelDificultad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InitialQrGenerationCard(
    onGenerateQr: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Generar QR de Verificación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Esta acción registrará la artesanía en blockchain de forma inmutable y generará un código QR para verificación de autenticidad.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onGenerateQr,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generar QR + Blockchain")
            }
        }
    }
}

@Composable
fun LoadingQrGenerationCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Generando QR y registrando en blockchain...",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Este proceso puede tomar unos segundos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SuccessfulQrGenerationCard(
    qrResponse: QrCodeResponseDto,
    onCopyToClipboard: (String) -> Unit,
    onRegenerateQr: () -> Unit,
    onVerifyBlockchain: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header exitoso
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QR y Blockchain Generados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = qrResponse.mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen QR
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Código QR de Verificación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(qrResponse.qrImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Código QR",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Escanear para verificar autenticidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información de URLs
            QrUrlInfo(
                label = "URL del QR:",
                url = qrResponse.qrImageUrl,
                onCopy = onCopyToClipboard
            )

            QrUrlInfo(
                label = "Explorador Blockchain:",
                url = qrResponse.blockchainExplorerUrl,
                onCopy = onCopyToClipboard
            )

            QrUrlInfo(
                label = "Hash Blockchain:",
                url = qrResponse.hashBlockchain,
                onCopy = onCopyToClipboard
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRegenerateQr,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regenerar")
                }

                Button(
                    onClick = onVerifyBlockchain,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verificar")
                }
            }
        }
    }
}

@Composable
fun QrUrlInfo(
    label: String,
    url: String,
    onCopy: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (url.length > 50) "${url.take(50)}..." else url,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            IconButton(onClick = { onCopy(url) }) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copiar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ErrorQrGenerationCard(
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
                text = "Error al Generar QR",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
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
fun BlockchainInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¿Cómo Funciona la Verificación?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "1. Los datos se registran en la blockchain Ethereum Sepolia de forma inmutable",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "2. Se genera un código QR que apunta a una página de verificación",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "3. Los clientes pueden escanear el QR para verificar la autenticidad",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "4. La información no puede ser alterada una vez registrada",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@HiltViewModel
class QrGenerationViewModel @Inject constructor(
    private val qrBlockchainRepository: QrBlockchainRepository,
    private val servicioArtesaniaRepository: ServicioArtesaniaRepository
) : ViewModel() {

    private val _qrState = MutableStateFlow<QrBlockchainState>(QrBlockchainState.Initial)
    val qrState: StateFlow<QrBlockchainState> = _qrState

    private val _artesaniaInfo = MutableStateFlow<pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp?>(null)
    val artesaniaInfo: StateFlow<pe.edu.upeu.granturismojpc.model.ServicioArtesaniaResp?> = _artesaniaInfo

    fun cargarInfoArtesania(idArtesania: Long) {
        viewModelScope.launch {
            try {
                val artesania = servicioArtesaniaRepository.buscarServicioArtesaniaId(idArtesania)
                _artesaniaInfo.value = artesania
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun generarQrYBlockchain(idArtesania: Long) {
        viewModelScope.launch {
            _qrState.value = QrBlockchainState.Loading

            qrBlockchainRepository.generarQrYBlockchainParaArtesania(idArtesania)
                .onSuccess { response ->
                    _qrState.value = QrBlockchainState.Success(response)
                }
                .onFailure { exception ->
                    _qrState.value = QrBlockchainState.Error(
                        exception.message ?: "Error al generar QR y blockchain"
                    )
                }
        }
    }

    fun regenerarQr(idArtesania: Long) {
        viewModelScope.launch {
            _qrState.value = QrBlockchainState.Loading

            qrBlockchainRepository.regenerarQr(idArtesania)
                .onSuccess { response ->
                    // Convert response to QrCodeResponseDto format
                    val qrResponse = QrCodeResponseDto(
                        qrImageUrl = response["qrImageUrl"] ?: "",
                        blockchainExplorerUrl = response["blockchainExplorerUrl"] ?: "",
                        hashBlockchain = response["hashBlockchain"] ?: "",
                        mensaje = response["mensaje"] ?: "QR regenerado exitosamente"
                    )
                    _qrState.value = QrBlockchainState.Success(qrResponse)
                }
                .onFailure { exception ->
                    _qrState.value = QrBlockchainState.Error(
                        exception.message ?: "Error al regenerar QR"
                    )
                }
        }
    }

    fun verificarEstadoBlockchain(idArtesania: Long) {
        viewModelScope.launch {
            qrBlockchainRepository.verificarEstadoBlockchain(idArtesania)
                .onSuccess { response ->
                    // Show verification status
                    val estado = response["estado"] ?: "Desconocido"
                    // You can update UI or show a snackbar here
                }
                .onFailure { exception ->
                    _qrState.value = QrBlockchainState.Error(
                        exception.message ?: "Error al verificar estado blockchain"
                    )
                }
        }
    }
}