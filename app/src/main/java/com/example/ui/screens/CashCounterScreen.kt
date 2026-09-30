package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DenominationType
import com.example.data.IndianCurrencyCatalog
import com.example.ui.components.CashSummaryHeader3D
import com.example.ui.components.DenominationRow3D
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDButtonVariant
import com.example.ui.components.ThreeDCard
import com.example.ui.theme.Local3DThemeColors
import com.example.viewmodel.CashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashCounterScreen(
    viewModel: CashViewModel,
    onNavigateToExtraAndHand: () -> Unit
) {
    val context = LocalContext.current
    val themeColors = Local3DThemeColors.current

    val counts by viewModel.counts.collectAsStateWithLifecycle()
    val totalDenomAmount by viewModel.totalDenominationAmount.collectAsStateWithLifecycle()
    val totalNotes by viewModel.totalNotesCount.collectAsStateWithLifecycle()
    val totalCoins by viewModel.totalCoinsCount.collectAsStateWithLifecycle()
    val netCashInHand by viewModel.netCashInHand.collectAsStateWithLifecycle()
    val extraItems by viewModel.extraItems.collectAsStateWithLifecycle()
    val depth by viewModel.selectedDepth.collectAsStateWithLifecycle()

    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Notes, 2: Coins
    var showAiScannerSheet by remember { mutableStateOf(false) }
    var showGstCalculatorSheet by remember { mutableStateOf(false) }
    var showThemeSelectorSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleInput by remember { mutableStateOf("") }
    var showClearConfirmation by remember { mutableStateOf(false) }

    val filteredDenominations = remember(selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> IndianCurrencyCatalog.denominations.filter { it.type == DenominationType.NOTE }
            2 -> IndianCurrencyCatalog.denominations.filter { it.type == DenominationType.COIN }
            else -> IndianCurrencyCatalog.denominations
        }
    }

    Scaffold(
        containerColor = themeColors.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Vault 3D Display Header
            item {
                CashSummaryHeader3D(
                    totalDenominationAmount = totalDenomAmount,
                    netCashInHand = netCashInHand,
                    totalNotes = totalNotes,
                    totalCoins = totalCoins,
                    hasAdjustments = extraItems.isNotEmpty(),
                    onSpeakTotal = { inHindi ->
                        viewModel.speakTotal(inHindi)
                    },
                    onOpenThemeSelector = {
                        showThemeSelectorSheet = true
                    },
                    depth = depth
                )
            }

            // 2. Primary 3D Action Tools Bar (AI Scan, Extra & Hand, GST, History, Share, Save)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // AI Cash Scan Button
                    ThreeDButton(
                        onClick = {
                            viewModel.triggerHapticClick()
                            showAiScannerSheet = true
                        },
                        variant = ThreeDButtonVariant.GOLD,
                        testTag = "ai_scan_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Cash Scan",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Cash Scan", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }

                    // Extra Add & Hand Button (with Badge)
                    BadgedBox(
                        badge = {
                            if (extraItems.isNotEmpty()) {
                                Badge(
                                    containerColor = Color(0xFFEF5350),
                                    contentColor = Color.White
                                ) {
                                    Text("${extraItems.size}")
                                }
                            }
                        }
                    ) {
                        ThreeDButton(
                            onClick = {
                                viewModel.triggerHapticClick()
                                onNavigateToExtraAndHand()
                            },
                            variant = ThreeDButtonVariant.ACCENT,
                            testTag = "extra_and_hand_button"
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Extra Add & Hand",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Extra & Hand", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                        }
                    }

                    // Save Tally Button
                    ThreeDButton(
                        onClick = {
                            viewModel.triggerHapticClick()
                            showSaveDialog = true
                        },
                        variant = ThreeDButtonVariant.SURFACE,
                        testTag = "save_tally_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save Tally",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
                    }

                    // Share WhatsApp / Slip Button
                    ThreeDButton(
                        onClick = {
                            viewModel.triggerHapticClick()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, viewModel.generateReceiptText())
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Cash Slip via"))
                        },
                        variant = ThreeDButtonVariant.SURFACE,
                        testTag = "share_slip_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Slip",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
                    }

                    // Financial Tools (GST & Change Dispenser)
                    ThreeDButton(
                        onClick = {
                            viewModel.triggerHapticClick()
                            showGstCalculatorSheet = true
                        },
                        variant = ThreeDButtonVariant.SURFACE,
                        testTag = "gst_tools_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "GST & Change Tools",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GST / Change", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
                    }

                    // History Register
                    ThreeDButton(
                        onClick = {
                            viewModel.triggerHapticClick()
                            showHistorySheet = true
                        },
                        variant = ThreeDButtonVariant.SURFACE,
                        testTag = "history_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("History", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
                    }

                    // Reset / Clear All
                    ThreeDButton(
                        onClick = {
                            viewModel.triggerHapticClick()
                            showClearConfirmation = true
                        },
                        variant = ThreeDButtonVariant.DANGER,
                        testTag = "clear_all_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear all",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // 3. Category Filter Chips (All, Notes, Coins)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("All Items (12)", "Notes (7)", "Coins (5)")
                    filters.forEachIndexed { index, title ->
                        val isSelected = selectedFilterIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) themeColors.accent else themeColors.surfaceVariant)
                                .clickable {
                                    selectedFilterIndex = index
                                    viewModel.triggerHapticClick()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            // 4. Denomination Rows List
            items(filteredDenominations, key = { "${it.type.name}_${it.value}" }) { denom ->
                val key = if (denom.type == DenominationType.NOTE) "${denom.value}" else "coin_${denom.value}"
                val currentCount = counts[key] ?: 0

                DenominationRow3D(
                    denomination = denom,
                    count = currentCount,
                    onCountChange = { newCount ->
                        viewModel.setCount(key, newCount)
                    },
                    depth = depth,
                    onHapticClick = {
                        viewModel.triggerHapticClick()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal Sheets & Dialogs
    if (showAiScannerSheet) {
        AiCashScannerSheet(
            viewModel = viewModel,
            onDismiss = { showAiScannerSheet = false }
        )
    }

    if (showGstCalculatorSheet) {
        GstAndChangeCalculatorSheet(
            initialCashAmount = netCashInHand,
            onDismiss = { showGstCalculatorSheet = false }
        )
    }

    if (showThemeSelectorSheet) {
        ThemeSelectorSheet(
            viewModel = viewModel,
            onDismiss = { showThemeSelectorSheet = false }
        )
    }

    if (showHistorySheet) {
        TallyHistorySheet(
            viewModel = viewModel,
            onDismiss = { showHistorySheet = false }
        )
    }

    // Save Tally Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = themeColors.surface,
            title = {
                Text("Save Daily Cash Tally", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column {
                    Text("Enter a title or note for this cash register count:", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = saveTitleInput,
                        onValueChange = { saveTitleInput = it },
                        placeholder = { Text("e.g. Day End Closing, Shop Register") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColors.accent,
                            unfocusedBorderColor = themeColors.highlightBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveCurrentTally(saveTitleInput)
                        saveTitleInput = ""
                        showSaveDialog = false
                    }
                ) {
                    Text("Save Record", fontWeight = FontWeight.Bold, color = themeColors.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // Clear Confirmation Dialog
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            containerColor = themeColors.surface,
            title = {
                Text("Reset Counter?", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Text("This will reset all note counts, coins, and extra adjustments back to zero.", color = Color.LightGray)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAll()
                        showClearConfirmation = false
                    }
                ) {
                    Text("Reset All", fontWeight = FontWeight.Bold, color = Color(0xFFEF5350))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }
}
