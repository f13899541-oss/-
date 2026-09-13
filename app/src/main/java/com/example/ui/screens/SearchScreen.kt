package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HadithCard
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.MasalaCard
import com.example.ui.components.copyToClipboard
import com.example.ui.components.shareText
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun SearchScreen(
  viewModel: NoorViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val searchQuery by viewModel.searchQuery.collectAsState()
  val searchResults by viewModel.searchResults.collectAsState()
  val settings by viewModel.settings.collectAsState()

  var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Hadith, 2: Masala, 3: Quran
  val filterTabs = listOf("সব", "হাদীস", "মাসআলা", "কুরআন")

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "অনুসন্ধান",
        subtitle = "হাদীস, মাসআলা ও কুরআনের আয়াত খুঁজুন",
        showBack = true,
        onBackClick = onBackClick
      )
    },
    modifier = modifier.fillMaxSize().testTag("search_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("কী খুঁজতে চান? যেমন: নামাজ, রোজা, নিয়ত, জান্নাত...") },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGreenPrimary)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = IslamicGreenPrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("search_input_field")
      )

      // Tab selector
      TabRow(
        selectedTabIndex = selectedFilterTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = IslamicGreenPrimary
      ) {
        filterTabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedFilterTab == index,
            onClick = { selectedFilterTab = index },
            text = { Text(title, fontWeight = if (selectedFilterTab == index) FontWeight.Bold else FontWeight.Normal) }
          )
        }
      }

      // Results
      val showHadiths = selectedFilterTab == 0 || selectedFilterTab == 1
      val showMasalas = selectedFilterTab == 0 || selectedFilterTab == 2
      val showAyahs = selectedFilterTab == 0 || selectedFilterTab == 3

      val totalResults = (if (showHadiths) searchResults.hadiths.size else 0) +
          (if (showMasalas) searchResults.masalas.size else 0) +
          (if (showAyahs) searchResults.ayahs.size else 0)

      if (searchQuery.isBlank()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = IslamicGreenPrimary.copy(alpha = 0.5f),
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "হাদীস, ফিকহি মাসআলা অথবা কুরআনের আয়াত খুঁজতে উপরে লিখুন",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            )
          }
        }
      } else if (totalResults == 0) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "\"$searchQuery\" সম্পর্কিত কোনো ফলাফল পাওয়া যায়নি",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Hadiths
          if (showHadiths && searchResults.hadiths.isNotEmpty()) {
            item {
              Text(
                text = "হাদীস ফলাফল (${searchResults.hadiths.size} টি)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = IslamicGreenPrimary
                )
              )
            }
            items(searchResults.hadiths, key = { "h_${it.id}" }) { hadith ->
              HadithCard(
                hadith = hadith,
                banglaFontSize = settings.banglaFontSize,
                arabicFontSize = settings.arabicFontSize,
                onBookmarkClick = { viewModel.toggleFavoriteHadith(hadith) }
              )
            }
          }

          // Masalas
          if (showMasalas && searchResults.masalas.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "মাসআলা ফলাফল (${searchResults.masalas.size} টি)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = IslamicGoldDark
                )
              )
            }
            items(searchResults.masalas, key = { "m_${it.id}" }) { masala ->
              MasalaCard(
                masala = masala,
                banglaFontSize = settings.banglaFontSize,
                onBookmarkClick = { viewModel.toggleFavoriteMasala(masala) }
              )
            }
          }

          // Ayahs
          if (showAyahs && searchResults.ayahs.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "কুরআনের আয়াত ফলাফল (${searchResults.ayahs.size} টি)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = IslamicGreenPrimary
                )
              )
            }
            items(searchResults.ayahs, key = { "a_${it.second.id}" }) { (surah, ayah) ->
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text(
                    text = "${surah?.banglaName ?: "কুরআন"} (আয়াত ${ayah.ayahNumber})",
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = IslamicGreenPrimary,
                      fontWeight = FontWeight.Bold
                    )
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = ayah.arabicText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      fontFamily = FontFamily.Serif,
                      fontSize = settings.arabicFontSize.sp,
                      textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.fillMaxWidth()
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = ayah.banglaTranslation,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = settings.banglaFontSize.sp)
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                  ) {
                    IconButton(onClick = { viewModel.toggleFavoriteAyah(ayah, surah?.banglaName ?: "কুরআন") }) {
                      Icon(
                        if (ayah.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "সংরক্ষণ",
                        tint = if (ayah.isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    IconButton(onClick = {
                      val c = "${surah?.banglaName} (${ayah.ayahNumber})\n${ayah.arabicText}\n${ayah.banglaTranslation}"
                      copyToClipboard(context, "Ayah", c)
                    }) {
                      Icon(Icons.Outlined.ContentCopy, contentDescription = "কপি", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {
                      val c = "${surah?.banglaName} (${ayah.ayahNumber})\n${ayah.arabicText}\n${ayah.banglaTranslation}"
                      shareText(context, "কুরআনের আয়াত", c)
                    }) {
                      Icon(Icons.Outlined.Share, contentDescription = "শেয়ার", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
}
