package org.example.granturismo.servicio.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.VerificacionArtesanoDTO;
import org.example.granturismo.modelo.ServicioArtesania;
import org.example.granturismo.modelo.TransaccionBlockchainArtesania;
import org.example.granturismo.repositorio.IServicioArtesaniaRepository;
import org.example.granturismo.repositorio.ITransaccionBlockchainArtesaniaRepository;
import org.example.granturismo.servicio.IBlockchainService;
import org.example.granturismo.servicio.IVerificacionArtesanoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de verificación de artesanos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificacionArtesanoServiceImpl implements IVerificacionArtesanoService {

    private final IServicioArtesaniaRepository artesaniaRepository;
    private final ITransaccionBlockchainArtesaniaRepository transaccionRepository;
    private final IBlockchainService blockchainService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.version:1.0.0}")
    private String versionSistema;

    @Override
    public String generarCodigoVerificacion(Long idArtesania) {
        // Generar código único basado en ID + timestamp + UUID corto
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("ART-%d-%s-%s", idArtesania, timestamp, uuid).toUpperCase();
    }

    @Override
    public VerificacionArtesanoDTO obtenerVerificacionPorCodigo(String codigoVerificacion) {
        log.info("Obteniendo verificación para código: {}", codigoVerificacion);

        // Extraer ID de artesanía del código (formato: ART-{ID}-{timestamp}-{uuid})
        Long idArtesania = extraerIdDeCodigoVerificacion(codigoVerificacion);

        if (idArtesania == null) {
            throw new IllegalArgumentException("Código de verificación inválido: " + codigoVerificacion);
        }

        // Buscar artesanía
        ServicioArtesania artesania = artesaniaRepository.findById(idArtesania)
                .orElseThrow(() -> new EntityNotFoundException("Artesanía no encontrada"));

        // Buscar transacción blockchain
        Optional<TransaccionBlockchainArtesania> transaccionOpt =
                transaccionRepository.findUltimaTransaccionConfirmada(idArtesania);

        // Verificar estado en blockchain
        boolean verificadoEnBlockchain = false;
        String estadoVerificacion = "NO_VERIFICADO";
        String mensajeVerificacion = "Esta artesanía aún no ha sido registrada en blockchain";
        LocalDateTime fechaRegistro = null;

        if (artesania.getHashBlockchain() != null) {
            try {
                verificadoEnBlockchain = blockchainService.verificarTransaccion(artesania.getHashBlockchain());
                estadoVerificacion = verificadoEnBlockchain ? "AUTENTICO" : "PENDIENTE";
                mensajeVerificacion = verificadoEnBlockchain
                        ? "✅ Artesanía verificada y registrada en blockchain de forma inmutable"
                        : "⏳ Transacción blockchain pendiente de confirmación";

                if (transaccionOpt.isPresent()) {
                    fechaRegistro = transaccionOpt.get().getFechaRegistro();
                }
            } catch (Exception e) {
                log.error("Error al verificar transacción blockchain: {}", artesania.getHashBlockchain(), e);
                estadoVerificacion = "ERROR_VERIFICACION";
                mensajeVerificacion = "Error al verificar en blockchain. Contacte al administrador.";
            }
        }

        // Construir DTO de verificación
        return VerificacionArtesanoDTO.builder()
                // Información del artesano
                .artesano(artesania.getArtesano())
                .tipoArtesania(artesania.getTipoArtesania())
                .origenCultural(artesania.getOrigenCultural())
                .descripcionArtesania(artesania.getArtesania())

                // Detalles del taller
                .nivelDificultad(artesania.getNivelDificultad())
                .duracionTaller(artesania.getDuracionTaller())
                .incluyeMaterial(artesania.getIncluyeMaterial())
                .maxParticipantes(artesania.getMaxParticipantes())
                .visitaTaller(artesania.getVisitaTaller())

                // Verificación blockchain
                .hashBlockchain(artesania.getHashBlockchain())
                .urlBlockchainExplorer(artesania.getHashBlockchain() != null
                        ? blockchainService.generarBlockchainExplorerUrl(artesania.getHashBlockchain())
                        : null)
                .fechaRegistroBlockchain(fechaRegistro)
                .verificadoEnBlockchain(verificadoEnBlockchain)

                // Estado de verificación
                .codigoVerificacion(codigoVerificacion)
                .estadoVerificacion(estadoVerificacion)
                .mensajeVerificacion(mensajeVerificacion)

                // URLs
                .urlVerificacionCompleta(generarUrlVerificacion(codigoVerificacion))
                .urlQrOriginal(baseUrl + "/api/qr/" + extraerNombreQrDeArtesania(idArtesania))

                // Metadatos
                .fechaGeneracionQr(LocalDateTime.now())
                .versionSistema(versionSistema)
                .esArtesanoVerificado(verificadoEnBlockchain)
                .build();
    }

    @Override
    public Map<String, Object> verificarEstadoRapido(String codigoVerificacion) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            Long idArtesania = extraerIdDeCodigoVerificacion(codigoVerificacion);
            ServicioArtesania artesania = artesaniaRepository.findById(idArtesania)
                    .orElseThrow(() -> new EntityNotFoundException("Artesanía no encontrada"));

            boolean esValido = artesania.getHashBlockchain() != null;
            boolean verificado = false;

            if (esValido) {
                verificado = blockchainService.verificarTransaccion(artesania.getHashBlockchain());
            }

            resultado.put("valido", esValido);
            resultado.put("verificado", verificado);
            resultado.put("artesano", artesania.getArtesano());
            resultado.put("tipoArtesania", artesania.getTipoArtesania());
            resultado.put("estado", verificado ? "VERIFICADO" : (esValido ? "PENDIENTE" : "NO_REGISTRADO"));
            resultado.put("timestamp", LocalDateTime.now().toString());

        } catch (Exception e) {
            resultado.put("valido", false);
            resultado.put("error", "Código de verificación inválido");
        }

        return resultado;
    }

    @Override
    public String generarUrlVerificacion(String codigoVerificacion) {
        return baseUrl + "/verificar/artesano/" + codigoVerificacion;
    }

    @Override
    public void actualizarEstadoBlockchain(String codigoVerificacion, String hashBlockchain) {
        Long idArtesania = extraerIdDeCodigoVerificacion(codigoVerificacion);
        if (idArtesania != null) {
            ServicioArtesania artesania = artesaniaRepository.findById(idArtesania).orElse(null);
            if (artesania != null) {
                artesania.setHashBlockchain(hashBlockchain);
                artesaniaRepository.save(artesania);
                log.info("Estado blockchain actualizado para código: {} - Hash: {}", codigoVerificacion, hashBlockchain);
            }
        }
    }

    /**
     * Extrae el ID de artesanía del código de verificación
     */
    private Long extraerIdDeCodigoVerificacion(String codigoVerificacion) {
        try {
            if (codigoVerificacion != null && codigoVerificacion.startsWith("ART-")) {
                String[] partes = codigoVerificacion.split("-");
                if (partes.length >= 2) {
                    return Long.parseLong(partes[1]);
                }
            }
        } catch (NumberFormatException e) {
            log.warn("Código de verificación con formato inválido: {}", codigoVerificacion);
        }
        return null;
    }

    /**
     * Genera un nombre de archivo QR basado en el ID de artesanía
     */
    private String extraerNombreQrDeArtesania(Long idArtesania) {
        // Este método debería coordinarse con el servicio QR para usar el mismo patrón
        return "qr_artesania_" + idArtesania + ".png";
    }
}