package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import com.example.model.HeroRegistry
import com.example.ui.components.HeroAvatarCard
import com.example.ui.components.HeroDetailDialog
import com.example.ui.theme.*
import com.example.viewmodel.ActiveBuilderTab
import com.example.viewmodel.GameUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamBuilderScreen(
    state: GameUiState,
    onTabSelect: (ActiveBuilderTab) -> Unit,
    onToggleHero: (String, ActiveBuilderTab) -> Unit,
    onSwapPosition: (Int, Int, ActiveBuilderTab) -> Unit,
    onUpdateTeamName: (String, ActiveBuilderTab) -> Unit,
    onShowHeroDetail: (Hero?) -> Unit,
    onInspectHero3D: (String) -> Unit = {},
    onStartBattle: () -> Unit,
    onBackToMenu: () -> Unit
) {
    BackHandler { onBackToMenu() }

    val activeTab = state.activeBuilderTab
    val currentSetup = if (activeTab == ActiveBuilderTab.TEAM_1) state.team1Setup else state.team2Setup
    val teamColor = if (activeTab == ActiveBuilderTab.TEAM_1) Team1Blue else Team2Red

    var showNameEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(currentSetup.teamName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Takımını Kur (10 Kahraman)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToMenu,
                        modifier = Modifier.testTag("team_builder_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArenaSurface)
            )
        },
        bottomBar = {
            Surface(
                color = ArenaSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Switch tab button
                    OutlinedButton(
                        onClick = {
                            val next = if (activeTab == ActiveBuilderTab.TEAM_1) ActiveBuilderTab.TEAM_2 else ActiveBuilderTab.TEAM_1
                            onTabSelect(next)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("switch_team_tab_button")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (activeTab == ActiveBuilderTab.TEAM_1) "2. Takıma Geç" else "1. Takıma Geç")
                    }

                    // Start battle button
                    Button(
                        onClick = onStartBattle,
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("start_battle_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Savaşı Başlat!", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = ArenaDarkBg
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Team Switch Tabs
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(8.dp))
                TabRow(
                    selectedTabIndex = if (activeTab == ActiveBuilderTab.TEAM_1) 0 else 1,
                    containerColor = ArenaSurfaceVariant,
                    contentColor = GoldAccent
                ) {
                    Tab(
                        selected = activeTab == ActiveBuilderTab.TEAM_1,
                        onClick = { onTabSelect(ActiveBuilderTab.TEAM_1) },
                        text = {
                            Text(
                                "🔵 1. Oyuncu (${state.team1Setup.selectedHeroIds.size}/4)",
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == ActiveBuilderTab.TEAM_1) Team1Blue else TextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_team_1")
                    )
                    Tab(
                        selected = activeTab == ActiveBuilderTab.TEAM_2,
                        onClick = { onTabSelect(ActiveBuilderTab.TEAM_2) },
                        text = {
                            Text(
                                "🔴 2. Oyuncu (${state.team2Setup.selectedHeroIds.size}/4)",
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == ActiveBuilderTab.TEAM_2) Team2Red else TextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_team_2")
                    )
                }
            }

            // Current Team Header & Formations Slots
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(teamColor)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentSetup.teamName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = teamColor
                                )
                                Text(
                                    text = "4 kahraman seç: 2 Ön Hat (Tank/Dövüşçü), 2 Arka Hat (Menzil/Büyü)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            TextButton(
                                onClick = {
                                    editedName = currentSetup.teamName
                                    showNameEditDialog = true
                                },
                                modifier = Modifier.testTag("rename_team_button")
                            ) {
                                Text("İsim Değiştir", fontSize = 11.sp, color = GoldAccent)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Formation Slots Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (slot in 0..3) {
                                val heroId = currentSetup.selectedHeroIds.getOrNull(slot)
                                val hero = heroId?.let { HeroRegistry.getHeroById(it) }
                                val isFront = slot < 2

                                TeamSlotBadge(
                                    slotLabel = if (isFront) "Ön ${slot + 1}" else "Arka ${slot - 1}",
                                    hero = hero,
                                    teamColor = teamColor,
                                    onClick = {
                                        if (hero != null) {
                                            onToggleHero(hero.id, activeTab)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Title: Hero Roster
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kahraman Seç (${HeroRegistry.allHeroes.size} Kahraman)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Dokun: Ekle/Çıkar | ℹ️: Detay",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            // 10 Heroes in Grid
            items(HeroRegistry.allHeroes, key = { it.id }) { hero ->
                val selectedIndex = currentSetup.selectedHeroIds.indexOf(hero.id)
                val isSelected = selectedIndex != -1
                val slotNumber = if (isSelected) selectedIndex + 1 else null

                HeroAvatarCard(
                    hero = hero,
                    isSelected = isSelected,
                    slotNumber = slotNumber,
                    teamColor = teamColor,
                    onToggleSelect = { onToggleHero(hero.id, activeTab) },
                    onInfoClick = { onShowHeroDetail(hero) },
                    onInspect3D = { onInspectHero3D(hero.id) }
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Hero Detail Dialog
    state.selectedHeroForDetail?.let { hero ->
        HeroDetailDialog(
            hero = hero,
            onDismiss = { onShowHeroDetail(null) },
            onOpen3DViewer = { onInspectHero3D(hero.id) }
        )
    }

    // Rename Team Dialog
    if (showNameEditDialog) {
        AlertDialog(
            onDismissRequest = { showNameEditDialog = false },
            title = { Text("Takım Adını Değiştir") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Takım Adı") },
                    singleLine = true,
                    modifier = Modifier.testTag("team_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            onUpdateTeamName(editedName.trim(), activeTab)
                        }
                        showNameEditDialog = false
                    },
                    modifier = Modifier.testTag("save_team_name")
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameEditDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun TeamSlotBadge(
    slotLabel: String,
    hero: Hero?,
    teamColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ArenaDarkBg)
                .border(
                    1.5.dp,
                    if (hero != null) teamColor else ArenaBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (hero != null) {
                Text(text = hero.emoji, fontSize = 22.sp)
            } else {
                Text(text = "+", fontSize = 20.sp, color = TextMuted)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = hero?.name ?: slotLabel,
            fontSize = 9.sp,
            fontWeight = if (hero != null) FontWeight.Bold else FontWeight.Normal,
            color = if (hero != null) TextPrimary else TextMuted,
            maxLines = 1
        )
    }
}
