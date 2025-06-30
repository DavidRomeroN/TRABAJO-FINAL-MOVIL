package pe.edu.upeu.granturismojpc.repository

import pe.edu.upeu.granturismojpc.model.CanjePuntosRequestDTO
import pe.edu.upeu.granturismojpc.model.PuntoDTO
import pe.edu.upeu.granturismojpc.model.PuntosDisponiblesResponseDTO
import pe.edu.upeu.granturismojpc.model.ReservaDTO
import pe.edu.upeu.granturismojpc.model.ReservaUpdateDTO
import pe.edu.upeu.granturismojpc.utils.RetrofitClient
import retrofit2.Response
import retrofit2.http.*

interface PuntosApiService {
    @GET("api/puntos/me")
    suspend fun getPuntosDisponibles(
        @Header("Authorization") token: String
    ): Response<PuntosDisponiblesResponseDTO>

    @GET("api/puntos/historial")
    suspend fun getHistorialPuntos(
        @Header("Authorization") token: String
    ): Response<List<PuntoDTO>>

    @POST("api/puntos/canjear")
    suspend fun canjearPuntos(
        @Header("Authorization") token: String,
        @Body request: CanjePuntosRequestDTO
    ): Response<PuntoDTO>

    // ⭐ NUEVOS ENDPOINTS PARA RESERVAS
    @GET("reservas/{id}")
    suspend fun getReserva(
        @Path("id") reservaId: Long,
        @Header("Authorization") token: String
    ): Response<ReservaDTO>

    @PUT("reservas/{id}")
    suspend fun updateReserva(
        @Path("id") reservaId: Long,
        @Header("Authorization") token: String,
        @Body reserva: ReservaUpdateDTO
    ): Response<ReservaDTO>
}

class PuntosRepository {
    private val apiService = RetrofitClient.retrofit.create(PuntosApiService::class.java)

    // 🔧 FUNCIÓN PRIVADA PARA LIMPIAR Y FORMATEAR TOKEN
    private fun limpiarToken(token: String): String {
        val cleanToken = token.trim().replace("\\s+".toRegex(), " ")
        return if (cleanToken.startsWith("Bearer ")) {
            cleanToken
        } else {
            "Bearer $cleanToken"
        }
    }

    suspend fun getPuntosDisponibles(token: String): Result<PuntosDisponiblesResponseDTO> {
        return try {
            val tokenLimpio = limpiarToken(token) // ⭐ USAR TOKEN LIMPIO
            println("🔑 getPuntosDisponibles - Token: ${tokenLimpio.take(20)}...")

            val response = apiService.getPuntosDisponibles(tokenLimpio)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                println("❌ Error HTTP en getPuntosDisponibles: ${response.code()}")
                Result.failure(Exception("Error HTTP: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            println("💥 Exception en getPuntosDisponibles: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun getHistorialPuntos(token: String): Result<List<PuntoDTO>> {
        return try {
            val tokenLimpio = limpiarToken(token) // ⭐ USAR TOKEN LIMPIO
            println("🔑 getHistorialPuntos - Token: ${tokenLimpio.take(20)}...")

            val response = apiService.getHistorialPuntos(tokenLimpio)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                println("❌ Error HTTP en getHistorialPuntos: ${response.code()}")
                Result.failure(Exception("Error HTTP: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            println("💥 Exception en getHistorialPuntos: ${e.message}")
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun canjearPuntos(token: String, request: CanjePuntosRequestDTO): Result<PuntoDTO> {
        return try {
            val tokenLimpio = limpiarToken(token) // ⭐ USAR TOKEN LIMPIO
            println("🔑 canjearPuntos - Token: ${tokenLimpio.take(20)}...")
            println("📋 Request: $request")

            val response = apiService.canjearPuntos(tokenLimpio, request)

            when {
                response.isSuccessful -> {
                    response.body()?.let { data ->
                        println("✅ Canje exitoso: $data")
                        Result.success(data)
                    } ?: Result.failure(Exception("Respuesta vacía del servidor"))
                }
                response.code() == 401 -> {
                    println("❌ Error 401: Token no válido o expirado")
                    val errorBody = response.errorBody()?.string()
                    println("❌ Error body: $errorBody")
                    Result.failure(Exception("Token de autenticación inválido. Por favor, inicia sesión nuevamente."))
                }
                response.code() == 400 -> {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Error 400: $errorBody")
                    Result.failure(Exception("Datos inválidos: $errorBody"))
                }
                else -> {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Error HTTP ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: ${errorBody ?: response.message()}"))
                }
            }
        } catch (e: Exception) {
            println("💥 Exception en canjearPuntos: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ⭐ NUEVA FUNCIÓN: Confirmar reserva cambiando estado a CONFIRMADA
    suspend fun confirmarReserva(token: String, reservaId: Long): Result<ReservaDTO> {
        return try {
            val tokenLimpio = limpiarToken(token) // ⭐ USAR TOKEN LIMPIO
            println("🚀 Obteniendo reserva ID: $reservaId")
            println("🔑 confirmarReserva - Token: ${tokenLimpio.take(20)}...")

            // 1. Primero obtener la reserva actual
            val getResponse = apiService.getReserva(reservaId, tokenLimpio)

            if (!getResponse.isSuccessful) {
                println("❌ Error obteniendo reserva: ${getResponse.code()}")
                return Result.failure(Exception("No se pudo obtener la reserva"))
            }

            val reservaActual = getResponse.body()
                ?: return Result.failure(Exception("Reserva no encontrada"))

            println("📋 Reserva actual - Estado: ${reservaActual.estado}")

            // 2. Verificar que esté PENDIENTE
            if (reservaActual.estado != "PENDIENTE") {
                println("⚠️ La reserva ya está en estado: ${reservaActual.estado}")
                return Result.failure(Exception("La reserva ya está confirmada o no es válida"))
            }

            // 3. Crear DTO para actualizar a CONFIRMADA
            val reservaUpdate = ReservaUpdateDTO(
                fechaInicio = reservaActual.fechaInicio,
                fechaFin = reservaActual.fechaFin,
                estado = "CONFIRMADA", // ⭐ CAMBIO CLAVE
                cantidadPersonas = reservaActual.cantidadPersonas,
                observaciones = reservaActual.observaciones,
                usuario = reservaActual.usuario.idUsuario,
                paquete = reservaActual.paquete.idPaquete
            )

            println("🔄 Actualizando reserva a estado CONFIRMADA")

            // 4. Actualizar la reserva (esto otorgará automáticamente los puntos)
            val updateResponse = apiService.updateReserva(reservaId, tokenLimpio, reservaUpdate)

            println("📡 HTTP Status: ${updateResponse.code()}")

            if (updateResponse.isSuccessful) {
                updateResponse.body()?.let { reservaConfirmada ->
                    println("✅ Reserva confirmada exitosamente: ${reservaConfirmada.idReserva}")
                    println("🎁 Los puntos se otorgaron automáticamente en el backend")
                    Result.success(reservaConfirmada)
                } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                val errorBody = updateResponse.errorBody()?.string()
                println("❌ Error actualizando reserva ${updateResponse.code()}: $errorBody")
                Result.failure(Exception("Error confirmando reserva: ${updateResponse.code()}"))
            }

        } catch (e: Exception) {
            println("💥 Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}