package org.example.granturismo.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador temporal para probar la conexión blockchain
 * ELIMINAR después de verificar que todo funciona
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class BlockchainTestController {

    private final Web3j web3j;

    @Value("${blockchain.private-key}")
    private String privateKey;

    @GetMapping("/blockchain-connection")
    public Map<String, Object> testBlockchainConnection() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Test 1: Verificar conexión
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            result.put("conexion", "✅ EXITOSA");
            result.put("cliente", clientVersion);

            // Test 2: Obtener número de bloque actual
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            result.put("bloqueActual", blockNumber.toString());

            // Test 3: Verificar wallet y balance
            Credentials credentials = Credentials.create(privateKey);
            String address = credentials.getAddress();
            result.put("walletAddress", address);

            // Obtener balance
            BigInteger balanceWei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .send().getBalance();

            // Convertir Wei a ETH
            BigDecimal balanceEth = new BigDecimal(balanceWei).divide(new BigDecimal("1000000000000000000"));
            result.put("balanceETH", balanceEth.toString());

            // Test 4: Verificar gas price
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            result.put("gasPriceWei", gasPrice.toString());
            result.put("gasPriceGwei", gasPrice.divide(BigInteger.valueOf(1000000000)).toString());

            result.put("estado", "✅ TODO CORRECTO - Listo para usar blockchain");

            log.info("Test blockchain exitoso - Wallet: {} - Balance: {} ETH", address, balanceEth);

        } catch (Exception e) {
            result.put("conexion", "❌ ERROR");
            result.put("error", e.getMessage());
            result.put("estado", "❌ REVISAR CONFIGURACIÓN");

            log.error("Error en test blockchain", e);
        }

        return result;
    }

    @GetMapping("/qr-storage")
    public Map<String, Object> testQrStorage() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Verificar directorio QR
            java.nio.file.Path qrPath = java.nio.file.Paths.get("./qr-codes");

            if (!java.nio.file.Files.exists(qrPath)) {
                java.nio.file.Files.createDirectories(qrPath);
                result.put("directorio", "✅ CREADO");
            } else {
                result.put("directorio", "✅ EXISTE");
            }

            result.put("ruta", qrPath.toAbsolutePath().toString());
            result.put("escribible", java.nio.file.Files.isWritable(qrPath));
            result.put("estado", "✅ ALMACENAMIENTO QR LISTO");

        } catch (Exception e) {
            result.put("directorio", "❌ ERROR");
            result.put("error", e.getMessage());
            result.put("estado", "❌ REVISAR PERMISOS");
        }

        return result;
    }
}