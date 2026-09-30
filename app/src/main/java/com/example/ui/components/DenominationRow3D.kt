package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CashDenomination
import com.example.data.DenominationType
import com.example.service.IndianNumberToWords
import com.example.ui.theme.Local3DThemeColors

@Composable
fun DenominationRow3D(
    denomination: CashDenomination,
    count: Int,
    onCountChange: (Int) -> Unit,
    depth: ThreeDDepth = ThreeDDepth.STANDARD,
    onHapticClick: () -> Unit = {}
) {
    val themeColors = Local3DThemeColors.current
    var showQuickMultipliers by remember { mutableStateOf(false) }
    var isEditingDirectly by remember { mutableStateOf(false) }
    var directText by remember(count) { mutableStateOf(if (count == 0) "" else count.toString()) }
    val focusManager = LocalFocusManager.current

    val subtotal = denomination.value.toLong() * count

    ThreeDCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("denom_row_${denomination.value}_${denomination.type.name}"),
        depth = depth,
        cornerRadius = 14.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 3D Note / Coin Visual Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        showQuickMultipliers = !showQuickMultipliers
                        onHapticClick()
                    }
                ) {
                    if (denomination.type == DenominationType.NOTE) {
                        // 3D Banknote Emblem
                        Box(
                            modifier = Modifier
                                .size(width = 68.dp, height = 36.dp)
                                .shadow(4.dp, RoundedCornerShape(6.dp))
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.6f))
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                    Brush.linearGradient(
                                        listOf(denomination.accentColor, denomination.primaryColor)
                                    )
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "₹",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${denomination.value}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    } else {
                        // 3D Metallic Coin Emblem
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFFFF176), Color(0xFF6D4C41))
                                    ),
                                    shape = CircleShape
                                )
                                .background(
                                    Brush.radialGradient(
                                        listOf(denomination.accentColor, denomination.primaryColor)
                                    )
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₹${denomination.value}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "×",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.accent.copy(alpha = 0.8f)
                    )
                }

                // Counter Box / Direct input
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(themeColors.surfaceVariant)
                            .clickable(enabled = count > 0) {
                                if (count > 0) {
                                    onCountChange(count - 1)
                                    onHapticClick()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease count",
                            tint = if (count > 0) themeColors.accent else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Count Clickable / Editable Display
                    if (isEditingDirectly) {
                        OutlinedTextField(
                            value = directText,
                            onValueChange = { newVal ->
                                val clean = newVal.filter { it.isDigit() }.take(5)
                                directText = clean
                                val parsed = clean.toIntOrNull() ?: 0
                                onCountChange(parsed)
                            },
                            modifier = Modifier
                                .width(70.dp)
                                .height(46.dp)
                                .testTag("direct_input_${denomination.value}"),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    isEditingDirectly = false
                                    focusManager.clearFocus()
                                }
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColors.accent,
                                unfocusedBorderColor = themeColors.highlightBorder,
                                focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                            )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(68.dp)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (count > 0) themeColors.accent.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(Color.Black.copy(alpha = 0.35f))
                                .clickable {
                                    isEditingDirectly = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = count.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (count > 0) themeColors.accent else Color.White.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Plus Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(themeColors.surfaceVariant)
                            .clickable {
                                onCountChange(count + 1)
                                onHapticClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase count",
                            tint = themeColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Subtotal
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(96.dp)
                ) {
                    Text(
                        text = IndianNumberToWords.formatIndianCurrency(subtotal.toDouble()),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (subtotal > 0) Color.White else Color.White.copy(alpha = 0.4f),
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                    if (count > 0) {
                        Text(
                            text = "${denomination.type.name.lowercase()}${if (count > 1) "s" else ""}",
                            fontSize = 10.sp,
                            color = Color.LightGray.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Quick Steppers Row (+5, +10, +25, +50, +100, Clear)
            AnimatedVisibility(
                visible = showQuickMultipliers || count > 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val quickAddList = listOf(5, 10, 25, 50, 100)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        quickAddList.forEach { increment ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeColors.surfaceVariant.copy(alpha = 0.7f))
                                    .border(
                                        width = 0.8.dp,
                                        color = themeColors.highlightBorder.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        onCountChange(count + increment)
                                        onHapticClick()
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+$increment",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = themeColors.accent
                                )
                            }
                        }
                    }

                    if (count > 0) {
                        IconButton(
                            onClick = {
                                onCountChange(0)
                                onHapticClick()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear count",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
