package com.example.model

enum class TeamId(val displayName: String, val colorHex: Long) {
    TEAM_1("Mavi Takım (1. Oyuncu)", 0xFF00B4D8),
    TEAM_2("Kızıl Takım (2. Oyuncu)", 0xFFEF233C)
}

enum class BattleFormation(val displayName: String) {
    FRONTLINE("Ön Hat"),
    BACKLINE("Arka Hat")
}

data class ActiveStatusEffect(
    val type: StatusType,
    var remainingSeconds: Float,
    val value: Float = 0f
)

enum class StatusType(val label: String, val badgeEmoji: String, val colorHex: Long) {
    STUN("Sersemledi", "⚡", 0xFFFFE600),
    FREEZE("Dondu", "❄️", 0xFF90E0EF),
    BURN("Yanıyor", "🔥", 0xFFFF5400),
    TAUNT("Hedef Çekti", "🎯", 0xFFF59E0B),
    SHIELD("Kalkan", "🛡️", 0xFF38BDF8),
    BUFF("Güçlendi", "⚔️", 0xFF10B981)
}

data class CombatHero(
    val uid: String,
    val hero: Hero,
    val teamId: TeamId,
    val slotIndex: Int,
    var currentHp: Int,
    val maxHp: Int,
    var currentMana: Int = 0,
    val maxMana: Int = 100,
    var currentShield: Int = 0,
    var attackCooldown: Float = 0f,
    var isAlive: Boolean = true,
    var isAttacking: Boolean = false,
    var isTakingDamage: Boolean = false,
    var isCastingSkill: Boolean = false,
    val activeEffects: MutableList<ActiveStatusEffect> = mutableListOf(),
    var totalDamageDealt: Int = 0,
    var totalDamageTaken: Int = 0,
    var totalHealingDone: Int = 0,
    var kills: Int = 0
) {
    val isStunnedOrFrozen: Boolean
        get() = activeEffects.any { it.type == StatusType.STUN || it.type == StatusType.FREEZE }

    val isFrontline: Boolean
        get() = slotIndex < 2

    val hpPercentage: Float
        get() = (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)

    val manaPercentage: Float
        get() = (currentMana.toFloat() / maxMana.toFloat()).coerceIn(0f, 1f)

    val shieldPercentage: Float
        get() = (currentShield.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)
}

data class FloatingCombatText(
    val id: Long,
    val heroUid: String,
    val text: String,
    val colorHex: Long,
    val isCrit: Boolean = false,
    val isHeal: Boolean = false,
    val isSkill: Boolean = false
)

data class CombatLogEntry(
    val id: Long,
    val timestampSeconds: Float,
    val message: String,
    val tag: String,
    val colorHex: Long
)

data class SkillCastAnnouncement(
    val id: Long,
    val heroName: String,
    val skillName: String,
    val teamId: TeamId,
    val emoji: String,
    val colorHex: Long
)

data class TeamSetup(
    val teamId: TeamId,
    var teamName: String,
    val selectedHeroIds: MutableList<String> = mutableListOf()
)

data class BattleStats(
    val winnerTeam: TeamId,
    val mvpHeroUid: String,
    val durationSeconds: Float,
    val allHeroes: List<CombatHero>
)
