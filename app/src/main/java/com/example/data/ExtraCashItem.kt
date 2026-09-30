package com.example.data

import java.util.UUID

data class ExtraCashItem(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val amount: Double,
    val isAddition: Boolean = true,
    val category: String = "Adjustment"
) {
    val signedAmount: Double
        get() = if (isAddition) amount else -amount
}
