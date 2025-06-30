package org.example.granturismo.control;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.granturismo.dtos.QrCodeResponseDTO;
import org.example.granturismo.dtos.ServicioArtesaniaDTO;
import org.example.granturismo.dtos.ServicioHoteleriaDTO;
import org.example.granturismo.excepciones.BlockchainException;
import org.example.granturismo.excepciones.QrGenerationException;
import org.example.granturismo.mappers.ServicioArtesaniaMapper;
import org.example.granturismo.modelo.ServicioArtesania;
import org.example.granturismo.modelo.ServicioHoteleria;
import org.example.granturismo.security.PermitRoles;
import org.example.granturismo.servicio.IServicioArtesaniaService;
import org.example.granturismo.servicio.impl.ServicioArtesaniaServiceImp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/servicioartesania")

public class ServicioArtesaniaController {

    private final IServicioArtesaniaService servicioArtesaniaService;
    private final ServicioArtesaniaMapper servicioArtesaniaMapper;

    @GetMapping
    @PermitRoles({"ADMIN", "USER", "PROV"})
    public ResponseEntity<List<ServicioArtesaniaDTO>> findAll() {
        List<ServicioArtesaniaDTO> list = servicioArtesaniaMapper.toDTOs(servicioArtesaniaService.findAll());
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{id}")
    @PermitRoles({"ADMIN", "USER", "PROV"})
    public ResponseEntity<ServicioArtesaniaDTO> findById(@PathVariable("id") Long id) {
        ServicioArtesania obj = servicioArtesaniaService.findById(id);
        return ResponseEntity.ok(servicioArtesaniaMapper.toDTO(obj));
    }

    @GetMapping("/servicio/{id}")
    @PermitRoles({"ADMIN", "USER", "PROV"})
    public ResponseEntity<ServicioArtesaniaDTO> findByServicio(
            @PathVariable("id") Long servicioId
    ) {
        ServicioArtesania art = servicioArtesaniaService.findByServicio(servicioId);
        if (art == null) {
            return ResponseEntity.notFound().build();
        }
        ServicioArtesaniaDTO dto = servicioArtesaniaMapper.toDTO(art);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PermitRoles({"ADMIN"})
    public ResponseEntity<Void> save(@Valid @RequestBody ServicioArtesaniaDTO.ServicioArtesaniaCADTO dto) {
        ServicioArtesaniaDTO obj = servicioArtesaniaService.saveD(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdArtesania()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PermitRoles({"ADMIN","PROV"})
    public ResponseEntity<ServicioArtesaniaDTO> update(@Valid @RequestBody ServicioArtesaniaDTO.ServicioArtesaniaCADTO dto, @PathVariable("id") Long id) {
        ServicioArtesaniaDTO obj = servicioArtesaniaService.updateD(dto, id);
        return ResponseEntity.ok(obj);
    }

    @DeleteMapping("/{id}")
    @PermitRoles({"ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        servicioArtesaniaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pageable")
    @PermitRoles({"ADMIN", "USER", "PROV"})
    public ResponseEntity<org.springframework.data.domain.Page<ServicioArtesaniaDTO>> listPage(Pageable pageable){
        Page<ServicioArtesaniaDTO> page = servicioArtesaniaService.listaPage(pageable).map(e -> servicioArtesaniaMapper.toDTO(e));
        return ResponseEntity.ok(page);
    }

    // =================== NUEVOS ENDPOINTS BLOCKCHAIN Y QR ===================

    @PostMapping("/{idArtesania}/generar-qr-blockchain")
    @Operation(summary = "Generar QR y registrar en blockchain",
            description = "Genera código QR y registra la artesanía en blockchain para transparencia y autenticidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR y blockchain generados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Artesanía no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<QrCodeResponseDTO> generarQrYBlockchain(
            @Parameter(description = "ID de la artesanía") @PathVariable Long idArtesania) {

        log.info("Solicitud para generar QR y blockchain para artesanía ID: {}", idArtesania);

        try {
            QrCodeResponseDTO response = servicioArtesaniaService.generarQrYBlockchainParaArtesania(idArtesania);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Artesanía no encontrada: {}", idArtesania, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{idArtesania}/verificar-blockchain")
    @Operation(summary = "Verificar estado en blockchain",
            description = "Verifica el estado de la transacción blockchain para una artesanía")
    public ResponseEntity<Map<String, String>> verificarEstadoBlockchain(
            @Parameter(description = "ID de la artesanía") @PathVariable Long idArtesania) {

        log.info("Verificando estado blockchain para artesanía ID: {}", idArtesania);

        try {
            String estado = servicioArtesaniaService.verificarEstadoBlockchain(idArtesania);
            Map<String, String> response = new HashMap<>();
            response.put("idArtesania", idArtesania.toString());
            response.put("estado", estado);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Artesanía no encontrada: {}", idArtesania, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{idArtesania}/regenerar-qr")
    @Operation(summary = "Regenerar código QR",
            description = "Regenera el código QR para una artesanía ya registrada en blockchain")
    public ResponseEntity<Map<String, String>> regenerarQr(
            @Parameter(description = "ID de la artesanía") @PathVariable Long idArtesania) {

        log.info("Regenerando QR para artesanía ID: {}", idArtesania);

        try {
            String qrUrl = servicioArtesaniaService.regenerarQr(idArtesania);
            Map<String, String> response = new HashMap<>();
            response.put("idArtesania", idArtesania.toString());
            response.put("qrImageUrl", qrUrl);
            response.put("mensaje", "QR regenerado exitosamente");

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Artesanía no encontrada: {}", idArtesania, e);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Estado inválido para regenerar QR: {}", idArtesania, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // =================== MANEJADORES DE EXCEPCIONES ===================

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        log.error("Entidad no encontrada", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Recurso no encontrado");
        error.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BlockchainException.class)
    public ResponseEntity<Map<String, String>> handleBlockchainException(BlockchainException e) {
        log.error("Error en blockchain", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error en blockchain");
        error.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(QrGenerationException.class)
    public ResponseEntity<Map<String, String>> handleQrGenerationException(QrGenerationException e) {
        log.error("Error al generar QR", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error al generar código QR");
        error.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        log.error("Estado inválido", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Estado inválido");
        error.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Error interno del servidor", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("mensaje", "Ocurrió un error inesperado. Por favor, contacte al administrador.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
