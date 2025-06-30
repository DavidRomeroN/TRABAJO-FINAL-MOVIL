package org.example.granturismo.servicio;

import java.util.Map;

public interface IClimaServicio {
    String obtenerResumenClimaParaDestino(double latitud, double longitud);
    Map<String, Object> obtenerClimaDeDia(double lat, double lon, int dia);
}
