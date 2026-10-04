package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CombatHero
import com.example.model.SkillCastAnnouncement
import com.example.model.TeamId
import com.example.ui.components.BattleHeroUnit
import com.example.ui.components.CombatLogView
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleArenaScreen(
    state: GameUiState,
    onTogglePause: () -> Unit,
    onCycleSpeed: () -> Unit,
    onSkipToEnd: () -> Unit,
    onBackToMenu: () -> Unit
) {
    BackHandler { onBackToMenu() }

    val t1Heroes = state.heroesTeam1
    val t2Heroes = state.heroesTeam2

    val t1Frontline = t1Heroes.filter { it.isFrontline }
    val t1Backline = t1Heroes.filter { !it.isFrontline }

    val t2Frontline = t2Heroes.filter { it.isFrontline }
    val t2Backline = t2Heroes.filter { !it.isFrontline }

    val seconds = state.battleDurationSeconds.toInt()
    val timeFormatted = String.format("%02d:%02d", seconds / 60, seconds % 60)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚔️ Savaş Arenası",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ArenaSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = timeFormatted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToMenu,
                        modifier = Modifier.testTag("battle_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Battle Speed Toggle
                    FilledTonalButton(
                        onClick = onCycleSpeed,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("battle_speed_button"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = ArenaSurfaceVariant)
                    ) {
                        Text(
                            text = "${state.battleSpeed.toInt()}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Pause / Resume
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier.testTag("battle_pause_button")
                    ) {
                        Icon(
                            imageVector = if (state.isBattlePaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (state.isBattlePaused) "Devam Et" else "Duraklat",
                            tint = if (state.isBattlePaused) GoldAccent else TextPrimary
                        )
                    }

                    // Skip to end
                    IconButton(
                        onClick = onSkipToEnd,
                        modifier = Modifier.testTag("battle_skip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Hızlı Bitir",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArenaSurface)
            )
        },
        containerColor = ArenaDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Main Arena Battlefield Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("arena_canvas"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        colors = listOf(Team2Red.copy(alpha = 0.6f), Team1Blue.copy(alpha = 0.6f))
                    )
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // --- TEAM 2 (TOP / RED) ---
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Team 2 Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔴 ${state.team2Setup.teamName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Team2Red
                                )
                                val aliveCount = t2Heroes.count { it.isAlive }
                                Text(
                                    text = "$aliveCount / 4 Hayatta",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Team 2 Backline
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                t2Backline.forEach { hero ->
                                    val unitFloating = state.floatingTexts.filter { it.heroUid == hero.uid }
                                    BattleHeroUnit(hero = hero, floatingTexts = unitFloating)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Team 2 Frontline
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                t2Frontline.forEach { hero ->
                                    val unitFloating = state.floatingTexts.filter { it.heroUid == hero.uid }
                                    BattleHeroUnit(hero = hero, floatingTexts = unitFloating)
                                }
                            }
                        }

                        // --- ARENA CENTER DIVIDER ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.5.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                GoldAccent.copy(alpha = 0.8f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ArenaSurfaceVariant)
                                    .border(1.dp, GoldAccent, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⚡ VS ⚡",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                            }
                        }

                        // --- TEAM 1 (BOTTOM / BLUE) ---
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Team 1 Frontline
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                t1Frontline.forEach { hero ->
                                    val unitFloating = state.floatingTexts.filter { it.heroUid == hero.uid }
                                    BattleHeroUnit(hero = hero, floatingTexts = unitFloating)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Team 1 Backline
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                t1Backline.forEach { hero ->
                                    val unitFloating = state.floatingTexts.filter { it.heroUid == hero.uid }
                                    BattleHeroUnit(hero = hero, floatingTexts = unitFloating)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Team 1 Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔵 ${state.team1Setup.teamName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Team1Blue
                                )
                                val aliveCount = t1Heroes.count { it.isAlive }
                                Text(
                                    text = "$aliveCount / 4 Hayatta",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Skill Cast Overlay Popup Banner
                    Box(modifier = Modifier.align(Alignment.Center)) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.latestSkillAnnouncement != null,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            state.latestSkillAnnouncement?.let { ann ->
                                SkillBannerOverlay(announcement = ann)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Combat Log Live View
            CombatLogView(
                logs = state.combatLog,
                modifier = Modifier.height(115.dp)
            )
        }
    }
}

@Composable
private fun SkillBannerOverlay(announcement: SkillCastAnnouncement) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaDarkBg.copy(alpha = 0.95f)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(announcement.colorHex))
        ),
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = announcement.emoji, fontSize = 24.sp)
            Column {
                Text(
                    text = "${announcement.heroName}!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = announcement.skillName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(announcement.colorHex)
                )
            }
        }
    }
}
