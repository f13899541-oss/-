package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.IslamicSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("নূর ইসলামিক", appName)
  }

  @Test
  fun `verify initial hadith seed data authenticity`() {
    val hadiths = IslamicSeedData.initialHadiths
    assertTrue("Hadith list should not be empty", hadiths.isNotEmpty())
    assertTrue("Should include Sahih Bukhari hadiths", hadiths.any { it.bookName.contains("বুখারী") })
    assertTrue("Hadiths should have Arabic text", hadiths.all { it.arabicText.isNotBlank() })
    assertTrue("Hadiths should have Bengali translation", hadiths.all { it.banglaText.isNotBlank() })
    assertTrue("Hadiths should have verified references", hadiths.all { it.reference.isNotBlank() })
  }

  @Test
  fun `verify initial masala seed data`() {
    val masalas = IslamicSeedData.initialMasalas
    assertTrue("Masalas list should not be empty", masalas.isNotEmpty())
    assertTrue("Should include prayer masalas", masalas.any { it.category == "নামাজের মাসআলা" })
    assertTrue("Should include fasting masalas", masalas.any { it.category == "রোজার মাসআলা" })
    assertTrue("Masalas should have dalil references", masalas.all { it.dalil.isNotBlank() })
  }

  @Test
  fun `verify surah list contains Surah Al-Mulk and Surah Ar-Rahman`() {
    val surahs = IslamicSeedData.initialSurahs
    assertTrue("Surah list should have Surah Al-Mulk (67)", surahs.any { it.number == 67 })
    assertTrue("Surah list should have Surah Ar-Rahman (55)", surahs.any { it.number == 55 })
    assertTrue("All surahs should have audio URLs", surahs.all { it.audioUrl.isNotBlank() })
  }
}
