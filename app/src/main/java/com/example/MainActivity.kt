package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurahEntity
import com.example.ui.components.MiniAudioPlayerBar
import com.example.ui.screens.*
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.theme.NoorIslamicTheme
import com.example.ui.viewmodel.NoorViewModel

enum class MainTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
  HOME("হোম", Icons.Filled.Home, Icons.Outlined.Home),
  HADITH("হাদীস", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
  MASALA("মাসআলা", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance),
  QURAN("কুরআন", Icons.Filled.AutoStories, Icons.Outlined.AutoStories),
  SETTINGS("সেটিংস", Icons.Filled.Settings, Icons.Outlined.Settings)
}

enum class SubScreen {
  NONE,
  SEARCH,
  BOOKMARK,
  AUDIO_PLAYER,
  SURAH_DETAIL,
  ADMIN
}

class MainActivity : ComponentActivity() {

  private val viewModel: NoorViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val settings by viewModel.settings.collectAsState()
      val darkTheme = if (settings.useSystemTheme) isSystemInDarkTheme() else settings.isDarkMode

      NoorIslamicTheme(darkTheme = darkTheme) {
        NoorIslamicApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun NoorIslamicApp(viewModel: NoorViewModel) {
  var currentTab by remember { mutableStateOf(MainTab.HOME) }
  var subScreen by remember { mutableStateOf(SubScreen.NONE) }
  var selectedSurah by remember { mutableStateOf<SurahEntity?>(null) }

  val audioState by viewModel.audioState.collectAsState()

  // Handle system back button
  BackHandler(enabled = subScreen != SubScreen.NONE) {
    subScreen = SubScreen.NONE
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Floating Mini Player (visible when audio is loaded/playing and not on full audio player screen)
        if (audioState.currentSurah != null && subScreen != SubScreen.AUDIO_PLAYER) {
          MiniAudioPlayerBar(
            audioState = audioState,
            onPlayPauseClick = { viewModel.togglePlayPauseAudio() },
            onOpenPlayerClick = { subScreen = SubScreen.AUDIO_PLAYER }
          )
        }

        // Bottom Navigation Bar (visible on primary tab screens)
        if (subScreen == SubScreen.NONE) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.testTag("bottom_navigation_bar")
          ) {
            MainTab.values().forEach { tab ->
              val isSelected = currentTab == tab
              NavigationBarItem(
                selected = isSelected,
                onClick = { currentTab = tab },
                icon = {
                  Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.title,
                    tint = if (isSelected) IslamicGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                },
                label = {
                  Text(
                    text = tab.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (isSelected) IslamicGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (subScreen == SubScreen.NONE) {
        when (currentTab) {
          MainTab.HOME -> {
            HomeScreen(
              viewModel = viewModel,
              onNavigateToTab = { route ->
                when (route) {
                  "HADITH" -> currentTab = MainTab.HADITH
                  "MASALA" -> currentTab = MainTab.MASALA
                  "QURAN" -> currentTab = MainTab.QURAN
                  "SETTINGS" -> currentTab = MainTab.SETTINGS
                }
              },
              onOpenSurah = { surah ->
                selectedSurah = surah
                viewModel.selectSurah(surah)
                subScreen = SubScreen.SURAH_DETAIL
              },
              onOpenAudioPlayer = { surah ->
                if (surah != null) {
                  viewModel.playSurah(surah)
                }
                subScreen = SubScreen.AUDIO_PLAYER
              },
              onOpenSearch = { subScreen = SubScreen.SEARCH },
              onOpenBookmarks = { subScreen = SubScreen.BOOKMARK },
              onOpenSettings = { currentTab = MainTab.SETTINGS },
              onOpenAdmin = { subScreen = SubScreen.ADMIN }
            )
          }

          MainTab.HADITH -> {
            HadithScreen(
              viewModel = viewModel,
              onOpenSearch = { subScreen = SubScreen.SEARCH },
              onOpenBookmarks = { subScreen = SubScreen.BOOKMARK }
            )
          }

          MainTab.MASALA -> {
            MasalaScreen(
              viewModel = viewModel,
              onOpenSearch = { subScreen = SubScreen.SEARCH },
              onOpenBookmarks = { subScreen = SubScreen.BOOKMARK }
            )
          }

          MainTab.QURAN -> {
            QuranScreen(
              viewModel = viewModel,
              onOpenSurah = { surah ->
                selectedSurah = surah
                viewModel.selectSurah(surah)
                subScreen = SubScreen.SURAH_DETAIL
              },
              onPlayAudio = { surah ->
                viewModel.playSurah(surah)
                subScreen = SubScreen.AUDIO_PLAYER
              },
              onOpenSearch = { subScreen = SubScreen.SEARCH },
              onOpenBookmarks = { subScreen = SubScreen.BOOKMARK }
            )
          }

          MainTab.SETTINGS -> {
            SettingsScreen(
              viewModel = viewModel,
              onOpenAdminPanel = { subScreen = SubScreen.ADMIN }
            )
          }
        }
      } else {
        // Sub-screens
        when (subScreen) {
          SubScreen.SEARCH -> {
            SearchScreen(
              viewModel = viewModel,
              onBackClick = { subScreen = SubScreen.NONE }
            )
          }

          SubScreen.BOOKMARK -> {
            BookmarkScreen(
              viewModel = viewModel,
              onBackClick = { subScreen = SubScreen.NONE }
            )
          }

          SubScreen.AUDIO_PLAYER -> {
            AudioPlayerScreen(
              viewModel = viewModel,
              onBackClick = { subScreen = SubScreen.NONE }
            )
          }

          SubScreen.SURAH_DETAIL -> {
            selectedSurah?.let { surah ->
              SurahDetailScreen(
                surah = surah,
                viewModel = viewModel,
                onBackClick = { subScreen = SubScreen.NONE },
                onPlayAudio = {
                  viewModel.playSurah(surah)
                  subScreen = SubScreen.AUDIO_PLAYER
                }
              )
            } ?: run {
              subScreen = SubScreen.NONE
            }
          }

          SubScreen.ADMIN -> {
            AdminScreen(
              viewModel = viewModel,
              onBackClick = { subScreen = SubScreen.NONE }
            )
          }

          SubScreen.NONE -> {}
        }
      }
    }
  }
}
