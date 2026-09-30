package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Local3DThemeColors

enum class ThreeDDepth(val elevation: Dp, val borderAlpha: Float, val label: String) {
    MILD(4.dp, 0.25f, "Mild 3D"),
    STANDARD(8.dp, 0.45f, "Standard 3D"),
    ULTRA(16.dp, 0.70f, "Ultra 3D")
}

@Composable
fun ThreeDCard(
    modifier: Modifier = Modifier,
    depth: ThreeDDepth = ThreeDDepth.STANDARD,
    cornerRadius: Dp = 18.dp,
    customGradient: Brush? = null,
    borderColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val themeColors = Local3DThemeColors.current
    val shape = RoundedCornerShape(cornerRadius)

    val effectiveBorder = borderColor ?: themeColors.highlightBorder.copy(alpha = depth.borderAlpha)
    val backgroundBrush = customGradient ?: themeColors.cardGradient

    Box(
        modifier = modifier
            .shadow(
                elevation = depth.elevation,
                shape = shape,
                ambientColor = Color.Black,
                spotColor = Color.Black.copy(alpha = 0.8f)
            )
            .clip(shape)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        effectiveBorder,
                        themeColors.shadowBorder.copy(alpha = depth.borderAlpha)
                    )
                ),
                shape = shape
            )
            .background(backgroundBrush)
            .padding(1.dp),
        content = content
    )
}
