package pe.edu.upeu.granturismojpc.ui.presentation.screens.idioma

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import pe.edu.upeu.granturismojpc.utils.LanguajesD

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val userConfig = viewModel.userConfig
    val strings = viewModel.strings
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (viewModel.userConfig == null) {
            viewModel.cargarPreferencias()
        }
    }
    Column(modifier = modifier.padding(16.dp, top = 80.dp)) {
        Text(text = strings.settingsTitle, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = strings.preferedLang, style = MaterialTheme.typography.bodyLarge)

        Column {
            LanguageOption(
                label = "Español",
                code = "es",
                selected = userConfig?.preferredLanguageCode == "es"
            ) {
                viewModel.updateLanguage("es")
                SincronizacionControl.paquetesSincronizados=false
            }

            Spacer(modifier = Modifier.width(16.dp))

            LanguageOption(
                label = "English",
                code = "en",
                selected = userConfig?.preferredLanguageCode == "en"
            ) {
                viewModel.updateLanguage("en")
                SincronizacionControl.paquetesSincronizados=false
            }
        }
    }
}

@Composable
fun LanguageOption(
    label: String,
    code: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color(0xFFE0F7FA) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontSize = 16.sp
        )

        Text(
            text = code,
            color = Color.Black,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}
