package pe.edu.upeu.granturismojpc.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import pe.edu.upeu.granturismojpc.repository.DestinoRepository
import pe.edu.upeu.granturismojpc.repository.ProveedorRepository
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "paquetes")
data class PaqueteEntity(
    @PrimaryKey val idPaquete: Long,
    val titulo: String,
    val descripcion: String?,
    val precioTotal: Double,
    val estado: String?,
    val duracionDias: Int?,
    val localidad: String,
    val tipoActividad: String,
    val cuposMaximos: Int?,
    val fechaInicio: String,
    val fechaFin: String,
    val proveedorNombre: String,
    val destinoNombre: String,
    val imagenUrl: String?,
    val monedaOriginal: String?,
    val idiomaOriginal: String?
){
    fun String.toFechaList(): List<Int> {
        // Espera formato "yyyy-MM-dd HH:mm:ss"
        return try {
            val parts = this.split(" ", ":", "-") // ["2025", "09", "15", "04", "00", "00"]
            parts.take(5).map { it.toInt() }
        } catch (e: Exception) {
            emptyList() // o listOf(0, 0, 0, 0, 0)
        }
    }
}

data class PaqueteDto(
    var idPaquete: Long,
    var titulo: String,
    var descripcion: String?,
    var precioTotal: Double,
    var estado: String?,
    var duracionDias: Int?,
    var localidad: String,
    var tipoActividad: String,
    var cuposMaximos: Int?,
    var proveedor: Long?,
    var fechaInicio: String,
    var fechaFin: String,
    var destino: Long?,
    var monedaOriginal: String? = null,
    var idiomaOriginal: String? = null
)

data class PaqueteResp(
    val idPaquete: Long,
    val titulo: String,
    val descripcion: String?,
    val precioTotal: BigDecimal,
    val estado: String?,
    val duracionDias: Int?,
    val localidad: String,
    val tipoActividad: String,
    val cuposMaximos: Int?,
    val fechaInicio: List<Int>,
    val fechaFin: List<Int>,
    val proveedorNombre: String,
    val destinoNombre: String,
    val imagenUrl: String?,
    var monedaOriginal: String? = null,
    var idiomaOriginal: String? = null
)

data class PaqueteRespBuscar(
    val idPaquete: Long,
    val titulo: String,
    val descripcion: String?,
    val precioTotal: BigDecimal,
    val estado: String?,
    val duracionDias: Int?,
    val localidad: String,
    val tipoActividad: String,
    val cuposMaximos: Int?,
    val fechaInicio: String,
    val fechaFin: String,
    val proveedor: ProveedorResp,
    val destino: DestinoResp,
    val imagenUrl: String?,
    var monedaOriginal: String? = null,
    var idiomaOriginal: String? = null
)

data class PaqueteResponse(
    val data: PaqueteRespBuscar,
    val appliedLanguage: String,
    val appliedCurrency: String,
    val wasTranslated: Boolean,
    val wasCurrencyConverted: Boolean,
    val exchangeRate: Double
)

data class PaqueteCreateDto(
    var titulo: String,
    var descripcion: String?,
    var precioTotal: Double,
    var estado: String?,
    var duracionDias: Int?,
    var localidad: String,
    var tipoActividad: String,
    var cuposMaximos: Int?,
    var proveedor: Long?,
    var fechaInicio: String,
    var fechaFin: String,
    var destino: Long?,
    var monedaOriginal: String? = null,
    var idiomaOriginal: String? = null
)
fun PaqueteResp.toDto(): PaqueteDto {
    return PaqueteDto(
        idPaquete = this.idPaquete,
        titulo = this.titulo,
        descripcion = this.descripcion,
        precioTotal = this.precioTotal.toDouble(),
        estado = this.estado,
        duracionDias = this.duracionDias,
        localidad = this.localidad,
        tipoActividad = this.tipoActividad,
        cuposMaximos = this.cuposMaximos,
        proveedor = 0L,
        fechaInicio = formatFecha(this.fechaInicio),
        fechaFin = formatFecha(this.fechaFin),
        destino = 0L,
        monedaOriginal=this.monedaOriginal,
        idiomaOriginal=this.idiomaOriginal
    )
}

fun PaqueteRespBuscar.toDto(): PaqueteDto {
    return PaqueteDto(
        idPaquete = this.idPaquete,
        titulo = this.titulo,
        descripcion = this.descripcion,
        precioTotal = this.precioTotal.toDouble(),
        estado = this.estado,
        duracionDias = this.duracionDias,
        localidad = this.localidad,
        tipoActividad = this.tipoActividad,
        cuposMaximos = this.cuposMaximos,
        proveedor = this.proveedor.idProveedor,
        fechaInicio = this.fechaInicio,
        fechaFin = this.fechaFin,
        destino = this.destino.idDestino,
        monedaOriginal=this.monedaOriginal,
        idiomaOriginal=this.idiomaOriginal
    )
}

fun PaqueteDto.toCreateDto(): PaqueteCreateDto {
    return PaqueteCreateDto(
        titulo = this.titulo,
        descripcion = this.descripcion,
        precioTotal = this.precioTotal,
        estado = this.estado,
        duracionDias = this.duracionDias,
        localidad = this.localidad,
        tipoActividad = this.tipoActividad,
        cuposMaximos = this.cuposMaximos,
        proveedor = this.proveedor,
        fechaInicio = this.fechaInicio,
        fechaFin = this.fechaFin,
        destino = this.destino,
        monedaOriginal=this.monedaOriginal,
        idiomaOriginal=this.idiomaOriginal
    )
}

fun PaqueteResp.toEntity(
    proveedor: ProveedorEmbeddable,
    destino: DestinoEmbeddable
): PaqueteEntity {
    return PaqueteEntity(
        idPaquete = idPaquete,
        titulo = titulo,
        descripcion = descripcion ?: "",
        precioTotal = precioTotal.toDouble(),
        estado = estado,
        duracionDias = duracionDias ?: 1,
        localidad = localidad,
        tipoActividad = tipoActividad,
        cuposMaximos = cuposMaximos ?: 0,
        proveedorNombre = proveedor.nombreCompleto,
        fechaInicio = formatFecha(fechaInicio),
        fechaFin = formatFecha(fechaFin),
        destinoNombre = destino.nombre,
        imagenUrl = imagenUrl ?: "",
        monedaOriginal = monedaOriginal ?: "USD",
        idiomaOriginal = idiomaOriginal ?: "es"
    )
}

fun PaqueteEntity.toResp(): PaqueteResp = PaqueteResp(
    idPaquete = idPaquete,
    titulo = titulo,
    descripcion = descripcion,
    precioTotal = BigDecimal(precioTotal),
    estado = estado,
    duracionDias = duracionDias,
    localidad = localidad,
    tipoActividad = tipoActividad,
    cuposMaximos = cuposMaximos,
    fechaInicio = fechaInicio.toFechaList(),
    fechaFin = fechaFin.toFechaList(),
    proveedorNombre = proveedorNombre,
    destinoNombre = destinoNombre,
    imagenUrl = imagenUrl,
    monedaOriginal = monedaOriginal,
    idiomaOriginal = idiomaOriginal
)

private fun formatFecha(fecha: List<Int>): String {
    require(fecha.size >= 5) { "Lista de fecha incompleta: $fecha" }
    return "%04d-%02d-%02d %02d:%02d:00".format(
        fecha[0], fecha[1], fecha[2], fecha[3], fecha[4]
    )
}

fun formatFechaP(fecha: List<Int>): String {
    require(fecha.size >= 5) { "Lista de fecha incompleta: $fecha" }
    return "%04d-%02d-%02d %02d:%02d:00".format(
        fecha[0], fecha[1], fecha[2], fecha[3], fecha[4]
    )
}