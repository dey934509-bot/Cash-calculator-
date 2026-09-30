package com.example.data

import androidx.compose.ui.graphics.Color

enum class DenominationType {
    NOTE,
    COIN
}

data class CashDenomination(
    val value: Int,
    val type: DenominationType,
    val label: String,
    val primaryColor: Color,
    val accentColor: Color,
    val textColor: Color = Color.White
)

object IndianCurrencyCatalog {
    val denominations: List<CashDenomination> = listOf(
        CashDenomination(
            value = 2000,
            type = DenominationType.NOTE,
            label = "₹2000",
            primaryColor = Color(0xFFC2185B), // Magenta
            accentColor = Color(0xFFE91E63)
        ),
        CashDenomination(
            value = 500,
            type = DenominationType.NOTE,
            label = "₹500",
            primaryColor = Color(0xFF455A64), // Stone Grey
            accentColor = Color(0xFF607D8B)
        ),
        CashDenomination(
            value = 200,
            type = DenominationType.NOTE,
            label = "₹200",
            primaryColor = Color(0xFFE65100), // Bright Orange-Yellow
            accentColor = Color(0xFFF57C00)
        ),
        CashDenomination(
            value = 100,
            type = DenominationType.NOTE,
            label = "₹100",
            primaryColor = Color(0xFF3949AB), // Lavender-Blue
            accentColor = Color(0xFF5C6BC0)
        ),
        CashDenomination(
            value = 50,
            type = DenominationType.NOTE,
            label = "₹50",
            primaryColor = Color(0xFF00838F), // Cyan
            accentColor = Color(0xFF00ACC1)
        ),
        CashDenomination(
            value = 20,
            type = DenominationType.NOTE,
            label = "₹20",
            primaryColor = Color(0xFF558B2F), // Greenish Yellow
            accentColor = Color(0xFF689F38)
        ),
        CashDenomination(
            value = 10,
            type = DenominationType.NOTE,
            label = "₹10",
            primaryColor = Color(0xFF5D4037), // Chocolate Brown
            accentColor = Color(0xFF795548)
        ),
        CashDenomination(
            value = 20,
            type = DenominationType.COIN,
            label = "₹20 Coin",
            primaryColor = Color(0xFFC5A059), // Brass Gold Rim
            accentColor = Color(0xFFD4AF37)
        ),
        CashDenomination(
            value = 10,
            type = DenominationType.COIN,
            label = "₹10 Coin",
            primaryColor = Color(0xFFB8860B), // Bronze Rim
            accentColor = Color(0xFFDAA520)
        ),
        CashDenomination(
            value = 5,
            type = DenominationType.COIN,
            label = "₹5 Coin",
            primaryColor = Color(0xFFD4AF37), // Nickel Brass Gold
            accentColor = Color(0xFFE5C158)
        ),
        CashDenomination(
            value = 2,
            type = DenominationType.COIN,
            label = "₹2 Coin",
            primaryColor = Color(0xFF78909C), // Steel Silver
            accentColor = Color(0xFF90A4AE)
        ),
        CashDenomination(
            value = 1,
            type = DenominationType.COIN,
            label = "₹1 Coin",
            primaryColor = Color(0xFF607D8B), // Steel Silver
            accentColor = Color(0xFF78909C)
        )
    )
}
