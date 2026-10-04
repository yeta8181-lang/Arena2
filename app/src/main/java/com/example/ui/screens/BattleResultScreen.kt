package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
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
import com.example.model.CombatHero
import com.example.model.TeamId
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

enum class StatFilter {
    DAMAGE_DEALT,
    DAMAGE_TAKEN,
    HEALING
}

@Composable
fun BattleResultScreen(
    state: GameUiState,
    onRematch: () -> Unit,
    onEditTeams: () -> Unit,
    onHome: () -> Unit
) {
    BackHandler { onHome() }

    val stats = state.battleStats ?: return
    val isTeam1Win = stats.winnerTeam == TeamId.TEAM_1
    val winnerName = if (isTeam1Win) state.team1Setup.teamName else state.team2Setup.teamName
    val winnerColor = if (isTeam1Win) Team1Blue else Team2Red

    val mvpHero = stats.allHeroes.firstOrNull { it.uid == stats.mvpHeroUid }

    var selectedFilter by remember { mutableStateOf(StatFilter.DAMAGE_DEALT) }

    val maxStatValue = remember(selectedFilter, stats) {
        val maxVal = stats.allHeroes.maxOfOrNull { hero ->
            when (selectedFilter) {
                StatFilter.DAMAGE_DEALT -> hero.totalDamageDealt
                StatFilter.DAMAGE_TAKEN -> hero.totalDamageTaken
                StatFilter.HEALING -> hero.totalHealingDone
            }
        } ?: 1
        maxVal.coerceAtLeast(1)
    }

    Scaffold(
        containerColor = ArenaDarkBg,
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Menü")
                    }

                    Button(
                        onClick = onRematch,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("result_rematch_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tekrar Savaş", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Victory Header
                Text(
                    text = "🏆 ZAFER!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$winnerName Kazandı",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = winnerColor
                )

                val durationSec = stats.durationSeconds.toInt()
                Text(
                    text = "Savaş Süresi: ${durationSec / 60}dk ${durationSec % 60}sn",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MVP Banner Card
                if (mvpHero != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mvp_banner_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceVariant),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(GoldAccent)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent.copy(alpha = 0.2f))
                                    .border(2.dp, GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mvpHero.hero.emoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "⭐ EN DEĞERLİ OYUNCU (MVP)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                                Text(
                                    text = mvpHero.hero.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Hasar: ${mvpHero.totalDamageDealt}  |  Alınan: ${mvpHero.totalDamageTaken}  |  Şifa: ${mvpHero.totalHealingDone}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stat Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = selectedFilter == StatFilter.DAMAGE_DEALT,
                        onClick = { selectedFilter = StatFilter.DAMAGE_DEALT },
                        label = { Text("⚔️ Verilen Hasar", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_damage_dealt")
                    )
                    FilterChip(
                        selected = selectedFilter == StatFilter.DAMAGE_TAKEN,
                        onClick = { selectedFilter = StatFilter.DAMAGE_TAKEN },
                        label = { Text("🛡️ Alınan Hasar", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_damage_taken")
                    )
                    FilterChip(
                        selected = selectedFilter == StatFilter.HEALING,
                        onClick = { selectedFilter = StatFilter.HEALING },
                        label = { Text("✨ Şifa", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_healing")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Hero Stat Rows
            items(stats.allHeroes) { hero ->
                val heroStatValue = when (selectedFilter) {
                    StatFilter.DAMAGE_DEALT -> hero.totalDamageDealt
                    StatFilter.DAMAGE_TAKEN -> hero.totalDamageTaken
                    StatFilter.HEALING -> hero.totalHealingDone
                }
                val fraction = (heroStatValue.toFloat() / maxStatValue.toFloat()).coerceIn(0.02f, 1f)
                val teamColor = if (hero.teamId == TeamId.TEAM_1) Team1Blue else Team2Red

                StatBarRow(
                    hero = hero,
                    statValue = heroStatValue,
                    fraction = fraction,
                    teamColor = teamColor,
                    barColor = when (selectedFilter) {
                        StatFilter.DAMAGE_DEALT -> CritRed
                        StatFilter.DAMAGE_TAKEN -> ShieldIce
                        StatFilter.HEALING -> HealGreen
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onEditTeams,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_teams_button")
                ) {
                    Text("Takımları Düzenle")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatBarRow(
    hero: CombatHero,
    statValue: Int,
    fraction: Float,
    teamColor: Color,
    barColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hero avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ArenaDarkBg)
                    .border(1.5.dp, teamColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = hero.hero.emoji, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = hero.hero.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$statValue",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = barColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ArenaDarkBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .background(barColor)
                    )
                }
            }
        }
    }
}
