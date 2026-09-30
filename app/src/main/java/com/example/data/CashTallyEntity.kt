package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_tallies")
data class CashTallyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val remark: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val totalDenominationAmount: Double,
    val totalExtraAdditions: Double,
    val totalHandDeductions: Double,
    val netCashInHand: Double,
    val expectedCash: Double,
    val difference: Double,
    val totalNotesCount: Int,
    val totalCoinsCount: Int,
    val breakdownJson: String,
    val extraItemsJson: String
)
