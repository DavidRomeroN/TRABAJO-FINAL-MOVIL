package pe.edu.upeu.granturismojpc.ui.presentation.screens.qr

import android.Manifest
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.QrScannerState
import pe.edu.upeu.granturismojpc.repository.QrBlockchainRepository
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(
    navController: NavHostController,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val scannerState by viewModel.scannerState.collectAsState()
    var flashEnabled by remember { mutableStateOf(false) }

    // State for Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // Coroutine scope for launching snackbar

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) } // Provide the SnackbarHost
    ) { paddingValues -> // Use paddingValues to apply padding to the content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp) // Your original padding
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Escanear QR de Artesano",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { flashEnabled = !flashEnabled }) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Default.FlashlightOff else Icons.Default.FlashlightOn,
                        contentDescription = if (flashEnabled) "Apagar flash" else "Encender flash",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                !cameraPermissionState.status.isGranted -> {
                    PermissionDeniedContent(
                        rationaleMessage = if (cameraPermissionState.status.shouldShowRationale) {
                            "La cámara es necesaria para escanear códigos QR de artesanos. " +
                                    "Por favor, concede el permiso para verificar la autenticidad."
                        } else {
                            "Se requiere permiso de cámara para escanear códigos QR."
                        },
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }
                else -> {
                    // Cámara y UI de escaneo
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraPreview(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            flashEnabled = flashEnabled,
                            onQrCodeDetected = { qrContent ->
                                viewModel.processQrCode(qrContent)
                            }
                        )

                        // Overlay con información
                        ScanOverlay(
                            modifier = Modifier.align(Alignment.Center),
                            scannerState = scannerState
                        )

                        // Instrucciones
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = "Apunta la cámara hacia el código QR del artesano para verificar su autenticidad",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Manejar estados del scanner
            LaunchedEffect(scannerState) {
                val currentScannerState = scannerState // Immutable copy
                when (currentScannerState) {
                    is QrScannerState.Success -> {
                        val qrContent = currentScannerState.qrContent
                        if (qrContent.contains("/verificar/artesano/")) {
                            val codigoVerificacion = qrContent.substringAfterLast("/")
                            navController.navigate(
                                Destinations.VerificacionArtesanoScreen.createRoute(codigoVerificacion)
                            )
                        } else {
                            // If the QR content is not valid for artisan verification, show an error.
                            // The ViewModel already calls showError, but we need to ensure the UI reacts.
                            // Instead of calling viewModel.showError again, we just show the message.
                            scope.launch { // Launch in the rememberCoroutineScope
                                snackbarHostState.showSnackbar("Este QR no corresponde a un artesano verificado")
                            }
                            viewModel.resetState() // Reset to allow rescanning
                        }
                    }
                    is QrScannerState.Error -> {
                        // Show error directly using the message from currentScannerState
                        scope.launch { // Launch in the rememberCoroutineScope
                            snackbarHostState.showSnackbar(currentScannerState.message)
                        }
                        viewModel.resetState() // Reset to allow rescanning
                    }
                    else -> { /* No action needed */ }
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedContent(
    rationaleMessage: String,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = rationaleMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRequestPermission) {
            Text("Conceder Permiso")
        }
    }
}

@Composable
fun ScanOverlay(
    modifier: Modifier = Modifier,
    scannerState: QrScannerState
) {
    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        // Marco de escaneo
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                when (scannerState) {
                    is QrScannerState.Success -> Color.Green
                    is QrScannerState.Error -> Color.Red
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        ) {}

        // Indicador de estado
        when (scannerState) {
            is QrScannerState.Scanning -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            is QrScannerState.Success -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "QR detectado",
                    tint = Color.Green,
                    modifier = Modifier.size(48.dp)
                )
            }
            is QrScannerState.Error -> {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            }
            else -> { /* No indicator */ }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    flashEnabled: Boolean,
    onQrCodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            val previewView = PreviewView(viewContext)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(viewContext)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(imageProxy, onQrCodeDetected)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )

                    // Controlar flash
                    camera.cameraControl.enableTorch(flashEnabled)

                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(viewContext))

            previewView
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    onQrCodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrContent ->
                        onQrCodeDetected(qrContent)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("QRScanner", "Error processing image", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val qrBlockchainRepository: QrBlockchainRepository
) : ViewModel() {

    private val _scannerState = MutableStateFlow<QrScannerState>(QrScannerState.Idle)
    val scannerState: StateFlow<QrScannerState> = _scannerState

    fun processQrCode(qrContent: String) {
        viewModelScope.launch {
            _scannerState.value = QrScannerState.Scanning

            try {
                if (qrContent.contains("/verificar/artesano/")) {
                    _scannerState.value = QrScannerState.Success(qrContent)
                } else {
                    _scannerState.value = QrScannerState.Error("QR no válido para verificación de artesano")
                }
            } catch (e: Exception) {
                _scannerState.value = QrScannerState.Error("Error al procesar QR: ${e.message}")
            }
        }
    }

    fun showError(message: String) {
        _scannerState.value = QrScannerState.Error(message)
    }

    fun resetState() {
        _scannerState.value = QrScannerState.Idle
    }
}