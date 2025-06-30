package org.example.granturismo.servicio.impl;

import lombok.RequiredArgsConstructor;
import org.example.granturismo.dtos.CanjePuntosRequestDTO;
import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.dtos.PuntosDisponiblesResponseDTO;
import org.example.granturismo.excepciones.SaldoInsuficienteException;
import org.example.granturismo.mappers.PuntoMapper;
import org.example.granturismo.modelo.Punto;
import org.example.granturismo.modelo.TipoTransaccion;
import org.example.granturismo.repositorio.IPuntoRepository;
import org.example.granturismo.servicio.IPuntoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PuntoServiceImp implements IPuntoService {

    private final IPuntoRepository puntoRepository;
    private final PuntoMapper puntoMapper;

    @Value("${app.puntos.registro:100}")
    private int puntosRegistro;

    @Value("${app.puntos.reserva-confirmada:50}")
    private int puntosReservaConfirmada;

    @Value("${app.puntos.compartir-app:20}")
    private int puntosCompartirApp;

    @Override
    public PuntosDisponiblesResponseDTO getPuntosDisponibles(Long uidUsuario) {
        Integer puntosDisponibles = puntoRepository.sumCantidadPuntosByUidUsuario(uidUsuario);
        return new PuntosDisponiblesResponseDTO(puntosDisponibles);
    }

    @Override
    public List<PuntoDTO> getHistorialPuntos(Long uidUsuario) {
        List<Punto> puntos = puntoRepository.findByUidUsuario(uidUsuario);
        return puntoMapper.toDTOList(puntos);
    }

    @Override
    @Transactional
    public PuntoDTO otorgarPuntos(Long uidUsuario, TipoTransaccion tipo, int cantidad, Long referenciaId, String descripcion) {
        Punto punto = new Punto();
        punto.setUidUsuario(uidUsuario);
        punto.setCantidadPuntos(cantidad);
        punto.setTipoTransaccion(tipo);
        punto.setReferenciaId(referenciaId);
        punto.setDescripcion(descripcion);

        Punto puntoGuardado = puntoRepository.save(punto);
        return puntoMapper.toDTO(puntoGuardado);
    }

    @Override
    @Transactional
    public PuntoDTO canjearPuntos(Long uidUsuario, CanjePuntosRequestDTO requestDTO) {
        // Verificar saldo actual
        Integer saldoActual = puntoRepository.sumCantidadPuntosByUidUsuario(uidUsuario);

        if (saldoActual < requestDTO.cantidad()) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo actual: " + saldoActual + ", Cantidad solicitada: " + requestDTO.cantidad());
        }

        // Crear transacción de canje (cantidad negativa)
        Punto punto = new Punto();
        punto.setUidUsuario(uidUsuario);
        punto.setCantidadPuntos(-requestDTO.cantidad());
        punto.setTipoTransaccion(TipoTransaccion.CANJE);
        punto.setDescripcion(requestDTO.descripcion());

        Punto puntoGuardado = puntoRepository.save(punto);
        return puntoMapper.toDTO(puntoGuardado);
    }
}