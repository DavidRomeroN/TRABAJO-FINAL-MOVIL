package pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.ui.presentation.screens.idioma.SettingsViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun CurrencyScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val userConfig = viewModel.userConfig
    val currentCode = userConfig?.preferredCurrencyCode
    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }

    val currencyOptions = listOf(
        "PEN" to strings.pen,
        "USD" to strings.usd,
        "EUR" to strings.eur
    )

    Column(modifier = modifier.padding(16.dp, top= 80.dp)) {
        Text(
            text = strings.currencyLabel,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        currencyOptions.forEach { (code, title) ->
            CurrencySettingItem(
                title = title,
                code = code,
                selected = code == currentCode
            ) {
                viewModel.updateCurrency(code)
                TokenUtils.TEMP_CURRENCY=code
                SincronizacionControl.paquetesSincronizados=false
                navController.popBackStack()
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CurrencySettingItem(
    title: String,
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
            text = title,
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
