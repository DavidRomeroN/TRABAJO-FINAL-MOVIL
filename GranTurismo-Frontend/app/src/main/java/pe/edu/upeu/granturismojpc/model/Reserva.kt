package pe.edu.upeu.granturismojpc.model

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import kotlin.text.format
import kotlin.text.replace

// 🎯 DESERIALIZADOR PARA FECHAS QUE VIENEN COMO ARRAY
class DateArrayDeserializer : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String {
        return try {
            when {
                json == null || json.isJsonNull -> ""

                // Si es string, devolverlo tal como está
                json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
                    json.asString
                }

                // Si es array [año, mes, día, hora, minuto]
                json.isJsonArray -> {
                    val dateArray = json.asJsonArray
                    if (dateArray.size() >= 3) {
                        val year = dateArray[0].asInt
                        val month = dateArray[1].asInt
                        val day = dateArray[2].asInt
                        val hour = if (dateArray.size() > 3) dateArray[3].asInt else 0
                        val minute = if (dateArray.size() > 4) dateArray[4].asInt else 0

                        // Formatear como ISO DateTime
                        String.format("%04d-%02d-%02dT%02d:%02d:00", year, month, day, hour, minute)
                    } else {
                        ""
                    }
                }

                else -> {
                    json.toString().replace("\"", "")
                }
            }
        } catch (e: Exception) {
            println("❌ Error procesando fecha: ${e.message}")
            ""
        }
    }
}

// 📝 USUARIO SIMPLIFICADO
data class UsuarioSimpleDTO(
    @SerializedName("idUsuario")
    val idUsuario: Long,

    @SerializedName("email")
    val email: String
)

// 📝 PAQUETE SIMPLIFICADO
data class PaqueteSimpleDTO(
    @SerializedName("idPaquete")
    val idPaquete: Long,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("precioTotal")
    val precioTotal: Double,

    @SerializedName("duracionDias")
    val duracionDias: Int
)

// 🎯 MODELO PRINCIPAL DE RESERVA
data class ReservaDTO(
    @SerializedName("idReserva")
    val idReserva: Long,

    @SerializedName("fechaInicio")
    @JsonAdapter(DateArrayDeserializer::class)
    val fechaInicio: String,

    @SerializedName("fechaFin")
    @JsonAdapter(DateArrayDeserializer::class)
    val fechaFin: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("cantidadPersonas")
    val cantidadPersonas: Int,

    @SerializedName("observaciones")
    val observaciones: String?,

    @SerializedName("usuario")
    val usuario: UsuarioSimpleDTO,

    @SerializedName("paquete")
    val paquete: PaqueteSimpleDTO
)

// 📤 DTO PARA ACTUALIZAR RESERVA (para el PUT)
data class ReservaUpdateDTO(
    val fechaInicio: String,
    val fechaFin: String,
    val estado: String,
    val cantidadPersonas: Int,
    val observaciones: String?,
    val usuario: Long,    // Solo el ID
    val paquete: Long     // Solo el ID
)