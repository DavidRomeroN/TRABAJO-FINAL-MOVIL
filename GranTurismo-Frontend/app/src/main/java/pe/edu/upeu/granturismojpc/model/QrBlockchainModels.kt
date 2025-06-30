package pe.edu.upeu.granturismojpc.model

import kotlinx.serialization.Serializable

/**
 * DTO para la respuesta de generación QR + Blockchain
 */
@Serializable
data class QrCodeResponseDto(
    val qrImageUrl: String,
    val blockchainExplorerUrl: String,
    val hashBlockchain: String,
    val mensaje: String
)

/**
 * DTO para verificación de artesano
 */
@Serializable
data class VerificacionArtesanoDto(
    // Información del artesano
    val artesano: String,
    val tipoArtesania: String,
    val origenCultural: String,
    val descripcionArtesania: String,

    // Detalles del taller
    val nivelDificultad: String,
    val duracionTaller: Int,
    val incluyeMaterial: Boolean,
    val maxParticipantes: Int,
    val visitaTaller: Boolean,

    // Información de verificación blockchain
    val hashBlockchain: String?,
    val urlBlockchainExplorer: String?,
    val fechaRegistroBlockchain: String?,
    val verificadoEnBlockchain: Boolean,

    // Información adicional de autenticidad
    val codigoVerificacion: String,
    val estadoVerificacion: String, // AUTENTICO, PENDIENTE, NO_VERIFICADO
    val mensajeVerificacion: String,

    // URLs útiles
    val urlVerificacionCompleta: String,
    val urlQrOriginal: String?,

    // Metadatos
    val fechaGeneracionQr: String,
    val versionSistema: String,
    val esArtesanoVerificado: Boolean
)

/**
 * DTO para verificación rápida del estado
 */
@Serializable
data class EstadoVerificacionDto(
    val valido: Boolean,
    val verificado: Boolean,
    val artesano: String,
    val tipoArtesania: String,
    val estado: String, // VERIFICADO, PENDIENTE, NO_REGISTRADO
    val timestamp: String
)

/**
 * DTO para solicitud de generación QR
 */
@Serializable
data class QrGenerationRequestDto(
    val texto: String,
    val ancho: Int = 300,
    val alto: Int = 300
)

/**
 * DTO para información QR
 */
@Serializable
data class QrInfoDto(
    val servicio: String,
    val version: String,
    val almacenamiento: String,
    val formatosSoportados: List<String>,
    val tamañoDefecto: String,
    val carpetaCloudinary: String,
    val cacheActivo: Boolean
)

/**
 * Estados para el manejo de QR y Blockchain
 */
sealed class QrBlockchainState {
    object Loading : QrBlockchainState()
    object Initial : QrBlockchainState()
    data class Success(val response: QrCodeResponseDto) : QrBlockchainState()
    data class VerificationSuccess(val verification: VerificacionArtesanoDto) : QrBlockchainState()
    data class Error(val message: String) : QrBlockchainState()
}

/**
 * Estados para la cámara QR Scanner
 */
sealed class QrScannerState {
    object Idle : QrScannerState()
    object Scanning : QrScannerState()
    data class Success(val qrContent: String) : QrScannerState()
    data class Error(val message: String) : QrScannerState()
}