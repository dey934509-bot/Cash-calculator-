package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.IndianNumberToWords
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CashCount 3D", appName)
  }

  @Test
  fun `indian currency formatting and words`() {
    val amount = 125450.0
    val formatted = IndianNumberToWords.formatIndianCurrency(amount)
    assertEquals("₹ 1,25,450", formatted)

    val wordsEng = IndianNumberToWords.convertToWordsEnglish(amount)
    assertTrue(wordsEng.contains("One Lakh"))
    assertTrue(wordsEng.contains("Twenty-Five Thousand"))
  }
}
