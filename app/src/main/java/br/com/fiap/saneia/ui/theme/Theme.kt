package br.com.fiap.saneia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Identidade visual: azul água + verde saneamento
val AzulAgua = Color(0xFF0B6E99)
val AzulClaro = Color(0xFFD6EEF8)
val VerdeSaneia = Color(0xFF2E8B57)
val VerdeClaro = Color(0xFFD9F2E3)
val AmareloAlerta = Color(0xFFB7791F)
val AmareloClaro = Color(0xFFFFF1D6)
val VermelhoAlerta = Color(0xFFB3261E)

private val SaneiaColors = lightColorScheme(
    primary = AzulAgua,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Color(0xFF002B3D),
    secondary = VerdeSaneia,
    onSecondary = Color.White,
    secondaryContainer = VerdeClaro,
    onSecondaryContainer = Color(0xFF00391D),
    error = VermelhoAlerta,
    background = Color(0xFFF7FAFC),
    surface = Color.White
)

@Composable
fun SaneiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = SaneiaColors, content = content)
}
