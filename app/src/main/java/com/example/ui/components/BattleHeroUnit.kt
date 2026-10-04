package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CombatHero
import com.example.model.FloatingCombatText
import com.example.model.TeamId
import com.example.ui.theme.*

@Composable
fun BattleHeroUnit(
    hero: CombatHero,
    floatingTexts: List<FloatingCombatText>,
    modifier: Modifier = Modifier
) {
    val isDead = !hero.isAlive
    val teamBorderColor = if (hero.teamId == TeamId.TEAM_1) Team1Blue else Team2Red

    // Animation for attack recoil & taking damage
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val manaGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mana_glow"
    )

    // Damage shake offset
    val attackScale = if (hero.isAttacking) 1.15f else if (hero.isTakingDamage) 0.92f else 1.0f

    Box(
        modifier = modifier
            .testTag("battle_unit_${hero.uid}")
            .scale(attackScale)
            .alpha(if (isDead) 0.35f else 1.0f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(76.dp)
        ) {
            // Floating texts container
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                floatingTexts.forEach { ft ->
                    Text(
                        text = ft.text,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (ft.isCrit || ft.isSkill) 13.sp else 11.sp,
                        color = Color(ft.colorHex)
                    )
                }
            }

            // Status Badges Row
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
            ) {
                hero.activeEffects.take(3).forEach { effect ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 1.dp)
                            .clip(CircleShape)
                            .background(Color(effect.type.colorHex))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(text = effect.type.badgeEmoji, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 3D Hero Unit Frame
            Box(
                modifier = Modifier.size(58.dp),
                contentAlignment = Alignment.Center
            ) {
                // Mana Full Pulse Ring
                if (hero.isAlive && hero.currentMana >= hero.maxMana) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(manaGlowScale)
                            .clip(CircleShape)
                            .border(2.5.dp, ManaGlow, CircleShape)
                    )
                }

                // Real 3D Model Rendering
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaDarkBg.copy(alpha = 0.6f))
                        .border(1.5.dp, teamBorderColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    com.example.graphics3d.Hero3DCanvas(
                        heroId = hero.hero.id,
                        modifier = Modifier.fillMaxSize(),
                        initialRotationY = if (hero.teamId == TeamId.TEAM_1) 0.35f else 2.8f,
                        initialRotationX = 0.25f,
                        interactiveRotation = false,
                        autoSpin = false,
                        zoomScale = 0.95f,
                        isAttacking = hero.isAttacking,
                        isDead = isDead
                    )

                    if (isDead) {
                        Text(text = "💀", fontSize = 24.sp)
                    }
                }

                // Frontline / Backline tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(ArenaSurfaceVariant)
                        .border(1.dp, teamBorderColor, CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (hero.isFrontline) "ÖN" else "ARK",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Name
            Text(
                text = hero.hero.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Health Bar with Shield overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(HpBarBackground)
            ) {
                // HP Bar
                val hpColor = when {
                    hero.hpPercentage > 0.5f -> HpGreen
                    hero.hpPercentage > 0.25f -> GoldAccent
                    else -> CritRed
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(hero.hpPercentage)
                        .background(hpColor)
                )

                // Shield Overlay
                if (hero.currentShield > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(hero.shieldPercentage.coerceAtMost(1f))
                            .background(ShieldIce.copy(alpha = 0.8f))
                    )
                }
            }

            // HP Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${hero.currentHp}/${hero.maxHp}",
                    fontSize = 8.sp,
                    color = TextSecondary
                )
                if (hero.currentShield > 0) {
                    Text(
                        text = "🛡️${hero.currentShield}",
                        fontSize = 8.sp,
                        color = ShieldIce,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mana / Energy Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(HpBarBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(hero.manaPercentage)
                        .background(
                            if (hero.currentMana >= hero.maxMana) ManaGlow else ManaPurple
                        )
                )
            }
        }
    }
}
