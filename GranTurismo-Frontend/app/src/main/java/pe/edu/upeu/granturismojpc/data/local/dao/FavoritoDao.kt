package pe.edu.upeu.granturismojpc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.granturismojpc.model.Favorito

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEntidad(to: Favorito)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEntidadRegs(to: List<Favorito>)
    @Update
    suspend fun modificarEntidad(to: Favorito)
    @Delete
    suspend fun eliminarEntidad(to: Favorito)
    @Query("select * from favorito_paquete")
    fun reportatEntidad(): Flow<List<Favorito>>
    @Query("select * from favorito_paquete where id_favorito=:idx")
    fun buscarEntidad(idx: Long): Flow<Favorito?>
    @Query("SELECT * FROM favorito_paquete WHERE tipo = :tipo")
    suspend fun obtenerFavoritosPorTipo(tipo: String): List<Favorito>
}