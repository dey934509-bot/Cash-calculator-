package com.example.service

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object IndianNumberToWords {

    private val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )

    private val tens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    private val hindiUnits = arrayOf(
        "", "एक", "दो", "तीन", "चार", "पाँच", "छः", "सात", "आठ", "नौ", "दस",
        "ग्यारह", "बारह", "तेरह", "चौदह", "पंद्रह", "सोलह", "सत्रह", "अठारह", "उन्नीस"
    )

    private val hindiTens = arrayOf(
        "", "", "बीस", "तीस", "चालीस", "पचास", "साठ", "सत्तर", "अस्सी", "नब्बे"
    )

    fun formatIndianCurrency(amount: Double): String {
        val longVal = amount.toLong()
        val decimalPart = Math.round((amount - longVal) * 100)
        val s = longVal.toString()
        val len = s.length

        val formatted = if (len <= 3) {
            s
        } else {
            val lastThree = s.substring(len - 3)
            val rest = s.substring(0, len - 3)
            val sb = StringBuilder()
            var count = 0
            for (i in rest.length - 1 downTo 0) {
                sb.insert(0, rest[i])
                count++
                if (count == 2 && i != 0) {
                    sb.insert(0, ",")
                    count = 0
                }
            }
            sb.toString() + "," + lastThree
        }

        return if (decimalPart > 0) {
            "₹ $formatted.${String.format(Locale.US, "%02d", decimalPart)}"
        } else {
            "₹ $formatted"
        }
    }

    fun convertToWordsEnglish(amount: Double): String {
        val longAmount = amount.toLong()
        if (longAmount == 0L) return "Zero Rupees Only"

        val sb = StringBuilder()

        val crore = longAmount / 10000000L
        var remainder = longAmount % 10000000L

        val lakh = remainder / 100000L
        remainder %= 100000L

        val thousand = remainder / 1000L
        remainder %= 1000L

        val hundred = remainder / 100L
        val tensAndUnits = remainder % 100L

        if (crore > 0) {
            sb.append(convertThreeDigits(crore.toInt())).append(" Crore ")
        }
        if (lakh > 0) {
            sb.append(convertThreeDigits(lakh.toInt())).append(" Lakh ")
        }
        if (thousand > 0) {
            sb.append(convertThreeDigits(thousand.toInt())).append(" Thousand ")
        }
        if (hundred > 0) {
            sb.append(convertThreeDigits(hundred.toInt())).append(" Hundred ")
        }
        if (tensAndUnits > 0) {
            if (sb.isNotEmpty()) sb.append("and ")
            sb.append(convertTwoDigits(tensAndUnits.toInt())).append(" ")
        }

        sb.append("Rupees Only")
        return sb.toString().trim()
    }

    private fun convertThreeDigits(n: Int): String {
        val sb = StringBuilder()
        val hundred = n / 100
        val rest = n % 100

        if (hundred > 0) {
            sb.append(units[hundred]).append(" Hundred ")
        }
        if (rest > 0) {
            sb.append(convertTwoDigits(rest))
        }
        return sb.toString().trim()
    }

    private fun convertTwoDigits(n: Int): String {
        return if (n < 20) {
            units[n]
        } else {
            val ten = n / 10
            val unit = n % 10
            if (unit > 0) {
                "${tens[ten]}-${units[unit]}"
            } else {
                tens[ten]
            }
        }
    }

    fun convertToWordsHindi(amount: Double): String {
        val longAmount = amount.toLong()
        if (longAmount == 0L) return "शून्य रुपये मात्र"

        val sb = StringBuilder()

        val crore = longAmount / 10000000L
        var remainder = longAmount % 10000000L

        val lakh = remainder / 100000L
        remainder %= 100000L

        val thousand = remainder / 1000L
        remainder %= 1000L

        val hundred = remainder / 100L
        val tensAndUnits = remainder % 100L

        if (crore > 0) {
            sb.append(convertHindiTwoDigits(crore.toInt())).append(" करोड़ ")
        }
        if (lakh > 0) {
            sb.append(convertHindiTwoDigits(lakh.toInt())).append(" लाख ")
        }
        if (thousand > 0) {
            sb.append(convertHindiTwoDigits(thousand.toInt())).append(" हज़ार ")
        }
        if (hundred > 0) {
            sb.append(hindiUnits[hundred.toInt()]).append(" सौ ")
        }
        if (tensAndUnits > 0) {
            sb.append(convertHindiTwoDigits(tensAndUnits.toInt())).append(" ")
        }

        sb.append("रुपये मात्र")
        return sb.toString().trim()
    }

    private fun convertHindiTwoDigits(n: Int): String {
        return if (n < 20) {
            hindiUnits[n]
        } else {
            val ten = n / 10
            val unit = n % 10
            if (unit > 0) {
                "${hindiTens[ten]} ${hindiUnits[unit]}"
            } else {
                hindiTens[ten]
            }
        }
    }
}
