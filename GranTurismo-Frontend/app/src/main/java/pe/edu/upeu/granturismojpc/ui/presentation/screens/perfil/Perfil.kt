package pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.ComponentePuntos
import pe.edu.upeu.granturismojpc.ui.presentation.components.SimpleBottomNavigationBar
import pe.edu.upeu.granturismojpc.utils.LanguajesD
import pe.edu.upeu.granturismojpc.utils.TokenUtils


@Composable
fun Perfil(
    navergarRegistro: (String) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }
    Scaffold (
        bottomBar = {
            SimpleBottomNavigationBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(navBarPadding)
                    .height(56.dp))
        },
    ) {
        paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
                .padding(paddingValues) // <- Añadido para respetar el espacio del bottomBar
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                Modifier
                    .fillMaxWidth()
            ){
                TextButton(
                    onClick = {
                        navController.navigate(Destinations.Login.route) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = strings.logout,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = TokenUtils.USER_LOGIN,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            ComponentePuntos(navController = navController)

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            SettingItem(strings.lang, LanguajesD(TokenUtils.TEMP_LANGUAJE)) {
                navController.navigate(Destinations.Language.route)
            }
            SettingItem(strings.currency, TokenUtils.TEMP_CURRENCY) {
                navController.navigate(Destinations.Currency.route)
            }
            SettingItem("Todos mis planes") {
                navController.navigate(Destinations.Plans.route)
            }
            if(TokenUtils.USER_ROLE=="USER"){
            SettingItem("Carrito") {
                navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString()))
            }}
            SettingItem("Contactenos") {
                navController.navigate(Destinations.Contact.route)
            }
            SettingItem("Pagina web") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                context.startActivity(intent)
            }
            SettingItem("Políticas de privacidad") {
                navController.navigate(Destinations.Privacy.route)
            }
            SettingItem("Condiciones de uso") {
                navController.navigate(Destinations.Terms.route)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "VERSION 1.1.1.1",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun SettingItem(title: String, value: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        if (value != null) {
            Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray
        )
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}