package org.example.granturismo.servicio;


import org.example.granturismo.dtos.QrCodeResponseDTO;
import org.example.granturismo.dtos.ServicioArtesaniaDTO;
import org.example.granturismo.modelo.ServicioArtesania;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServicioArtesaniaService extends ICrudGenericoService<ServicioArtesania, Long> {

    ServicioArtesaniaDTO saveD(ServicioArtesaniaDTO.ServicioArtesaniaCADTO dto);

    ServicioArtesaniaDTO updateD(ServicioArtesaniaDTO.ServicioArtesaniaCADTO dto, Long id);

    Page<ServicioArtesania> listaPage(Pageable pageable);

    /**
     * Genera QR y registra en blockchain para una artesanía específica
     * @param idArtesania ID de la artesanía
     * @return Respuesta con URLs del QR y explorador blockchain
     */
    QrCodeResponseDTO generarQrYBlockchainParaArtesania(Long idArtesania);

    /**
     * Verifica el estado de una transacción blockchain
     * @param idArtesania ID de la artesanía
     * @return Estado de la transacción
     */
    String verificarEstadoBlockchain(Long idArtesania);

    /**
     * Regenera el QR para una artesanía que ya tiene registro en blockchain
     * @param idArtesania ID de la artesanía
     * @return Nueva URL del QR
     */
    String regenerarQr(Long idArtesania);

    ServicioArtesania findByServicio(Long id);

}
