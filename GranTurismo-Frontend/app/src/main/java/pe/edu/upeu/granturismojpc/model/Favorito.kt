package pe.edu.upeu.granturismojpc.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorito_paquete")
data class Favorito(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_favorito")
    var idFavorito: Long = 0L,
    var usuario: Long,
    @ColumnInfo(name = "referencia_id")
    var referenciaId: Long,
    var tipo: String        //paquete, actividad, favoritoAlimentacion, favoritoHoteleria, favoritoArtesania
)

data class FavoritoDto(
    var idFavorito: Long,
    var usuario: Long,
    var referenciaId: Long,
    var tipo: String,
)

data class FavoritoCreateDto(
    var usuario: Long,
    var referenciaId: Long,
    var tipo: String,
)

data class FavoritoResp(
    var idFavorito: Long,
    var usuario: UsuarioRespSinToken,
    var referenciaId: Long,
    var tipo: String,
)

fun FavoritoResp.toDto(): FavoritoDto {
    return FavoritoDto(
        idFavorito = this.idFavorito,
        usuario = this.usuario.idUsuario,
        referenciaId = this.referenciaId,
        tipo = this.tipo,
    )
}

fun FavoritoDto.toCreateDto(): FavoritoCreateDto {
    return FavoritoCreateDto(
        usuario = this.usuario,
        referenciaId = this.referenciaId,
        tipo = this.tipo,
    )
}
fun FavoritoDto.toRoom(): Favorito {
    return Favorito(
        idFavorito = idFavorito,
        usuario = usuario,
        referenciaId = referenciaId,
        tipo = tipo
    )
}
fun Favorito.toDto(): FavoritoDto {
    return FavoritoDto(
        idFavorito = idFavorito,
        usuario = usuario,
        referenciaId = referenciaId,
        tipo = tipo
    )
}
fun FavoritoResp.toRoom(): Favorito {
    return Favorito(
        idFavorito = idFavorito,
        usuario = usuario.idUsuario,
        referenciaId = referenciaId,
        tipo = tipo
    )
}
fun FavoritoCreateDto.toRoom(): Favorito {
    return Favorito(
        idFavorito = 0,
        usuario = usuario,
        referenciaId = referenciaId,
        tipo = tipo
    )
}