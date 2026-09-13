package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AdminScreen
import com.example.ui.theme.NoorIslamicTheme
import com.example.ui.viewmodel.NoorViewModel

/**
 * নূর ইসলামিক - স্বতন্ত্র এডমিন অ্যাক্টিভিটি (Admin Activity)
 * এই ফাইলের মাধ্যমে সম্পূর্ণ আলাদাভাবে এডমিন প্যানেল চালানো যায়।
 * এডমিন পিন: 7860
 */
class AdminActivity : ComponentActivity() {

  private val viewModel: NoorViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val settings by viewModel.settings.collectAsState()
      val darkTheme = if (settings.useSystemTheme) isSystemInDarkTheme() else settings.isDarkMode

      NoorIslamicTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
          AdminScreen(
            viewModel = viewModel,
            onBackClick = { finish() }
          )
        }
      }
    }
  }
}
