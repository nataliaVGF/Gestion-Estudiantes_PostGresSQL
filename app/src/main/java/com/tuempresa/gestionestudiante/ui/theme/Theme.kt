package com.tuempresa.gestionestudiante.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Colores personalizados para el tema de la app.
 *
 * Analogía: Es como elegir la paleta de colores para pintar
 * todo el edificio (la aplicación).
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBB86FC),
    onPrimaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFF9800),
    error = Color(0xFFB00020),
    onError = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color(0xFF03DAC6),
    tertiary = Color(0xFFFFB74D),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1)
)

/**
 * Tema principal de la aplicación.
 * Soporta modo claro y oscuro automáticamente.
 */
@Composable
fun GestionEstudiantesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}