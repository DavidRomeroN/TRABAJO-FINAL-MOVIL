package org.example.granturismo.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Configuración para la interacción con blockchain
 */
@Configuration
@Slf4j
public class BlockchainConfig {

    @Value("${blockchain.node-url}")
    private String nodeUrl;

    @Value("${blockchain.connection-timeout:60000}")
    private long connectionTimeout;

    @Value("${blockchain.read-timeout:60000}")
    private long readTimeout;

    /**
     * Bean para configurar Web3j con timeouts personalizados
     */
    @Bean
    public Web3j web3j() {
        log.info("Configurando conexión Web3j a: {}", nodeUrl);

        // Configurar cliente HTTP con timeouts
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .build();

        HttpService httpService = new HttpService(nodeUrl, httpClient);

        Web3j web3j = Web3j.build(httpService);

        log.info("Web3j configurado exitosamente");
        return web3j;
    }
}