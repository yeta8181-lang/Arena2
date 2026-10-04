package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HeroRegistry
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(
    state: GameUiState,
    onStartSearch: () -> Unit,
    onCancelSearch: () -> Unit,
    onBackToMenu: () -> Unit
) {
    BackHandler {
        onCancelSearch()
        onBackToMenu()
    }

    LaunchedEffect(Unit) {
        if (!state.isSearchingMatch && state.matchedOpponent == null) {
            onStartSearch()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏆 Hızlı Online Eşleşme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onCancelSearch()
                            onBackToMenu()
                        },
                        modifier = Modifier.testTag("matchmaking_back_button")
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
        containerColor = ArenaDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val opponent = state.matchedOpponent

            if (opponent == null) {
                // Radar Searching Animation
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .border(2.dp, GoldAccent.copy(alpha = pulseAlpha), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(GoldAccent.copy(alpha = 0.3f), ArenaSurface)
                                )
                            )
                            .border(2.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚔️", fontSize = 36.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Rakip Aranıyor...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Kupan: 🏆 ${state.playerTrophies} (Altın Lig)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Text(
                    text = "Sana uygun dengeli bir rakip seçiliyor...",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = {
                        onCancelSearch()
                        onBackToMenu()
                    },
                    modifier = Modifier.testTag("cancel_search_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aramayı İptal Et")
                }
            } else {
                // Match Found Screen!
                Text(
                    text = "🔥 RAKİP BULUNDU!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(20.dp))

                // VS Card Container
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Team1Blue)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🛡️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sen (1. Oyuncu)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Team1Blue
                            )
                            Text(
                                text = "🏆 ${state.playerTrophies}",
                                fontSize = 11.sp,
                                color = GoldAccent
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "VS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    // Opponent Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ArenaSurface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Team2Red)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "⚔️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = opponent.playerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Team2Red,
                                maxLines = 1
                            )
                            Text(
                                text = "🏆 ${opponent.trophies}",
                                fontSize = 11.sp,
                                color = GoldAccent
                            )
                            Text(
                                text = "%${opponent.winRate} Zafer",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Opponent Heroes Preview
                Text(
                    text = "Rakip Kadrosu: ${opponent.teamName}",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    opponent.heroIds.forEach { id ->
                        val hero = HeroRegistry.getHeroById(id)
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ArenaSurface)
                                .border(1.5.dp, Team2Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = hero.emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldAccent,
                    trackColor = ArenaSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Arenaya giriliyor...",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
