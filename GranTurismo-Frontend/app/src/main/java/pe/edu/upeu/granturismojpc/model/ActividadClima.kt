package pe.edu.upeu.granturismojpc.model

data class ActividadClimaResp(
    val idActividad: Long,
    val titulo: String,
    val descripcion: String?,
    val tipo: String,
    val duracionHoras: Long,
    val imagenUrl: String?,
    val precioBase: Double
)

// En pe.edu.upeu.granturismojpc.model
data class ActividadesClimaWrapper(
    val clima: ClimaInfo,
    val actividades: List<ActividadClimaResp>
)

data class ClimaInfo(
    val fecha: String,
    val ideal: Boolean,
    val mensaje: String,
    val temperatura: Double
)

data class RecomendacionIAWrapper(
    val mensajeIA: String,
    val actividades: List<ActividadClimaResp>,
    val clima: ClimaInfo
)
