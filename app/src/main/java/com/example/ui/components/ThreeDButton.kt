package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Local3DThemeColors

enum class ThreeDButtonVariant {
    GOLD,
    ACCENT,
    SURFACE,
    DANGER
}

@Composable
fun ThreeDButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ThreeDButtonVariant = ThreeDButtonVariant.GOLD,
    cornerRadius: Dp = 14.dp,
    testTag: String = "threed_button",
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val themeColors = Local3DThemeColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val yOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 2.5.dp else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "pressOffset"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed && enabled) 1.dp else 5.dp,
        animationSpec = tween(durationMillis = 60),
        label = "elevationOffset"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val backgroundBrush = when (variant) {
        ThreeDButtonVariant.GOLD -> themeColors.goldGradient
        ThreeDButtonVariant.ACCENT -> Brush.linearGradient(
            listOf(themeColors.accentSecondary, themeColors.accent)
        )
        ThreeDButtonVariant.DANGER -> Brush.linearGradient(
            listOf(Color(0xFFEF4444), Color(0xFFB91C1C))
        )
        ThreeDButtonVariant.SURFACE -> Brush.linearGradient(
            listOf(themeColors.surfaceVariant, themeColors.surface)
        )
    }

    val borderColor = when (variant) {
        ThreeDButtonVariant.GOLD -> Color(0xFFFFF9C4)
        ThreeDButtonVariant.ACCENT -> Color(0x80FFFFFF)
        ThreeDButtonVariant.DANGER -> Color(0x80FFA4A4)
        ThreeDButtonVariant.SURFACE -> themeColors.highlightBorder
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .offset(y = yOffset)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(shape)
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(borderColor, Color(0x60000000))
                ),
                shape = shape
            )
            .background(backgroundBrush)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}
