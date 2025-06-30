package org.example.granturismo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del endpoint que genera el QR
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeResponseDTO {

    private String qrImageUrl; // URL donde se puede acceder a la imagen del Código QR
    private String blockchainExplorerUrl; // URL del explorador de la blockchain donde se verifica la transacción
    private String hashBlockchain; // Hash de la transacción en blockchain
    private String mensaje; // Mensaje adicional para el usuario
}