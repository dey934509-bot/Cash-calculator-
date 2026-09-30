package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.IndianNumberToWords
import com.example.ui.components.ThreeDCard
import com.example.ui.components.ThreeDDepth
import com.example.ui.theme.Local3DThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstAndChangeCalculatorSheet(
    initialCashAmount: Double,
    onDismiss: () -> Unit
) {
    val themeColors = Local3DThemeColors.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // GST States
    var gstAmountInput by remember { mutableStateOf(if (initialCashAmount > 0) initialCashAmount.toLong().toString() else "") }
    var selectedGstRate by remember { mutableDoubleStateOf(18.0) }
    var isAddingGst by remember { mutableStateOf(true) }

    // Change Dispenser States
    var billAmountInput by remember { mutableStateOf("") }
    var paidAmountInput by remember { mutableStateOf("") }

    val gstRates = listOf(3.0, 5.0, 12.0, 18.0, 28.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Tools & Calculator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = themeColors.surfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = themeColors.accent
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "GST Calculator",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) themeColors.accent else Color.LightGray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Change Dispenser",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) themeColors.accent else Color.LightGray
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // GST CALCULATOR TAB
                val base = gstAmountInput.toDoubleOrNull() ?: 0.0
                val gstValue = if (isAddingGst) {
                    (base * selectedGstRate) / 100.0
                } else {
                    base - (base / (1 + selectedGstRate / 100.0))
                }
                val finalTotal = if (isAddingGst) base + gstValue else base - gstValue

                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = ThreeDDepth.STANDARD,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Toggle: Add GST vs Remove GST
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isAddingGst) themeColors.accent else themeColors.surfaceVariant)
                                    .clickable { isAddingGst = true }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Add GST (+)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAddingGst) Color.Black else Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isAddingGst) themeColors.accent else themeColors.surfaceVariant)
                                    .clickable { isAddingGst = false }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Extract GST (-)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isAddingGst) Color.Black else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = gstAmountInput,
                            onValueChange = { gstAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Base Cash Amount (₹)") },
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

                        // GST Rate selection
                        Text("Select GST Slab Rate:", fontSize = 12.sp, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            gstRates.forEach { rate ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedGstRate == rate) themeColors.accent else themeColors.surfaceVariant)
                                        .border(
                                            1.dp,
                                            if (selectedGstRate == rate) themeColors.accent else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedGstRate = rate }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "${rate.toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedGstRate == rate) Color.Black else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.15f)))
                        Spacer(modifier = Modifier.height(14.dp))

                        // GST Split Breakdown
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("CGST (${selectedGstRate / 2}%):", fontSize = 12.sp, color = Color.LightGray)
                            Text(IndianNumberToWords.formatIndianCurrency(gstValue / 2.0), fontSize = 13.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SGST (${selectedGstRate / 2}%):", fontSize = 12.sp, color = Color.LightGray)
                            Text(IndianNumberToWords.formatIndianCurrency(gstValue / 2.0), fontSize = 13.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total GST Tax:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = themeColors.accent)
                            Text(IndianNumberToWords.formatIndianCurrency(gstValue), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = themeColors.accent)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.15f)))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isAddingGst) "Total with GST:" else "Pre-GST Amount:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(IndianNumberToWords.formatIndianCurrency(finalTotal), fontSize = 20.sp, fontWeight = FontWeight.Black, color = themeColors.accent)
                        }
                    }
                }
            } else {
                // CHANGE DISPENSER TAB
                val bill = billAmountInput.toDoubleOrNull() ?: 0.0
                val paid = paidAmountInput.toDoubleOrNull() ?: 0.0
                val change = (paid - bill).coerceAtLeast(0.0)

                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = ThreeDDepth.STANDARD,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = billAmountInput,
                            onValueChange = { billAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Bill Amount (₹)") },
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

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = paidAmountInput,
                            onValueChange = { paidAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Customer Paid / Tender (₹)") },
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

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Change to Return:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                IndianNumberToWords.formatIndianCurrency(change),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = if (change > 0) Color(0xFF81C784) else Color.White
                            )
                        }

                        if (change > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Recommended Note Dispenser:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.accent
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Calculate optimal notes
                            val denoms = listOf(500, 200, 100, 50, 20, 10, 5, 2, 1)
                            var rem = change.toLong()
                            val dispensed = mutableListOf<Pair<Int, Long>>()
                            for (d in denoms) {
                                if (rem >= d) {
                                    val count = rem / d
                                    dispensed.add(Pair(d, count))
                                    rem %= d
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                dispensed.forEach { (denom, count) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("₹$denom", fontSize = 12.sp, color = Color.LightGray)
                                        Text("$count pcs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
