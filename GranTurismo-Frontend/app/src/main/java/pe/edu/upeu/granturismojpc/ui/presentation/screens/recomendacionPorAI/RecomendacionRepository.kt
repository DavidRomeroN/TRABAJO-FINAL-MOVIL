package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI



import pe.edu.upeu.granturismojpc.model.ActividadClimaResp
import pe.edu.upeu.granturismojpc.model.ActividadesClimaWrapper
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.Message
import javax.inject.Inject
import pe.edu.upeu.granturismojpc.model.RecomendacionIAWrapper


interface RecomendacionRepository {
    suspend fun obtenerRecomendadasPorClima(dia: Int): ActividadesClimaWrapper
    suspend fun obtenerRecomendacionAsesor(dia: Int): RecomendacionIAWrapper
}


