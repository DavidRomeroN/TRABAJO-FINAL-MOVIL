package pe.edu.upeu.granturismojpc.model

import pe.edu.upeu.granturismojpc.ui.strings.Strings
import pe.edu.upeu.granturismojpc.ui.strings.StringsEn
import pe.edu.upeu.granturismojpc.ui.strings.StringsEs

data class UserConfig(
    var idPreference: Int,
    var idUsuario: Int,
    var preferredCurrencyCode: String,
    var preferredLanguageCode: String
)

data class UserConfigUpdateDto(
    val currencyCode: String,
    val languageCode: String
)

fun getStrings(userConfig: UserConfig): Strings = when (userConfig.preferredLanguageCode) {
    "es" -> StringsEs
    "en" -> StringsEn
    else -> StringsEs
}

fun getStringsByCode(langCode: String): Strings = when (langCode) {
    "en" -> StringsEn
    "es" -> StringsEs
    else -> StringsEs
}