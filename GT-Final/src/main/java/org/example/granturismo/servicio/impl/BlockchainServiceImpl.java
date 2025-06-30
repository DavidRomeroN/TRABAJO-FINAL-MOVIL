package org.example.granturismo.servicio.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.ArtesaniaBlockchainDTO;
import org.example.granturismo.excepciones.BlockchainException;
import org.example.granturismo.servicio.IBlockchainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction; // Importa RawTransaction
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction; // Este import sigue siendo válido para el tipo de retorno de EthGetTransactionReceipt si lo necesitas
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Implementación del servicio de blockchain usando Web3j
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainServiceImpl implements IBlockchainService {

    private final Web3j web3j;
    private final ObjectMapper objectMapper;

    @Value("${blockchain.private-key}")
    private String privateKey;

    @Value("${blockchain.explorer-url:https://mumbai.polygonscan.com/tx/}")
    private String explorerBaseUrl;

    @Value("${blockchain.gas-price:20000000000}") // 20 Gwei
    private BigInteger gasPrice;

    @Value("${blockchain.gas-limit:300000}")
    private BigInteger gasLimit;

    @Override
    public String registrarArtesaniaEnBlockchain(ArtesaniaBlockchainDTO artesaniaData) throws BlockchainException {
        try {
            log.info("Iniciando registro en blockchain para artesanía ID: {}", artesaniaData.getIdArtesania());

            // Convertir datos a JSON
            String jsonData = objectMapper.writeValueAsString(artesaniaData);
            log.debug("Datos JSON a registrar: {}", jsonData);

            // Convertir JSON a hexadecimal para envío
            String hexData = Numeric.toHexString(jsonData.getBytes(StandardCharsets.UTF_8)); // No necesita "0x" al principio aquí, RawTransaction.createTransaction lo añade.

            // Configurar credenciales
            Credentials credentials = Credentials.create(privateKey);
            // TransactionManager transactionManager = new RawTransactionManager(web3j, credentials); // No necesario directamente aquí si usas web3j.ethSendRawTransaction

            // Obtener nonce
            BigInteger nonce = web3j.ethGetTransactionCount(
                    credentials.getAddress(),
                    org.web3j.protocol.core.DefaultBlockParameterName.LATEST
            ).send().getTransactionCount();

            // *** MODIFICACIÓN AQUÍ: Usar RawTransaction.createTransaction ***
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    credentials.getAddress(), // Dirección 'to' (enviamos a nosotros mismos)
                    BigInteger.ZERO,        // Valor (BigInteger.ZERO para no enviar Ether)
                    hexData                 // Los datos JSON en formato hexadecimal
            );

            // Firmar la transacción
            byte[] signedMessage = org.web3j.crypto.TransactionEncoder.signMessage(rawTransaction, credentials);
            String encodedTransaction = Numeric.toHexString(signedMessage);

            // Enviar la transacción firmada
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(encodedTransaction).send();

            if (ethSendTransaction.hasError()) {
                throw new BlockchainException("Error al enviar transacción: " + ethSendTransaction.getError().getMessage());
            }

            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("Transacción enviada exitosamente. Hash: {}", transactionHash);

            // Esperar confirmación (opcional, para desarrollo)
            esperarConfirmacion(transactionHash);

            return transactionHash;

        } catch (Exception e) {
            log.error("Error al registrar artesanía en blockchain", e);
            throw new BlockchainException("Error al registrar en blockchain: " + e.getMessage(), e);
        }
    }

    @Override
    public String generarBlockchainExplorerUrl(String hashTransaccion) {
        if (hashTransaccion == null || hashTransaccion.trim().isEmpty()) {
            throw new IllegalArgumentException("Hash de transacción no puede ser nulo o vacío");
        }
        // Asegúrate de que el hash tenga el prefijo 0x si tu explorer lo espera (Polygonscan lo hace)
        String formattedHash = hashTransaccion.startsWith("0x") ? hashTransaccion : "0x" + hashTransaccion;
        return explorerBaseUrl + formattedHash;
    }

    @Override
    public boolean verificarTransaccion(String hashTransaccion) throws BlockchainException {
        try {
            // Asegúrate de que el hash tenga el prefijo 0x
            String formattedHash = hashTransaccion.startsWith("0x") ? hashTransaccion : "0x" + hashTransaccion;

            EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(formattedHash).send();
            Optional<TransactionReceipt> receiptOptional = receiptResponse.getTransactionReceipt();

            if (receiptOptional.isPresent()) {
                TransactionReceipt receipt = receiptOptional.get();
                // Una transacción está confirmada si tiene receipt y status "0x1"
                return "0x1".equals(receipt.getStatus());
            }

            return false;
        } catch (Exception e) {
            log.error("Error al verificar transacción: {}", hashTransaccion, e);
            throw new BlockchainException("Error al verificar transacción: " + e.getMessage(), e);
        }
    }

    @Override
    public String obtenerDetallesTransaccion(String hashTransaccion) throws BlockchainException {
        try {
            // Asegúrate de que el hash tenga el prefijo 0x
            String formattedHash = hashTransaccion.startsWith("0x") ? hashTransaccion : "0x" + hashTransaccion;

            EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(formattedHash).send();
            Optional<TransactionReceipt> receiptOptional = receiptResponse.getTransactionReceipt();

            if (receiptOptional.isPresent()) {
                return objectMapper.writeValueAsString(receiptOptional.get());
            } else {
                return "{\"status\": \"pending\", \"message\": \"Transacción aún no confirmada\"}";
            }
        } catch (Exception e) {
            log.error("Error al obtener detalles de transacción: {}", hashTransaccion, e);
            throw new BlockchainException("Error al obtener detalles: " + e.getMessage(), e);
        }
    }

    /**
     * Espera a que la transacción sea confirmada (método auxiliar para desarrollo)
     */
    private void esperarConfirmacion(String transactionHash) {
        try {
            log.info("Esperando confirmación de transacción: {}", transactionHash);

            // Intentar hasta 30 veces (5 minutos aprox)
            for (int i = 0; i < 30; i++) {
                Thread.sleep(10000); // Esperar 10 segundos

                if (verificarTransaccion(transactionHash)) {
                    log.info("Transacción confirmada: {}", transactionHash);
                    return;
                }

                log.debug("Intento {}/30 - Transacción aún pendiente: {}", i + 1, transactionHash);
            }

            log.warn("Transacción no confirmada después de 5 minutos: {}", transactionHash);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrumpida la espera de confirmación para: {}", transactionHash);
        } catch (Exception e) {
            log.error("Error al esperar confirmación: {}", transactionHash, e);
        }
    }
}