package pe.edu.upeu.granturismojpc.ui.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.granturismojpc.R
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.ui.theme.LightOrangeColors
import pe.edu.upeu.granturismojpc.ui.theme.orange_errorDark
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import kotlin.coroutines.coroutineContext

@Composable
fun HeroSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val strings = remember(TokenUtils.TEMP_LANGUAJE) { getStringsByCode(TokenUtils.TEMP_LANGUAJE) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp) // un poco más de altura para dejar espacio a la barra
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.lago_titicaca),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Gradiente
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 100f
                    )
                )
        )

        // Texto
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, bottom = 56.dp) // deja espacio para la barra
        ) {
            Text(
                text = strings.homeText1,
                style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = strings.homeText2,
                style = TextStyle(color = Color.White, fontSize = 14.sp)
            )
        }
/*
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar asociaciones o servicios") },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 25.dp) // este valor controla cuánto sobresale
                .fillMaxWidth(0.9f), // ancho un poco menor al total
            singleLine = true,
            colors = TextFieldDefaults.colors(
            )
        )
        */

    }
}
@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Buscar asociaciones o servicios") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(50),

        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}
