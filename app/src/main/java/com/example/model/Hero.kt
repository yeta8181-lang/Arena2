package com.example.model

enum class HeroRole(val displayName: String, val iconEmoji: String) {
    TANK("Tank", "🛡️"),
    WARRIOR("Savaşçı", "⚔️"),
    RANGED("Menzilli", "🏹"),
    MAGE("Büyücü", "🔮"),
    ASSASSIN("Suikastçı", "🗡️"),
    SUPPORT("Destek / Şifacı", "✨")
}

enum class AttackRange {
    MELEE,
    RANGED
}

data class Hero(
    val id: String,
    val name: String,
    val title: String,
    val role: HeroRole,
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val attackSpeedSeconds: Float,
    val attackRange: AttackRange,
    val skillName: String,
    val skillDesc: String,
    val skillCost: Int = 100,
    val colorHex: Long,
    val emoji: String,
    val lore: String
)
