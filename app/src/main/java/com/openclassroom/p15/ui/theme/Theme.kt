package com.openclassroom.p15.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Red80,           // Boutons d'action rouge
    onPrimary = White80,       // Texte/icône blanc sur boutons

    secondary = White80,       // Éléments secondaires blanc
    onSecondary = Black80,     // Texte noir sur éléments blancs

    tertiary = Red80,
    onTertiary = White80,

    background = Black80,      // Fond noir
    onBackground = White80,    // Texte blanc sur fond

    surface = Black80,         // Surfaces (cartes)
    onSurface = White80,       // Texte blanc sur surfaces

    surfaceVariant = Grey80,   // Listes, items alternatifs
    onSurfaceVariant = White80,// Texte sur surfaces grises

    outline = Grey80,          // Bordures
    outlineVariant = Grey80,   // Pour les inputs
)

@Composable
fun P15Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}