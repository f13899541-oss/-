package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HadithEntity
import com.example.ui.components.HadithCard
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenContainer
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun HadithScreen(
  viewModel: NoorViewModel,
  onOpenSearch: () -> Unit,
  onOpenBookmarks: () -> Unit,
  onAddHadithClick: (() -> Unit)? = null,
  onEditHadithClick: ((HadithEntity) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val hadiths by viewModel.filteredHadiths.collectAsState()
  val selectedCategory by viewModel.selectedHadithCategory.collectAsState()
  val settings by viewModel.settings.collectAsState()

  val categories = listOf(
    "সব", "নামাজ", "রোজা", "যাকাত", "হজ", "আখলাক", "দোয়া", "ঈমান", "পরিবার", "দৈনন্দিন জীবন"
  )

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "হাদীস শরীফ",
        subtitle = "সহীহ বুখারী, মুসলিম ও তিরমিযী থেকে সংকলিত",
        actions = {
          IconButton(onClick = onOpenSearch) {
            Icon(Icons.Default.Search, contentDescription = "সার্চ", tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onOpenBookmarks) {
            Icon(Icons.Default.Bookmark, contentDescription = "সংরক্ষিত", tint = IslamicGold)
          }
        }
      )
    },
    floatingActionButton = {
      if (onAddHadithClick != null) {
        FloatingActionButton(
          onClick = onAddHadithClick,
          containerColor = IslamicGreenPrimary,
          contentColor = androidx.compose.ui.graphics.Color.White,
          modifier = Modifier.testTag("add_hadith_fab")
        ) {
          Icon(Icons.Default.Add, contentDescription = "নতুন হাদীস যোগ করুন")
        }
      }
    },
    modifier = modifier.fillMaxSize().testTag("hadith_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Category Filter Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { category ->
          val isSelected = selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setHadithCategory(category) },
            label = {
              Text(
                text = category,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = IslamicGreenPrimary,
              selectedLabelColor = androidx.compose.ui.graphics.Color.White,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("category_chip_$category")
          )
        }
      }

      // Count badge & summary
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (selectedCategory == "সব") "মোট হাদীস: ${hadiths.size} টি" else "$selectedCategory বিষয়ক: ${hadiths.size} টি",
          style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
          )
        )
      }

      // Hadiths list
      if (hadiths.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "কোনো হাদীস পাওয়া যায়নি",
              style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(hadiths, key = { it.id }) { hadith ->
            HadithCard(
              hadith = hadith,
              banglaFontSize = settings.banglaFontSize,
              arabicFontSize = settings.arabicFontSize,
              onBookmarkClick = { viewModel.toggleFavoriteHadith(hadith) },
              onEditClick = if (onEditHadithClick != null) { { onEditHadithClick(hadith) } } else null,
              onDeleteClick = if (onEditHadithClick != null) { { viewModel.deleteHadith(hadith) } } else null
            )
          }
        }
      }
    }
  }
}
