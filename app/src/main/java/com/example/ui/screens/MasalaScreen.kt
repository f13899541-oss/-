package com.example.ui.screens

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
import com.example.data.model.MasalaEntity
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.MasalaCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun MasalaScreen(
  viewModel: NoorViewModel,
  onOpenSearch: () -> Unit,
  onOpenBookmarks: () -> Unit,
  onAddMasalaClick: (() -> Unit)? = null,
  onEditMasalaClick: ((MasalaEntity) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val masalas by viewModel.filteredMasalas.collectAsState()
  val selectedCategory by viewModel.selectedMasalaCategory.collectAsState()
  val settings by viewModel.settings.collectAsState()

  val categories = listOf(
    "সব",
    "নামাজের মাসআলা",
    "রোজার মাসআলা",
    "অজুর মাসআলা",
    "গোসলের মাসআলা",
    "পবিত্রতার মাসআলা",
    "যাকাতের মাসআলা",
    "হজের মাসআলা",
    "দৈনন্দিন জীবনের মাসআলা"
  )

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "ইসলামিক মাসআলা",
        subtitle = "কুরআন ও সুন্নাহভিত্তিক নির্ভরযোগ্য সমাধান",
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
      if (onAddMasalaClick != null) {
        FloatingActionButton(
          onClick = onAddMasalaClick,
          containerColor = IslamicGreenPrimary,
          contentColor = androidx.compose.ui.graphics.Color.White,
          modifier = Modifier.testTag("add_masala_fab")
        ) {
          Icon(Icons.Default.Add, contentDescription = "নতুন মাসআলা যোগ করুন")
        }
      }
    },
    modifier = modifier.fillMaxSize().testTag("masala_screen")
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Horizontal category chips
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
            onClick = { viewModel.setMasalaCategory(category) },
            label = {
              Text(
                text = category,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = androidx.compose.ui.graphics.Color.White,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("masala_chip_$category")
          )
        }
      }

      // Counter
      Text(
        text = if (selectedCategory == "সব") "মোট মাসআলা: ${masalas.size} টি" else "$selectedCategory: ${masalas.size} টি",
        style = MaterialTheme.typography.labelMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )

      if (masalas.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "এই বিভাগে কোনো মাসআলা পাওয়া যায়নি",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(masalas, key = { it.id }) { masala ->
            MasalaCard(
              masala = masala,
              banglaFontSize = settings.banglaFontSize,
              onBookmarkClick = { viewModel.toggleFavoriteMasala(masala) },
              onEditClick = if (onEditMasalaClick != null) { { onEditMasalaClick(masala) } } else null,
              onDeleteClick = if (onEditMasalaClick != null) { { viewModel.deleteMasala(masala) } } else null
            )
          }
        }
      }
    }
  }
}
