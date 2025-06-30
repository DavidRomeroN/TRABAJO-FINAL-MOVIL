package pe.edu.upeu.granturismojpc.repository

import jakarta.inject.Inject
import pe.edu.upeu.granturismojpc.data.remote.RestTipoServicio
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import pe.edu.upeu.granturismojpc.utils.TokenUtils


interface TipoServicioRepository {
    suspend fun findAll(): List<TipoServicioResp>
}
class TipoServicioRepositoryImp @Inject constructor(
    private val rest: RestTipoServicio,
): TipoServicioRepository{
    override suspend fun findAll(): List<TipoServicioResp> {
        val response =
            rest.reportarTipoServicio(TokenUtils.TOKEN_CONTENT)
        return if (response.isSuccessful) response.body() ?:
        emptyList()
        else emptyList()
    }
}