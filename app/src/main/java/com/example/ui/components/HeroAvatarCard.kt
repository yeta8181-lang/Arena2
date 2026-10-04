package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import com.example.ui.theme.*

@Composable
fun HeroAvatarCard(
    hero: Hero,
    isSelected: Boolean,
    slotNumber: Int?, // 1, 2, 3, 4
    teamColor: Color,
    onToggleSelect: () -> Unit,
    onInfoClick: () -> Unit,
    onInspect3D: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) teamColor else ArenaBorder
    val borderWidth = if (isSelected) 2.5.dp else 1.dp

    Card(
        modifier = modifier
            .testTag("hero_card_${hero.id}")
            .clip(RoundedCornerShape(16.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onToggleSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ArenaSurfaceVariant else ArenaSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Top Row: Slot badge or Role + 3D Badge + Info Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected && slotNumber != null) {
                    val positionText = if (slotNumber <= 2) "Ön $slotNumber" else "Arka ${slotNumber - 2}"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(teamColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = positionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(hero.colorHex).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${hero.role.iconEmoji} ${hero.role.displayName}",
                            fontSize = 10.sp,
                            color = Color(hero.colorHex),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onInspect3D != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldAccent.copy(alpha = 0.2f))
                                .clickable { onInspect3D() }
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("3D", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("info_${hero.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Detay",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3D Hero Model Display
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaDarkBg.copy(alpha = 0.7f))
                        .border(1.dp, Color(hero.colorHex).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    com.example.graphics3d.Hero3DCanvas(
                        heroId = hero.id,
                        modifier = Modifier.fillMaxSize(),
                        initialRotationY = 0.4f,
                        initialRotationX = 0.2f,
                        interactiveRotation = false,
                        autoSpin = false,
                        zoomScale = 0.95f
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(teamColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seçildi",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Name
            Text(
                text = hero.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Skill Preview Tag
            Text(
                text = "✨ ${hero.skillName}",
                fontSize = 10.sp,
                color = GoldAccent,
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mini Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ArenaDarkBg)
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "❤️${hero.maxHp}", fontSize = 10.sp, color = HpGreen, fontWeight = FontWeight.Bold)
                Text(text = "⚔️${hero.attack}", fontSize = 10.sp, color = CritRed, fontWeight = FontWeight.Bold)
                Text(text = "🛡️${hero.defense}", fontSize = 10.sp, color = ShieldIce, fontWeight = FontWeight.Bold)
            }
        }
    }
}
