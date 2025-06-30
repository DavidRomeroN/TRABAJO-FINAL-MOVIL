package org.example.granturismo.modelo;

public enum TipoActividad {
    CULTURAL,
    ENTRETENIMIENTO,
    RELAJANTE,
    AVENTURA,
    GASTRONOMICO,
    ACUATICO,
    FOTOGRAFIA,
    RELIGIOSO;

    public static boolean esValido(String tipo) {
        if (tipo == null) return false;
        try {
            TipoActividad.valueOf(tipo.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
