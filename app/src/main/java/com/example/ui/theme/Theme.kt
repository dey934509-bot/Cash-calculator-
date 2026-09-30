package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class App3DTheme(val displayName: String, val description: String) {
    ROYAL_GOLD("Royal Gold 3D", "Deep Emerald & 24K Gold luxury"),
    NEO_OBSIDIAN("Neo Obsidian 3D", "Dark Neumorphism & Mint glow"),
    VIBRANT_RUPEE("Vibrant Rupee 3D", "Reserve Navy & Multi-note colors"),
    CHAMPAGNE_BRONZE("Bronze Luxe 3D", "Metallic Copper & Warm Brass")
}

data class Custom3DThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val accent: Color,
    val accentSecondary: Color,
    val highlightBorder: Color,
    val shadowBorder: Color,
    val goldGradient: Brush,
    val cardGradient: Brush,
    val themeMode: App3DTheme
)

val Local3DThemeColors = staticCompositionLocalOf {
    createRoyalGoldColors()
}

fun createRoyalGoldColors() = Custom3DThemeColors(
    background = RoyalBackground,
    surface = RoyalSurface,
    surfaceVariant = RoyalSurfaceVariant,
    accent = RoyalGold,
    accentSecondary = RoyalGoldAccent,
    highlightBorder = Color(0x60FFE082),
    shadowBorder = Color(0x99000000),
    goldGradient = Brush.linearGradient(
        listOf(Color(0xFFFFF176), Color(0xFFFFD700), Color(0xFFB45309))
    ),
    cardGradient = Brush.verticalGradient(
        listOf(Color(0xFF13362B), Color(0xFF0C241C))
    ),
    themeMode = App3DTheme.ROYAL_GOLD
)

fun createNeoObsidianColors() = Custom3DThemeColors(
    background = NeoBackground,
    surface = NeoSurface,
    surfaceVariant = NeoSurfaceVariant,
    accent = NeoMint,
    accentSecondary = NeoMintDark,
    highlightBorder = Color(0x40FFFFFF),
    shadowBorder = Color(0xCC000000),
    goldGradient = Brush.linearGradient(
        listOf(Color(0xFF80FFAE), Color(0xFF00E676), Color(0xFF00B0FF))
    ),
    cardGradient = Brush.verticalGradient(
        listOf(Color(0xFF1F242C), Color(0xFF13171D))
    ),
    themeMode = App3DTheme.NEO_OBSIDIAN
)

fun createVibrantRupeeColors() = Custom3DThemeColors(
    background = VibrantBackground,
    surface = VibrantSurface,
    surfaceVariant = VibrantSurfaceVariant,
    accent = VibrantGold,
    accentSecondary = VibrantCyan,
    highlightBorder = Color(0x5082B1FF),
    shadowBorder = Color(0x99000000),
    goldGradient = Brush.linearGradient(
        listOf(Color(0xFFFFD54F), Color(0xFFF9A825), Color(0xFFFF6F00))
    ),
    cardGradient = Brush.verticalGradient(
        listOf(Color(0xFF182855), Color(0xFF0E1838))
    ),
    themeMode = App3DTheme.VIBRANT_RUPEE
)

fun createChampagneBronzeColors() = Custom3DThemeColors(
    background = BronzeBackground,
    surface = BronzeSurface,
    surfaceVariant = BronzeSurfaceVariant,
    accent = BronzeGold,
    accentSecondary = BronzeCopper,
    highlightBorder = Color(0x50FFCC80),
    shadowBorder = Color(0x99000000),
    goldGradient = Brush.linearGradient(
        listOf(Color(0xFFFFE0B2), Color(0xFFD4A373), Color(0xFF8D5B4C))
    ),
    cardGradient = Brush.verticalGradient(
        listOf(Color(0xFF33231A), Color(0xFF1F140D))
    ),
    themeMode = App3DTheme.CHAMPAGNE_BRONZE
)

@Composable
fun MyApplicationTheme(
    selectedTheme: App3DTheme = App3DTheme.ROYAL_GOLD,
    content: @Composable () -> Unit
) {
    val customColors = when (selectedTheme) {
        App3DTheme.ROYAL_GOLD -> createRoyalGoldColors()
        App3DTheme.NEO_OBSIDIAN -> createNeoObsidianColors()
        App3DTheme.VIBRANT_RUPEE -> createVibrantRupeeColors()
        App3DTheme.CHAMPAGNE_BRONZE -> createChampagneBronzeColors()
    }

    val darkScheme = darkColorScheme(
        primary = customColors.accent,
        onPrimary = Color.Black,
        secondary = customColors.accentSecondary,
        background = customColors.background,
        surface = customColors.surface,
        surfaceVariant = customColors.surfaceVariant,
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFFE2E8F0)
    )

    CompositionLocalProvider(Local3DThemeColors provides customColors) {
        MaterialTheme(
            colorScheme = darkScheme,
            typography = Typography,
            content = content
        )
    }
}
