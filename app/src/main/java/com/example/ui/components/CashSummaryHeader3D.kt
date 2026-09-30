package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.IndianNumberToWords
import com.example.ui.theme.Local3DThemeColors

@Composable
fun CashSummaryHeader3D(
    totalDenominationAmount: Double,
    netCashInHand: Double,
    totalNotes: Int,
    totalCoins: Int,
    hasAdjustments: Boolean,
    onSpeakTotal: (Boolean) -> Unit, // inHindi boolean
    onOpenThemeSelector: () -> Unit,
    depth: ThreeDDepth = ThreeDDepth.STANDARD
) {
    val themeColors = Local3DThemeColors.current
    var showHindiWords by remember { mutableStateOf(false) }

    val amountInWords = if (showHindiWords) {
        IndianNumberToWords.convertToWordsHindi(netCashInHand)
    } else {
        IndianNumberToWords.convertToWordsEnglish(netCashInHand)
    }

    ThreeDCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cash_summary_header"),
        depth = depth,
        cornerRadius = 20.dp,
        borderColor = themeColors.accent.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Vault Indicator, Theme Chip & Audio Speak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Vault Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, themeColors.accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onOpenThemeSelector() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(themeColors.accent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = themeColors.themeMode.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.accent
                        )
                    }
                }

                // Audio & Language toggles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // English / Hindi Words Switcher
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(themeColors.surfaceVariant)
                            .clickable {
                                showHindiWords = !showHindiWords
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language toggle",
                                tint = themeColors.accent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHindiWords) "हिंदी" else "ENG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // TTS Speaker Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(themeColors.accent)
                            .clickable {
                                onSpeakTotal(showHindiWords)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak total",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "TOTAL CASH IN HAND",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = themeColors.accent.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main 3D Display
            Text(
                text = IndianNumberToWords.formatIndianCurrency(netCashInHand),
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // If adjustments exist, show denomination breakdown vs net cash
            AnimatedVisibility(visible = hasAdjustments) {
                Text(
                    text = "Denominations: ${IndianNumberToWords.formatIndianCurrency(totalDenominationAmount)}",
                    fontSize = 12.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Words Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = amountInWords,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFECEFF1),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes & Coins Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Notes pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(themeColors.surfaceVariant)
                        .border(1.dp, themeColors.highlightBorder.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notes: ",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = "$totalNotes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                    }
                }

                // Coins pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(themeColors.surfaceVariant)
                        .border(1.dp, themeColors.highlightBorder.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Coins: ",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = "$totalCoins",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                    }
                }
            }
        }
    }
}
