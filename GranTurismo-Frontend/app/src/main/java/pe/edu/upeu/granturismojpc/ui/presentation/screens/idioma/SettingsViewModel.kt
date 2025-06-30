package pe.edu.upeu.granturismojpc.ui.presentation.screens.idioma

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.UserConfig
import pe.edu.upeu.granturismojpc.model.getStrings
import pe.edu.upeu.granturismojpc.repository.PreferenceRepository
import pe.edu.upeu.granturismojpc.ui.strings.Strings
import pe.edu.upeu.granturismojpc.ui.strings.StringsEs
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: PreferenceRepository
) : ViewModel() {

    var userConfig by mutableStateOf<UserConfig?>(null)
        private set

    var strings by mutableStateOf<Strings>(StringsEs)
        private set

    fun cargarPreferencias() {
        viewModelScope.launch {
            repository.getMyPreferences().onSuccess { pref ->
                userConfig = UserConfig(
                    idPreference = pref.idPreference,
                    idUsuario = pref.idUsuario,
                    preferredLanguageCode = pref.preferredLanguageCode,
                    preferredCurrencyCode = pref.preferredCurrencyCode
                )
                strings = getStrings(userConfig!!)
            }
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            repository.updateMyLanguage(languageCode).onSuccess {
                val updatedConfig = userConfig?.copy(preferredLanguageCode = it.preferredLanguageCode)
                Log.i("Settings",updatedConfig.toString())
                if (updatedConfig != null) {
                    userConfig = updatedConfig
                    strings = getStrings(updatedConfig)
                    TokenUtils.TEMP_LANGUAJE=languageCode
                } else {
                    Log.e("SettingsViewModel", "userConfig es null")
                }
            }.onFailure {
                Log.e("SettingsViewModel", "Error al actualizar idioma", it)
            }
        }
    }
    fun updateCurrency(currencyCode: String) {
        viewModelScope.launch {
            repository.updateMyCurrency(currencyCode).onSuccess {
                val updatedConfig = userConfig?.copy(preferredCurrencyCode = it.preferredCurrencyCode)
                if (updatedConfig != null) {
                    userConfig = updatedConfig
                }
            }.onFailure {
                Log.e("CurrencyScreen", "Error al actualizar moneda", it)
            }
        }
    }
}