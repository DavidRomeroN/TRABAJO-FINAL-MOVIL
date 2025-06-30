package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI

import pe.edu.upeu.granturismojpc.model.ActividadClimaResp
import pe.edu.upeu.granturismojpc.model.ActividadResp
import pe.edu.upeu.granturismojpc.model.ActividadesClimaWrapper
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.Message
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import pe.edu.upeu.granturismojpc.model.RecomendacionIAWrapper

interface RecomendacionApiService {

    @GET("actividad/recomendadas")
    suspend fun getActividadesPorClima(
        @Query("dia") dia: Int
    ): ActividadesClimaWrapper

    @GET("api/recomendacion/destinos")
     suspend fun getDestinosPorClima(@Query("dia") dia: Int): List<DestinoResp>

     @GET("api/recomendacion/mensajeIA")
      suspend fun getMensajeIA(@Query("dia") dia: Int): Message

      @GET("actividad/asesor")
     suspend fun getRecomendacionAsesor(@Query("dia") dia: Int): RecomendacionIAWrapper

}
