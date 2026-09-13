package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun SettingsScreen(
  viewModel: NoorViewModel,
  onOpenAdminPanel: () -> Unit,
  modifier: Modifier = Modifier
) {
  val settings by viewModel.settings.collectAsState()

  var banglaSize by remember(settings.banglaFontSize) { mutableFloatStateOf(settings.banglaFontSize) }
  var arabicSize by remember(settings.arabicFontSize) { mutableFloatStateOf(settings.arabicFontSize) }

  Scaffold(
    topBar = {
      IslamicTopAppBar(
        title = "সেটিংস ও তথ্য",
        subtitle = "ফন্ট সাইজ, ডার্ক মোড ও অ্যাপ পরিচিতি"
      )
    },
    modifier = modifier.fillMaxSize().testTag("settings_screen")
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Theme & Dark Mode
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "ডিসপ্লে ও থিম",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (settings.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                  contentDescription = null,
                  tint = IslamicGold
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("ডার্ক মোড (Dark Theme)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                  Text("রাতে পড়ার জন্য আরামদায়ক ডার্ক থিম", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
              }

              Switch(
                checked = settings.isDarkMode,
                onCheckedChange = { viewModel.updateDarkMode(it, useSystem = false) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = IslamicGreenPrimary
                ),
                modifier = Modifier.testTag("dark_mode_switch")
              )
            }
          }
        }
      }

      // 2. Font Size Customization with Live Preview
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "ফন্ট সাইজ পরিবর্তন",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bangla font slider
            Text(
              text = "বাংলা ফন্ট সাইজ: ${banglaSize.toInt()} sp",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Slider(
              value = banglaSize,
              onValueChange = { banglaSize = it },
              onValueChangeFinished = { viewModel.updateFontSize(banglaSize, arabicSize) },
              valueRange = 13f..24f,
              steps = 10,
              colors = SliderDefaults.colors(
                thumbColor = IslamicGreenPrimary,
                activeTrackColor = IslamicGreenPrimary
              ),
              modifier = Modifier.testTag("bangla_font_slider")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Arabic font slider
            Text(
              text = "আরবি ফন্ট সাইজ: ${arabicSize.toInt()} sp",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Slider(
              value = arabicSize,
              onValueChange = { arabicSize = it },
              onValueChangeFinished = { viewModel.updateFontSize(banglaSize, arabicSize) },
              valueRange = 18f..34f,
              steps = 14,
              colors = SliderDefaults.colors(
                thumbColor = IslamicGoldDark,
                activeTrackColor = IslamicGold
              ),
              modifier = Modifier.testTag("arabic_font_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Live Preview Card
            Text(
              text = "ফন্ট প্রিভিউ:",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Column {
                Text(
                  text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                  style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = arabicSize.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Right
                  ),
                  modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে শুরু করছি।",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = banglaSize.sp
                  )
                )
              }
            }
          }
        }
      }

      // 3. Daily Notifications (Requirement 12)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "দৈনিক নোটিফিকেশন",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = null,
                  tint = IslamicGreenPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("প্রতিদিনের হাদীস ও আয়াত", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                  Text("নিয়মিত ইসলামিক নসিহত পেতে নোটিফিকেশন অন রাখুন", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
              }

              Switch(
                checked = settings.notificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications(it) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = IslamicGreenPrimary
                ),
                modifier = Modifier.testTag("notification_switch")
              )
            }
          }
        }
      }

      // 4. Admin Panel Entry (Requirement 7)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = IslamicGoldLight.copy(alpha = 0.35f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(IslamicGoldDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = "এডমিন প্যানেল (Admin)",
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "হাদীস, মাসআলা ও অডিও লিংক ব্যবস্থাপনা",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }
            }

            Button(
              onClick = onOpenAdminPanel,
              colors = ButtonDefaults.buttonColors(containerColor = IslamicGoldDark),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.testTag("open_admin_panel_button")
            ) {
              Text("প্রবেশ", style = MaterialTheme.typography.labelMedium)
            }
          }
        }
      }

      // 5. About App & Trust Information (Requirement 13, 16)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "অ্যাপ পরিচিতি ও নির্ভরযোগ্যতা",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "অ্যাপের নাম: নূর ইসলামিক (Noor Islamic)",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "ভার্সন: ১.০.০ (অফলাইন ও আধুনিক ডিজাইন)",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "লক্ষ্য ও অঙ্গীকার:",
              style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = "• সকল হাদীস ও মাসআলা সহীহ বুখারী, মুসলিম, তিরমিযী এবং নির্ভরযোগ্য ফিকহ গ্রন্থ থেকে তথ্যসূত্রসহ সন্নিবেশিত। কোনো ভিত্তিহীন বা জাল তথ্য এই অ্যাপে নেই।\n• পবিত্র কুরআনের সূরা আল-মুলক ও সূরা আর-রহমান সহ গুরুত্বপূর্ণ সূরাগুলোর সুললিত ও অনুমোদিত অডিও তিলাওয়াত সংযোজিত।",
              style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "© নূর ইসলামিক • সর্বস্বত্ব সংরক্ষিত",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }
  }
}
