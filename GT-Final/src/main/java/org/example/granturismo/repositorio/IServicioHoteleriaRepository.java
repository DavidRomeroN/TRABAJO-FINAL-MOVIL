package org.example.granturismo.repositorio;

import org.example.granturismo.modelo.ServicioHoteleria;

public interface IServicioHoteleriaRepository extends ICrudGenericoRepository<ServicioHoteleria, Long>{
    ServicioHoteleria findServicioHoteleriaByServicio_IdServicio(Long servicioIdServicio);
}
