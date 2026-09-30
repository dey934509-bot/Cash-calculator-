package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.IndianNumberToWords
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDButtonVariant
import com.example.ui.components.ThreeDCard
import com.example.ui.components.ThreeDDepth
import com.example.ui.theme.Local3DThemeColors
import com.example.viewmodel.CashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraAndHandScreen(
    viewModel: CashViewModel,
    onBack: () -> Unit
) {
    androidx.activity.compose.BackHandler {
        onBack()
    }

    val themeColors = Local3DThemeColors.current
    val depth by viewModel.selectedDepth.collectAsStateWithLifecycle()
    val totalDenom by viewModel.totalDenominationAmount.collectAsStateWithLifecycle()
    val totalExtras by viewModel.totalExtraAdditions.collectAsStateWithLifecycle()
    val totalDeductions by viewModel.totalHandDeductions.collectAsStateWithLifecycle()
    val netCash by viewModel.netCashInHand.collectAsStateWithLifecycle()
    val expectedCash by viewModel.expectedCash.collectAsStateWithLifecycle()
    val drawerDiff by viewModel.drawerDifference.collectAsStateWithLifecycle()
    val extraItems by viewModel.extraItems.collectAsStateWithLifecycle()

    var newLabel by remember { mutableStateOf("") }
    var newAmountText by remember { mutableStateOf("") }
    var isAdditionMode by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Adjustment") }

    var expectedCashInput by remember(expectedCash) {
        mutableStateOf(if (expectedCash == 0.0) "" else expectedCash.toLong().toString())
    }

    val presetCategories = listOf("Drawer Float", "Petty Cash", "UPI/Bank", "Staff Advance", "Tips", "Shortage/Overage")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cash in Hand & Extra Additions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = themeColors.accent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColors.surface
                )
            )
        },
        containerColor = themeColors.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Reconciliation Overview Card
            item {
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = depth,
                    cornerRadius = 18.dp,
                    borderColor = themeColors.accent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DRAWER CASH IN HAND SUMMARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Counted Physical Cash:", fontSize = 13.sp, color = Color.LightGray)
                            Text(
                                IndianNumberToWords.formatIndianCurrency(totalDenom),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (totalExtras > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("(+) Extra Cash Inflows:", fontSize = 13.sp, color = Color(0xFF66BB6A))
                                Text(
                                    "+ " + IndianNumberToWords.formatIndianCurrency(totalExtras),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF66BB6A)
                                )
                            }
                        }

                        if (totalDeductions > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("(-) Hand Cash Outflows:", fontSize = 13.sp, color = Color(0xFFEF5350))
                                Text(
                                    "- " + IndianNumberToWords.formatIndianCurrency(totalDeductions),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF5350)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.15f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Net Cash in Hand:",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                IndianNumberToWords.formatIndianCurrency(netCash),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = themeColors.accent
                            )
                        }
                    }
                }
            }

            // 2. Expected Register Cash & Difference Reconciliation
            item {
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = depth,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DRAWER RECONCILIATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accentSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = expectedCashInput,
                            onValueChange = { newVal ->
                                val clean = newVal.filter { it.isDigit() }
                                expectedCashInput = clean
                                val parsed = clean.toDoubleOrNull() ?: 0.0
                                viewModel.setExpectedCash(parsed)
                            },
                            label = { Text("Expected Drawer Balance / Register Total (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColors.accent,
                                unfocusedBorderColor = themeColors.highlightBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        if (expectedCash > 0) {
                            Spacer(modifier = Modifier.height(12.dp))

                            val isBalanced = drawerDiff == 0.0
                            val isExcess = drawerDiff > 0
                            val statusBg = when {
                                isBalanced -> Color(0xFF1B5E20)
                                isExcess -> Color(0xFFE65100)
                                else -> Color(0xFFB71C1C)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(statusBg)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isBalanced -> Icons.Default.CheckCircle
                                            isExcess -> Icons.Default.Warning
                                            else -> Icons.Default.Error
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = when {
                                                isBalanced -> "PERFECT MATCH! (Drawer Balanced)"
                                                isExcess -> "EXCESS CASH (+${IndianNumberToWords.formatIndianCurrency(drawerDiff)})"
                                                else -> "SHORTAGE / DEFICIT (${IndianNumberToWords.formatIndianCurrency(drawerDiff)})"
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = when {
                                                isBalanced -> "Counted cash exactly equals expected amount."
                                                isExcess -> "Physical drawer has more cash than system book."
                                                else -> "Physical cash is less than expected system balance."
                                            },
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Add Extra Cash / Hand Adjustment
            item {
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = depth,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ADD EXTRA ITEM / HAND ADJUSTMENT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Type Toggle (+ Extra Addition / - Hand Outflow)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isAdditionMode) Color(0xFF2E7D32) else themeColors.surfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isAdditionMode) Color(0xFF81C784) else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { isAdditionMode = true }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Extra Cash (+)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isAdditionMode) Color(0xFFC62828) else themeColors.surfaceVariant)
                                    .border(
                                        1.dp,
                                        if (!isAdditionMode) Color(0xFFEF9A9A) else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { isAdditionMode = false }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Hand Outflow (-)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetCategories.take(3).forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedCategory == cat) themeColors.accent.copy(alpha = 0.2f) else themeColors.surfaceVariant)
                                        .border(
                                            1.dp,
                                            if (selectedCategory == cat) themeColors.accent else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedCategory = cat
                                            newLabel = cat
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        color = if (selectedCategory == cat) themeColors.accent else Color.LightGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newLabel,
                            onValueChange = { newLabel = it },
                            label = { Text("Item Remark (e.g. Petty Cash, UPI Receipt)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColors.accent,
                                unfocusedBorderColor = themeColors.highlightBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newAmountText,
                            onValueChange = { newAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Amount (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColors.accent,
                                unfocusedBorderColor = themeColors.highlightBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ThreeDButton(
                            onClick = {
                                val amt = newAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    viewModel.addExtraItem(
                                        label = newLabel,
                                        amount = amt,
                                        isAddition = isAdditionMode,
                                        category = selectedCategory
                                    )
                                    newLabel = ""
                                    newAmountText = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            variant = if (isAdditionMode) ThreeDButtonVariant.GOLD else ThreeDButtonVariant.DANGER,
                            enabled = (newAmountText.toDoubleOrNull() ?: 0.0) > 0
                        ) {
                            Text(
                                text = if (isAdditionMode) "+ Add Extra Cash" else "- Record Hand Outflow",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // 4. Current List of Adjustments
            item {
                Text(
                    text = "ITEMIZED ADJUSTMENTS (${extraItems.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }

            if (extraItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColors.surfaceVariant.copy(alpha = 0.5f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No extra additions or hand outflows recorded yet.",
                            fontSize = 12.sp,
                            color = Color.LightGray.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(extraItems, key = { it.id }) { item ->
                    ThreeDCard(
                        modifier = Modifier.fillMaxWidth(),
                        depth = ThreeDDepth.MILD,
                        cornerRadius = 12.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (item.isAddition) Color(0xFF2E7D32).copy(alpha = 0.3f)
                                            else Color(0xFFC62828).copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (item.isAddition) Icons.Default.Add else Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = if (item.isAddition) Color(0xFF81C784) else Color(0xFFEF9A9A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = item.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = item.category,
                                        fontSize = 11.sp,
                                        color = Color.LightGray.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = (if (item.isAddition) "+ " else "- ") + IndianNumberToWords.formatIndianCurrency(item.amount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isAddition) Color(0xFF81C784) else Color(0xFFEF9A9A)
                                )

                                IconButton(
                                    onClick = { viewModel.removeExtraItem(item.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete item",
                                        tint = Color.LightGray.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
