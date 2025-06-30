package pe.edu.upeu.granturismojpc.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.data.remote.RestPreference
import pe.edu.upeu.granturismojpc.model.UserConfig
import pe.edu.upeu.granturismojpc.model.UserConfigUpdateDto
import pe.edu.upeu.granturismojpc.model.toRoom
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import pe.edu.upeu.granturismojpc.utils.isNetworkAvailable
import javax.inject.Inject

interface PreferenceRepository {
    suspend fun getMyPreferences(): Result<UserConfig>
    suspend fun updateMyPreferences(updateDto: UserConfigUpdateDto): Result<UserConfig>
    suspend fun updateMyCurrency(currencyCode: String): Result<UserConfig>
    suspend fun updateMyLanguage(languageCode: String): Result<UserConfig>
    suspend fun deleteMyPreferences(): Result<Unit>
    suspend fun createMyDefaultPreferences(): Result<UserConfig>
    suspend fun checkMyPreferencesExist(): Result<Boolean>
}

class PreferenceRepositoryImp @Inject constructor(
    private val rest: RestPreference,
): PreferenceRepository{
    override suspend fun getMyPreferences(): Result<UserConfig> {
        return try {
            val response = rest.getMyPreferences(TokenUtils.TOKEN_CONTENT)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMyPreferences(updateDto: UserConfigUpdateDto): Result<UserConfig> {
        return try {
            val response = rest.updateMyPreferences(TokenUtils.TOKEN_CONTENT, updateDto)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMyCurrency(currencyCode: String): Result<UserConfig> {
        return try {
            val response = rest.updateMyCurrency(TokenUtils.TOKEN_CONTENT, currencyCode)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMyLanguage(languageCode: String): Result<UserConfig> {
        return try {
            val response = rest.updateMyLanguage(TokenUtils.TOKEN_CONTENT, languageCode)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMyPreferences(): Result<Unit> {
        return try {
            val response = rest.deleteMyPreferences(TokenUtils.TOKEN_CONTENT)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createMyDefaultPreferences(): Result<UserConfig> {
        return try {
            val response = rest.createMyDefaultPreferences(TokenUtils.TOKEN_CONTENT)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Respuesta vacía"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkMyPreferencesExist(): Result<Boolean> {
        return try {
            val response = rest.checkMyPreferencesExist(TokenUtils.TOKEN_CONTENT)
            if (response.isSuccessful) {
                val hasPreferences = response.body()?.get("hasPreferences") ?: false
                Result.success(hasPreferences)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 
