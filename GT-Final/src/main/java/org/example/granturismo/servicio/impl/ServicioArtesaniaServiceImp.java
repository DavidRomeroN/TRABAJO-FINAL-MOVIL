package org.example.granturismo.servicio.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.ArtesaniaBlockchainDTO;
import org.example.granturismo.dtos.QrCodeResponseDTO;
import org.example.granturismo.dtos.ServicioArtesaniaDTO;
import org.example.granturismo.excepciones.BlockchainException;
import org.example.granturismo.excepciones.QrGenerationException;
import org.example.granturismo.mappers.ArtesaniaBlockchainMapper;
import org.example.granturismo.mappers.ServicioArtesaniaMapper;
import org.example.granturismo.modelo.Servicio;
import org.example.granturismo.modelo.ServicioArtesania;
import org.example.granturismo.modelo.TransaccionBlockchainArtesania;
import org.example.granturismo.repositorio.ICrudGenericoRepository;
import org.example.granturismo.repositorio.IServicioArtesaniaRepository;
import org.example.granturismo.repositorio.IServicioRepository;
import org.example.granturismo.repositorio.ITransaccionBlockchainArtesaniaRepository;
import org.example.granturismo.servicio.IBlockchainService;
import org.example.granturismo.servicio.IQrCodeService;
import org.example.granturismo.servicio.IServicioArtesaniaService;
import org.example.granturismo.servicio.IVerificacionArtesanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ServicioArtesaniaServiceImp extends CrudGenericoServiceImp<ServicioArtesania, Long> implements IServicioArtesaniaService {

    @Autowired
    private DataSource dataSource;

    private final IServicioArtesaniaRepository repo;
    private final ServicioArtesaniaMapper servicioArtesaniaMapper;
    private final IServicioRepository servicioRepository;

    // Nuevas dependencias para blockchain y QR
    private final IBlockchainService blockchainService;
    private final IQrCodeService qrCodeService;
    private final ArtesaniaBlockchainMapper artesaniaBlockchainMapper;
    private final ITransaccionBlockchainArtesaniaRepository transaccionBlockchainRepository;
    private final IVerificacionArtesanoService verificacionArtesanoService;

    @Override
    protected ICrudGenericoRepository<ServicioArtesania, Long> getRepo() {
        return repo;
    }

    @Override
    public ServicioArtesaniaDTO saveD(ServicioArtesaniaDTO.ServicioArtesaniaCADTO dto) {
        ServicioArtesania servicioArtesania = servicioArtesaniaMapper.toEntityFromCADTO(dto);

        Servicio servicio = servicioRepository.findById(dto.servicio())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        servicioArtesania.setServicio(servicio);

        ServicioArtesania servicioartesaniaGuardado = repo.save(servicioArtesania);
        return servicioArtesaniaMapper.toDTO(servicioartesaniaGuardado);
    }

    @Override
    public ServicioArtesaniaDTO updateD(ServicioArtesaniaDTO.ServicioArtesaniaCADTO dto, Long id) {
        ServicioArtesania servicioArtesania = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio de Artesania no encontrado"));

        Servicio servicio = servicioRepository.findById(dto.servicio())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        ServicioArtesania servicioArtesaniaActualizado = servicioArtesaniaMapper.toEntityFromCADTO(dto);
        servicioArtesaniaActualizado.setIdArtesania(servicioArtesania.getIdArtesania());
        servicioArtesaniaActualizado.setServicio(servicio);

        ServicioArtesania servicioArtesaniaGuardado = repo.save(servicioArtesaniaActualizado);
        return servicioArtesaniaMapper.toDTO(servicioArtesaniaGuardado);
    }

    public Page<ServicioArtesania> listaPage(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional
    public QrCodeResponseDTO generarQrYBlockchainParaArtesania(Long idArtesania) {
        log.info("Iniciando generación de QR y blockchain para artesanía ID: {}", idArtesania);

        try {
            // 1. Buscar la artesanía
            ServicioArtesania servicioArtesania = repo.findById(idArtesania)
                    .orElseThrow(() -> new EntityNotFoundException("Artesanía no encontrada con ID: " + idArtesania));

            // 2. Generar código de verificación único
            String codigoVerificacion = verificacionArtesanoService.generarCodigoVerificacion(idArtesania);
            log.info("Código de verificación generado: {}", codigoVerificacion);

            // 3. Verificar si ya tiene hash blockchain
            if (servicioArtesania.getHashBlockchain() != null && !servicioArtesania.getHashBlockchain().trim().isEmpty()) {
                log.info("La artesanía ID: {} ya tiene registro en blockchain: {}", idArtesania, servicioArtesania.getHashBlockchain());

                // Regenerar QR con información de verificación
                String urlVerificacion = verificacionArtesanoService.generarUrlVerificacion(codigoVerificacion);
                String qrImageUrl = qrCodeService.generarQrCodeUrl(urlVerificacion);

                return QrCodeResponseDTO.builder()
                        .qrImageUrl(qrImageUrl)
                        .blockchainExplorerUrl(blockchainService.generarBlockchainExplorerUrl(servicioArtesania.getHashBlockchain()))
                        .hashBlockchain(servicioArtesania.getHashBlockchain())
                        .mensaje("QR de verificación regenerado - Código: " + codigoVerificacion)
                        .build();
            }

            // 4. Mapear a DTO para blockchain
            ArtesaniaBlockchainDTO artesaniaBlockchainDTO = artesaniaBlockchainMapper.toBlockchainDTO(servicioArtesania);

            // 5. Registrar en blockchain
            String hashBlockchain = blockchainService.registrarArtesaniaEnBlockchain(artesaniaBlockchainDTO);
            log.info("Artesanía registrada en blockchain con hash: {}", hashBlockchain);

            // 6. Actualizar la entidad con el hash
            servicioArtesania.setHashBlockchain(hashBlockchain);
            repo.save(servicioArtesania);

            // 7. Actualizar estado blockchain en servicio de verificación
            verificacionArtesanoService.actualizarEstadoBlockchain(codigoVerificacion, hashBlockchain);

            // 8. Generar URL de verificación (esto es lo que va en el QR)
            String urlVerificacion = verificacionArtesanoService.generarUrlVerificacion(codigoVerificacion);

            // 9. Generar código QR con la URL de verificación
            String qrImageUrl = qrCodeService.generarQrCodeUrl(urlVerificacion, 300, 300);
            log.info("Código QR de verificación generado: {}", qrImageUrl);

            // 10. Registrar transacción para auditoría
            String blockchainExplorerUrl = blockchainService.generarBlockchainExplorerUrl(hashBlockchain);
            registrarTransaccionAuditoria(idArtesania, hashBlockchain, blockchainExplorerUrl);

            // 11. Retornar respuesta
            return QrCodeResponseDTO.builder()
                    .qrImageUrl(qrImageUrl)
                    .blockchainExplorerUrl(blockchainExplorerUrl)
                    .hashBlockchain(hashBlockchain)
                    .mensaje("QR de verificación generado exitosamente - Código: " + codigoVerificacion)
                    .build();

        } catch (BlockchainException e) {
            log.error("Error en blockchain para artesanía ID: {}", idArtesania, e);
            throw new RuntimeException("Error al registrar en blockchain: " + e.getMessage(), e);
        } catch (QrGenerationException e) {
            log.error("Error al generar QR para artesanía ID: {}", idArtesania, e);
            throw new RuntimeException("Error al generar código QR: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error general para artesanía ID: {}", idArtesania, e);
            throw new RuntimeException("Error al procesar artesanía: " + e.getMessage(), e);
        }
    }

    @Override
    public String verificarEstadoBlockchain(Long idArtesania) {
        log.info("Verificando estado blockchain para artesanía ID: {}", idArtesania);

        ServicioArtesania servicioArtesania = repo.findById(idArtesania)
                .orElseThrow(() -> new EntityNotFoundException("Artesanía no encontrada con ID: " + idArtesania));

        if (servicioArtesania.getHashBlockchain() == null || servicioArtesania.getHashBlockchain().trim().isEmpty()) {
            return "Sin registro en blockchain";
        }

        try {
            boolean confirmado = blockchainService.verificarTransaccion(servicioArtesania.getHashBlockchain());
            return confirmado ? "Confirmado" : "Pendiente";
        } catch (BlockchainException e) {
            log.error("Error al verificar estado blockchain para artesanía ID: {}", idArtesania, e);
            return "Error al verificar";
        }
    }

    @Override
    public String regenerarQr(Long idArtesania) {
        log.info("Regenerando QR para artesanía ID: {}", idArtesania);

        ServicioArtesania servicioArtesania = repo.findById(idArtesania)
                .orElseThrow(() -> new EntityNotFoundException("Artesanía no encontrada con ID: " + idArtesania));

        if (servicioArtesania.getHashBlockchain() == null || servicioArtesania.getHashBlockchain().trim().isEmpty()) {
            throw new IllegalStateException("La artesanía no tiene registro en blockchain. Genere primero el registro completo.");
        }

        try {
            String blockchainExplorerUrl = blockchainService.generarBlockchainExplorerUrl(servicioArtesania.getHashBlockchain());
            return qrCodeService.generarQrCodeUrl(blockchainExplorerUrl);
        } catch (QrGenerationException e) {
            log.error("Error al regenerar QR para artesanía ID: {}", idArtesania, e);
            throw new RuntimeException("Error al regenerar código QR: " + e.getMessage(), e);
        }
    }

    /**
     * Registra la transacción en la tabla de auditoría
     */
    private void registrarTransaccionAuditoria(Long idArtesania, String hashTransaccion, String urlExplorer) {
        try {
            TransaccionBlockchainArtesania transaccion = TransaccionBlockchainArtesania.builder()
                    .idArtesania(idArtesania)
                    .hashTransaccion(hashTransaccion)
                    .urlExplorer(urlExplorer)
                    .estado("CONFIRMADO") // Asumimos confirmado por simplicidad
                    .build();

            transaccionBlockchainRepository.save(transaccion);
            log.info("Transacción de auditoría registrada para artesanía ID: {}", idArtesania);
        } catch (Exception e) {
            log.error("Error al registrar auditoría para artesanía ID: {}", idArtesania, e);
            // No lanzamos excepción para no afectar el flujo principal
        }
    }

    @Override
    public ServicioArtesania findByServicio(Long servicioId) {
        return repo.findServicioArtesaniaByServicio_IdServicio(servicioId);
    }
}