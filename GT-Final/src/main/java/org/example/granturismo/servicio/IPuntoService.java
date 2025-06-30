package org.example.granturismo.servicio;

import org.example.granturismo.dtos.CanjePuntosRequestDTO;
import org.example.granturismo.dtos.PuntoDTO;
import org.example.granturismo.dtos.PuntosDisponiblesResponseDTO;
import org.example.granturismo.modelo.TipoTransaccion;

import java.util.List;

public interface IPuntoService {

    PuntosDisponiblesResponseDTO getPuntosDisponibles(Long uidUsuario);

    List<PuntoDTO> getHistorialPuntos(Long uidUsuario);

    PuntoDTO otorgarPuntos(Long uidUsuario, TipoTransaccion tipo, int cantidad, Long referenciaId, String descripcion);

    PuntoDTO canjearPuntos(Long uidUsuario, CanjePuntosRequestDTO requestDTO);
}