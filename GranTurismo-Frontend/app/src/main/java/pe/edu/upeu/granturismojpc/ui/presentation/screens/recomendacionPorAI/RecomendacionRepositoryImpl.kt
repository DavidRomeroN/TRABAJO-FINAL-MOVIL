package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI

import pe.edu.upeu.granturismojpc.model.ActividadClimaResp
import pe.edu.upeu.granturismojpc.model.ActividadesClimaWrapper
import pe.edu.upeu.granturismojpc.model.RecomendacionIAWrapper
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

class RecomendacionRepositoryImpl @Inject constructor(
    private val api: RecomendacionApiService
) : RecomendacionRepository {

    override suspend fun obtenerRecomendadasPorClima(dia: Int): ActividadesClimaWrapper {
        return api.getActividadesPorClima(dia)
    }

    override suspend fun obtenerRecomendacionAsesor(dia: Int): RecomendacionIAWrapper {
        return api.getRecomendacionAsesor(dia)
    }



}
