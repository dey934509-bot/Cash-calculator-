package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CashDatabase
import com.example.data.CashRepository
import com.example.data.CashTallyEntity
import com.example.data.ExtraCashItem
import com.example.data.IndianCurrencyCatalog
import com.example.service.AiDetectedCash
import com.example.service.GeminiCashScannerService
import com.example.service.IndianNumberToWords
import com.example.service.SoundAndHapticHelper
import com.example.ui.components.ThreeDDepth
import com.example.ui.theme.App3DTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CashViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CashRepository
    private val soundHelper = SoundAndHapticHelper(application)
    private val aiService = GeminiCashScannerService()

    init {
        val dao = CashDatabase.getDatabase(application).cashTallyDao()
        repository = CashRepository(dao)
    }

    val savedTallies = repository.allTallies.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Denomination counts map key -> count (e.g. "2000" -> 2, "500" -> 10, "coin_10" -> 5)
    private val _counts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counts: StateFlow<Map<String, Int>> = _counts.asStateFlow()

    // Extra additions and hand adjustments
    private val _extraItems = MutableStateFlow<List<ExtraCashItem>>(emptyList())
    val extraItems: StateFlow<List<ExtraCashItem>> = _extraItems.asStateFlow()

    // Expected cash in hand / drawer (for Cash in Hand Drawer reconciliation)
    private val _expectedCash = MutableStateFlow<Double>(0.0)
    val expectedCash: StateFlow<Double> = _expectedCash.asStateFlow()

    // 3D theme settings
    private val _selectedTheme = MutableStateFlow(App3DTheme.ROYAL_GOLD)
    val selectedTheme: StateFlow<App3DTheme> = _selectedTheme.asStateFlow()

    private val _selectedDepth = MutableStateFlow(ThreeDDepth.STANDARD)
    val selectedDepth: StateFlow<ThreeDDepth> = _selectedDepth.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    // AI Scanner state
    private val _isAiScanning = MutableStateFlow(false)
    val isAiScanning: StateFlow<Boolean> = _isAiScanning.asStateFlow()

    private val _aiScanResult = MutableStateFlow<AiDetectedCash?>(null)
    val aiScanResult: StateFlow<AiDetectedCash?> = _aiScanResult.asStateFlow()

    private val _aiScanError = MutableStateFlow<String?>(null)
    val aiScanError: StateFlow<String?> = _aiScanError.asStateFlow()

    // Combined calculations
    val totalDenominationAmount = _counts.combine(_extraItems) { countsMap, _ ->
        var total = 0.0
        for (denom in IndianCurrencyCatalog.denominations) {
            val key = if (denom.type == com.example.data.DenominationType.NOTE) "${denom.value}" else "coin_${denom.value}"
            val count = countsMap[key] ?: 0
            total += denom.value.toLong() * count
        }
        total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalNotesCount = _counts.combine(_extraItems) { countsMap, _ ->
        var total = 0
        for (denom in IndianCurrencyCatalog.denominations) {
            if (denom.type == com.example.data.DenominationType.NOTE) {
                total += countsMap["${denom.value}"] ?: 0
            }
        }
        total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCoinsCount = _counts.combine(_extraItems) { countsMap, _ ->
        var total = 0
        for (denom in IndianCurrencyCatalog.denominations) {
            if (denom.type == com.example.data.DenominationType.COIN) {
                total += countsMap["coin_${denom.value}"] ?: 0
            }
        }
        total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalExtraAdditions = _extraItems.combine(_counts) { extras, _ ->
        extras.filter { it.isAddition }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalHandDeductions = _extraItems.combine(_counts) { extras, _ ->
        extras.filter { !it.isAddition }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netCashInHand = combine(totalDenominationAmount, totalExtraAdditions, totalHandDeductions) { denomAmt, extras, deductions ->
        denomAmt + extras - deductions
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val drawerDifference = combine(netCashInHand, _expectedCash) { netCash, expected ->
        netCash - expected
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setCount(key: String, count: Int) {
        val current = _counts.value.toMutableMap()
        if (count <= 0) {
            current.remove(key)
        } else {
            current[key] = count
        }
        _counts.value = current
    }

    fun clearAll() {
        _counts.value = emptyMap()
        _extraItems.value = emptyList()
        _expectedCash.value = 0.0
        soundHelper.triggerHeavyFeedback(_hapticsEnabled.value)
    }

    fun triggerHapticClick() {
        soundHelper.triggerClickFeedback(_hapticsEnabled.value)
    }

    fun speakTotal(inHindi: Boolean) {
        val total = netCashInHand.value
        val textToSpeak = if (inHindi) {
            "कुल राशि " + IndianNumberToWords.convertToWordsHindi(total)
        } else {
            "Total Cash in Hand is " + IndianNumberToWords.convertToWordsEnglish(total)
        }
        soundHelper.speakTotal(textToSpeak, inHindi)
    }

    fun addExtraItem(label: String, amount: Double, isAddition: Boolean, category: String) {
        val item = ExtraCashItem(
            label = label.ifBlank { if (isAddition) "Extra Addition" else "Hand Outflow" },
            amount = amount,
            isAddition = isAddition,
            category = category
        )
        _extraItems.value = _extraItems.value + item
        triggerHapticClick()
    }

    fun removeExtraItem(id: String) {
        _extraItems.value = _extraItems.value.filter { it.id != id }
        triggerHapticClick()
    }

    fun setExpectedCash(amount: Double) {
        _expectedCash.value = amount
    }

    fun setTheme(theme: App3DTheme) {
        _selectedTheme.value = theme
        triggerHapticClick()
    }

    fun setDepth(depth: ThreeDDepth) {
        _selectedDepth.value = depth
        triggerHapticClick()
    }

    fun toggleHaptics() {
        _hapticsEnabled.value = !_hapticsEnabled.value
    }

    fun scanCashImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAiScanning.value = true
            _aiScanError.value = null
            _aiScanResult.value = null
            val result = aiService.analyzeCashImage(bitmap)
            _isAiScanning.value = false
            if (result.isSuccess) {
                _aiScanResult.value = result.getOrNull()
            } else {
                _aiScanError.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to scan cash"
            }
        }
    }

    fun applyAiScanToCounter(detected: AiDetectedCash) {
        val updated = _counts.value.toMutableMap()
        for ((k, v) in detected.counts) {
            if (v > 0) {
                updated[k] = (updated[k] ?: 0) + v
            }
        }
        _counts.value = updated
        _aiScanResult.value = null
        soundHelper.triggerHeavyFeedback(_hapticsEnabled.value)
    }

    fun clearAiScanResult() {
        _aiScanResult.value = null
        _aiScanError.value = null
    }

    fun saveCurrentTally(title: String, remark: String = "") {
        viewModelScope.launch {
            val countsJson = JSONObject(_counts.value).toString()
            val extraArray = JSONArray()
            for (item in _extraItems.value) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("label", item.label)
                    put("amount", item.amount)
                    put("isAddition", item.isAddition)
                    put("category", item.category)
                }
                extraArray.put(obj)
            }

            val entity = CashTallyEntity(
                title = title.ifBlank { "Cash Tally " + SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date()) },
                remark = remark,
                timestamp = System.currentTimeMillis(),
                totalDenominationAmount = totalDenominationAmount.value,
                totalExtraAdditions = totalExtraAdditions.value,
                totalHandDeductions = totalHandDeductions.value,
                netCashInHand = netCashInHand.value,
                expectedCash = _expectedCash.value,
                difference = drawerDifference.value,
                totalNotesCount = totalNotesCount.value,
                totalCoinsCount = totalCoinsCount.value,
                breakdownJson = countsJson,
                extraItemsJson = extraArray.toString()
            )

            repository.insertTally(entity)
            soundHelper.triggerHeavyFeedback(_hapticsEnabled.value)
        }
    }

    fun deleteTally(id: Long) {
        viewModelScope.launch {
            repository.deleteTally(id)
            triggerHapticClick()
        }
    }

    fun clearAllTallies() {
        viewModelScope.launch {
            repository.clearAll()
            triggerHapticClick()
        }
    }

    fun generateReceiptText(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date())
        val sb = StringBuilder()

        sb.append("================================\n")
        sb.append("    INDIAN CASH TALLY SLIP\n")
        sb.append("================================\n")
        sb.append("Date: $dateStr\n")
        sb.append("--------------------------------\n")
        sb.append("DENOMINATION BREAKDOWN:\n")

        for (denom in IndianCurrencyCatalog.denominations) {
            val key = if (denom.type == com.example.data.DenominationType.NOTE) "${denom.value}" else "coin_${denom.value}"
            val count = _counts.value[key] ?: 0
            if (count > 0) {
                val lineTotal = denom.value.toLong() * count
                sb.append(String.format(Locale.US, "%-8s x %-4d = %s\n", denom.label, count, IndianNumberToWords.formatIndianCurrency(lineTotal.toDouble())))
            }
        }

        sb.append("--------------------------------\n")
        sb.append("Total Notes: ${totalNotesCount.value}\n")
        sb.append("Total Coins: ${totalCoinsCount.value}\n")
        sb.append("Denominations Total: ${IndianNumberToWords.formatIndianCurrency(totalDenominationAmount.value)}\n")

        if (_extraItems.value.isNotEmpty()) {
            sb.append("--------------------------------\n")
            sb.append("EXTRA ADDITIONS & HAND ADJUSTMENTS:\n")
            for (item in _extraItems.value) {
                val sign = if (item.isAddition) "(+)" else "(-)"
                sb.append("$sign ${item.label}: ${IndianNumberToWords.formatIndianCurrency(item.amount)}\n")
            }
        }

        if (_expectedCash.value > 0) {
            sb.append("--------------------------------\n")
            sb.append("Expected Drawer Cash: ${IndianNumberToWords.formatIndianCurrency(_expectedCash.value)}\n")
            val diff = drawerDifference.value
            val diffLabel = when {
                diff == 0.0 -> "PERFECT MATCH (₹ 0.00)"
                diff > 0 -> "EXCESS (+${IndianNumberToWords.formatIndianCurrency(diff)})"
                else -> "DEFICIT (${IndianNumberToWords.formatIndianCurrency(diff)})"
            }
            sb.append("Drawer Difference: $diffLabel\n")
        }

        sb.append("================================\n")
        sb.append("NET CASH IN HAND: ${IndianNumberToWords.formatIndianCurrency(netCashInHand.value)}\n")
        sb.append("In Words: ${IndianNumberToWords.convertToWordsEnglish(netCashInHand.value)}\n")
        sb.append("================================\n")
        sb.append("Generated with CashCount 3D\n")

        return sb.toString()
    }

    override fun onCleared() {
        super.onCleared()
        soundHelper.shutdown()
    }
}
