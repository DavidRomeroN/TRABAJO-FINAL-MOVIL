package pe.edu.upeu.granturismojpc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pe.edu.upeu.granturismojpc.data.local.dao.FavoritoDao
import pe.edu.upeu.granturismojpc.data.local.dao.PaqueteDao
import pe.edu.upeu.granturismojpc.model.Favorito
import pe.edu.upeu.granturismojpc.model.PaqueteEntity

@Database(entities = [Favorito::class, PaqueteEntity::class], version = 6)
abstract class DbDataSource: RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao
    abstract fun paqueteDao(): PaqueteDao
}