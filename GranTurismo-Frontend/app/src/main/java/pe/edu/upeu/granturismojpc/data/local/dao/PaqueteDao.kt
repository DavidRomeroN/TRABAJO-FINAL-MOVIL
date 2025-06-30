package pe.edu.upeu.granturismojpc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.granturismojpc.model.PaqueteEntity

@Dao
interface PaqueteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEntidad(to: PaqueteEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEntidadRegs(to: List<PaqueteEntity>)
    @Update
    suspend fun modificarEntidad(to: PaqueteEntity)
    @Delete
    suspend fun eliminarEntidad(to: PaqueteEntity)
    @Query("select * from paquetes")
    fun reportatEntidad(): Flow<List<PaqueteEntity>>
    @Query("select * from paquetes where idPaquete=:idx")
    fun buscarEntidad(idx: Long): PaqueteEntity?
    @Query("DELETE FROM paquetes")
    suspend fun deleteAll()
    @Query("DELETE FROM paquetes WHERE idPaquete NOT IN (:ids)")
    suspend fun eliminarPaquetesQueNoEstanEn(ids: List<Long>)
    @Query("SELECT idPaquete FROM paquetes")
    suspend fun getAllIds(): List<Long>
    @Transaction
    suspend fun reemplazarTodo(paquetes: List<PaqueteEntity>) {
        insertarEntidadRegs(paquetes)
        val ids = paquetes.map { it.idPaquete }
        if (ids.isNotEmpty()) {
            eliminarPaquetesQueNoEstanEn(ids)
        } else {
            deleteAll()
        }
    }
}