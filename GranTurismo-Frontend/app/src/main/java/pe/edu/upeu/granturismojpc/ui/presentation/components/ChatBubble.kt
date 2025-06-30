package pe.edu.upeu.granturismojpc.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.granturismojpc.model.ChatMessage
import pe.edu.upeu.granturismojpc.utils.TokenUtils

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.remitente == "usuario"
    val bubbleColor = if (isUser) Color(0xFFFFA726) else Color(0xFFE0E0E0) // Cambiado
    val textColor = if (isUser) Color.White else Color.Black
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(bubbleColor)
                .padding(12.dp)
                .widthIn(max = 260.dp)
        ) {
            Text(text = message.contenido, color = textColor)
        }
    }
}
