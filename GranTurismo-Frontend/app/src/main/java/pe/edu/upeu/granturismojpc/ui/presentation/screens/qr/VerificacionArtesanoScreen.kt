package pe.edu.upeu.granturismojpc.ui.presentation.screens.qr

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage // <-- THIS IS THE FIX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.QrBlockchainState
import pe.edu.upeu.granturismojpc.model.VerificacionArtesanoDto
import pe.edu.upeu.granturismojpc.repository.QrBlockchainRepository
import javax.inject.Inject

@Composable
fun VerificacionArtesanoScreen(
    codigoVerificacion: String,
    navController: NavHostController,
    viewModel: VerificacionArtesanoViewModel = hiltViewModel()
) {
    val verificacionState by viewModel.verificacionState.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(codigoVerificacion) {
        viewModel.verificarArtesano(codigoVerificacion)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val currentVerificacionState = verificacionState
        when (currentVerificacionState) { // Use the local copy here
            is QrBlockchainState.Loading -> {
                LoadingVerificationScreen()
            }
            is QrBlockchainState.VerificationSuccess -> {
                // Now smart cast works on 'currentVerificacionState'
                VerifiedArtesanContent(
                    verificacion = currentVerificacionState.verification,
                    onOpenBlockchainExplorer = { url ->
                        uriHandler.openUri(url)
                    },
                    onGoBack = { navController.popBackStack() }
                )
            }
            is QrBlockchainState.Error -> {
                // Now smart cast works on 'currentVerificacionState'
                ErrorVerificationScreen(
                    message = currentVerificacionState.message,
                    codigoVerificacion = codigoVerificacion,
                    onRetry = { viewModel.verificarArtesano(codigoVerificacion) },
                    onGoBack = { navController.popBackStack() }
                )
            }
            else -> {
                InitialVerificationScreen()
            }
        }
    }
}

@Composable
fun LoadingVerificationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Verificando autenticidad...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Consultando blockchain",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun VerifiedArtesanContent(
    verificacion: VerificacionArtesanoDto,
    onOpenBlockchainExplorer: (String) -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header de verificación exitosa
        VerificationSuccessHeader(verificacion.estadoVerificacion)

        Spacer(modifier = Modifier.height(24.dp))

        // Información del artesano
        ArtesanInfoCard(verificacion)

        Spacer(modifier = Modifier.height(16.dp))

        // Información de la artesanía
        ArtesaniaDetailsCard(verificacion)

        Spacer(modifier = Modifier.height(16.dp))

        // Información de blockchain
        BlockchainInfoCard(
            verificacion = verificacion,
            onOpenExplorer = onOpenBlockchainExplorer
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de acción
        ActionButtons(
            verificacion = verificacion,
            onOpenExplorer = onOpenBlockchainExplorer,
            onGoBack = onGoBack
        )
    }
}

@Composable
fun VerificationSuccessHeader(estadoVerificacion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (estadoVerificacion) {
                "AUTENTICO" -> Color(0xFF4CAF50)
                "PENDIENTE" -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (estadoVerificacion) {
                    "AUTENTICO" -> Icons.Default.Verified
                    "PENDIENTE" -> Icons.Default.HourglassEmpty
                    else -> Icons.Default.Error
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (estadoVerificacion) {
                    "AUTENTICO" -> "✅ ARTESANÍA VERIFICADA"
                    "PENDIENTE" -> "⏳ VERIFICACIÓN PENDIENTE"
                    else -> "❌ NO VERIFICADO"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = when (estadoVerificacion) {
                    "AUTENTICO" -> "Esta artesanía ha sido autenticada mediante blockchain"
                    "PENDIENTE" -> "La verificación está siendo procesada"
                    else -> "Esta artesanía no ha sido verificada"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ArtesanInfoCard(verificacion: VerificacionArtesanoDto) {
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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información del Artesano",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                label = "Artesano:",
                value = verificacion.artesano,
                icon = Icons.Default.Badge
            )

            InfoRow(
                label = "Tipo de Artesanía:",
                value = verificacion.tipoArtesania,
                icon = Icons.Default.Palette
            )

            InfoRow(
                label = "Origen Cultural:",
                value = verificacion.origenCultural,
                icon = Icons.Default.Public
            )
        }
    }
}

@Composable
fun ArtesaniaDetailsCard(verificacion: VerificacionArtesanoDto) {
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
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalles del Taller",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = verificacion.descripcionArtesania,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailChip(
                    label = "Nivel",
                    value = verificacion.nivelDificultad,
                    icon = Icons.Default.Star
                )

                DetailChip(
                    label = "Duración",
                    value = "${verificacion.duracionTaller} min",
                    icon = Icons.Default.Schedule
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailChip(
                    label = "Participantes",
                    value = "Máx. ${verificacion.maxParticipantes}",
                    icon = Icons.Default.Group
                )

                DetailChip(
                    label = "Materiales",
                    value = if (verificacion.incluyeMaterial) "Incluidos" else "No incluidos",
                    icon = Icons.Default.Inventory
                )
            }
        }
    }
}

@Composable
fun BlockchainInfoCard(
    verificacion: VerificacionArtesanoDto,
    onOpenExplorer: (String) -> Unit
) {
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
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verificación Blockchain",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                label = "Estado:",
                value = verificacion.estadoVerificacion,
                icon = Icons.Default.CheckCircle
            )

            InfoRow(
                label = "Código de Verificación:",
                value = verificacion.codigoVerificacion,
                icon = Icons.Default.QrCode
            )

            if (verificacion.hashBlockchain != null) {
                InfoRow(
                    label = "Hash Blockchain:",
                    value = "${verificacion.hashBlockchain.take(10)}...${verificacion.hashBlockchain.takeLast(10)}",
                    icon = Icons.Default.Tag
                )
            }

            if (verificacion.fechaRegistroBlockchain != null) {
                InfoRow(
                    label = "Fecha de Registro:",
                    value = verificacion.fechaRegistroBlockchain,
                    icon = Icons.Default.CalendarToday
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = verificacion.mensajeVerificacion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ActionButtons(
    verificacion: VerificacionArtesanoDto,
    onOpenExplorer: (String) -> Unit,
    onGoBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onGoBack,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Volver")
        }

        if (verificacion.urlBlockchainExplorer != null) {
            Button(
                onClick = { onOpenExplorer(verificacion.urlBlockchainExplorer) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver Blockchain")
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DetailChip(
    label: String,
    value: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
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
fun ErrorVerificationScreen(
    message: String,
    codigoVerificacion: String,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Código de Verificación No Válido",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Posibles causas:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("• El código QR ha expirado")
                Text("• El código no existe en el sistema")
                Text("• La artesanía no ha sido registrada aún")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Código ingresado: $codigoVerificacion",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onGoBack) {
                Text("Volver")
            }

            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun InitialVerificationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Inicializando verificación...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@HiltViewModel
class VerificacionArtesanoViewModel @Inject constructor(
    private val qrBlockchainRepository: QrBlockchainRepository
) : ViewModel() {

    private val _verificacionState = MutableStateFlow<QrBlockchainState>(QrBlockchainState.Initial)
    val verificacionState: StateFlow<QrBlockchainState> = _verificacionState

    fun verificarArtesano(codigoVerificacion: String) {
        viewModelScope.launch {
            _verificacionState.value = QrBlockchainState.Loading

            qrBlockchainRepository.verificarArtesanoPorCodigo(codigoVerificacion)
                .onSuccess { verificacion ->
                    _verificacionState.value = QrBlockchainState.VerificationSuccess(verificacion)
                }
                .onFailure { exception ->
                    _verificacionState.value = QrBlockchainState.Error(
                        exception.message ?: "Error al verificar artesano"
                    )
                }
        }
    }
}