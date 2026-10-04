package com.example.battle

import com.example.model.*
import kotlin.random.Random

class BattleEngine(
    val team1Setup: TeamSetup,
    val team2Setup: TeamSetup,
    val onEvent: (CombatLogEntry) -> Unit = {},
    val onFloatingText: (FloatingCombatText) -> Unit = {},
    val onSkillAnnouncement: (SkillCastAnnouncement) -> Unit = {}
) {
    private var nextLogId = 1L
    private var nextFloatingTextId = 1L
    private var nextSkillId = 1L

    val heroesTeam1: List<CombatHero> = team1Setup.selectedHeroIds.mapIndexed { index, heroId ->
        val hero = HeroRegistry.getHeroById(heroId)
        CombatHero(
            uid = "t1_${index}_${hero.id}",
            hero = hero,
            teamId = TeamId.TEAM_1,
            slotIndex = index,
            currentHp = hero.maxHp,
            maxHp = hero.maxHp,
            currentMana = 20, // Initial momentum
            attackCooldown = Random.nextFloat() * 0.4f // Slight offset for natural start
        )
    }

    val heroesTeam2: List<CombatHero> = team2Setup.selectedHeroIds.mapIndexed { index, heroId ->
        val hero = HeroRegistry.getHeroById(heroId)
        CombatHero(
            uid = "t2_${index}_${hero.id}",
            hero = hero,
            teamId = TeamId.TEAM_2,
            slotIndex = index,
            currentHp = hero.maxHp,
            maxHp = hero.maxHp,
            currentMana = 20,
            attackCooldown = Random.nextFloat() * 0.4f
        )
    }

    var elapsedTimeSeconds: Float = 0f
        private set

    var isFinished: Boolean = false
        private set

    var winnerTeam: TeamId? = null
        private set

    fun tick(deltaSeconds: Float) {
        if (isFinished) return
        elapsedTimeSeconds += deltaSeconds

        // Reset short animation triggers
        allHeroes().forEach {
            it.isAttacking = false
            it.isTakingDamage = false
            it.isCastingSkill = false
        }

        // 1. Process active status effects (burn, stun, freeze ticks)
        processStatusEffects(deltaSeconds)

        // 2. Process attack cooldowns & hero actions
        val aliveHeroes = allHeroes().filter { it.isAlive }
        for (hero in aliveHeroes) {
            if (!hero.isAlive || hero.isStunnedOrFrozen) continue

            // Check if hero has enough mana to cast skill
            if (hero.currentMana >= hero.maxMana) {
                castHeroSkill(hero)
                checkWinCondition()
                if (isFinished) return
                continue
            }

            // Normal Attack Cooldown
            hero.attackCooldown -= deltaSeconds
            if (hero.attackCooldown <= 0f) {
                performNormalAttack(hero)
                hero.attackCooldown = hero.hero.attackSpeedSeconds
                checkWinCondition()
                if (isFinished) return
            }
        }

        checkWinCondition()
    }

    fun allHeroes(): List<CombatHero> = heroesTeam1 + heroesTeam2

    private fun processStatusEffects(deltaSeconds: Float) {
        for (hero in allHeroes().filter { it.isAlive }) {
            val iterator = hero.activeEffects.iterator()
            while (iterator.hasNext()) {
                val effect = iterator.next()
                effect.remainingSeconds -= deltaSeconds

                // Burn damage tick every second
                if (effect.type == StatusType.BURN) {
                    val burnDamage = (effect.value * deltaSeconds).toInt()
                    if (burnDamage > 0) {
                        applyDamage(
                            target = hero,
                            rawAmount = burnDamage,
                            isPureMagic = true,
                            attacker = null,
                            customTag = "Yanma"
                        )
                    }
                }

                if (effect.remainingSeconds <= 0f) {
                    iterator.remove()
                }
            }
        }
    }

    private fun findTargetFor(attacker: CombatHero): CombatHero? {
        val enemyTeam = if (attacker.teamId == TeamId.TEAM_1) heroesTeam2 else heroesTeam1
        val aliveEnemies = enemyTeam.filter { it.isAlive }
        if (aliveEnemies.isEmpty()) return null

        // Check for taunter
        val taunter = aliveEnemies.firstOrNull { enemy ->
            enemy.activeEffects.any { it.type == StatusType.TAUNT }
        }
        if (taunter != null) return taunter

        // Assassin logic: targets squishy backline
        if (attacker.hero.role == HeroRole.ASSASSIN) {
            val backline = aliveEnemies.filter { !it.isFrontline }
            if (backline.isNotEmpty()) {
                return backline.minByOrNull { it.currentHp }
            }
            return aliveEnemies.minByOrNull { it.currentHp }
        }

        // Standard logic: Frontline first
        val frontliners = aliveEnemies.filter { it.isFrontline }
        if (frontliners.isNotEmpty()) {
            return frontliners.minByOrNull { it.currentHp }
        }

        return aliveEnemies.minByOrNull { it.currentHp }
    }

    private fun performNormalAttack(attacker: CombatHero) {
        val target = findTargetFor(attacker) ?: return

        attacker.isAttacking = true

        val baseAtk = attacker.hero.attack
        val isCrit = (Random.nextFloat() < if (attacker.hero.role == HeroRole.ASSASSIN) 0.35f else 0.15f)
        val multiplier = if (isCrit) 1.8f else 1.0f

        val variance = 0.95f + Random.nextFloat() * 0.1f
        val rawDamage = (baseAtk * multiplier * variance).toInt()

        applyDamage(
            target = target,
            rawAmount = rawDamage,
            isPureMagic = false,
            attacker = attacker,
            isCrit = isCrit
        )

        // Gain mana
        attacker.currentMana = (attacker.currentMana + 18).coerceAtMost(attacker.maxMana)
        target.currentMana = (target.currentMana + 10).coerceAtMost(target.maxMana)
    }

    private fun castHeroSkill(caster: CombatHero) {
        caster.currentMana = 0
        caster.isCastingSkill = true

        val enemyTeam = if (caster.teamId == TeamId.TEAM_1) heroesTeam2 else heroesTeam1
        val allyTeam = if (caster.teamId == TeamId.TEAM_1) heroesTeam1 else heroesTeam2

        onSkillAnnouncement(
            SkillCastAnnouncement(
                id = nextSkillId++,
                heroName = caster.hero.name,
                skillName = caster.hero.skillName,
                teamId = caster.teamId,
                emoji = caster.hero.emoji,
                colorHex = caster.hero.colorHex
            )
        )

        addCombatLog(
            message = "${caster.hero.name} [${caster.hero.skillName}] yeteneğini kullandı!",
            tag = "YETENEK",
            colorHex = caster.hero.colorHex
        )

        when (caster.hero.id) {
            "okcu_elf" -> {
                // Rain of arrows: all enemies take 180 damage and slow
                enemyTeam.filter { it.isAlive }.forEach { enemy ->
                    applyDamage(enemy, 180, isPureMagic = true, attacker = caster, isSkill = true)
                    enemy.activeEffects.add(ActiveStatusEffect(StatusType.FREEZE, 2.5f))
                }
            }
            "siyah_ork" -> {
                // Savage strike + lifesteal
                val target = findTargetFor(caster)
                if (target != null) {
                    val damageDealt = applyDamage(target, 280, isPureMagic = false, attacker = caster, isCrit = true, isSkill = true)
                    val healAmount = (damageDealt * 0.45f).toInt()
                    healHero(caster, healAmount)
                }
            }
            "zirhli_insan" -> {
                // Holy blade smite + armor shred
                val target = findTargetFor(caster)
                if (target != null) {
                    applyDamage(target, 240, isPureMagic = true, attacker = caster, isSkill = true)
                    target.activeEffects.add(ActiveStatusEffect(StatusType.STUN, 1.2f))
                }
            }
            "muhafiz" -> {
                // Aegis Shield Wall: shields all allies and taunts
                allyTeam.filter { it.isAlive }.forEach { ally ->
                    ally.currentShield += 260
                    ally.activeEffects.add(ActiveStatusEffect(StatusType.SHIELD, 4.0f))
                    showFloating(ally, "+260 Kalkan", 0xFF38BDF8, isHeal = true)
                }
                caster.activeEffects.add(ActiveStatusEffect(StatusType.TAUNT, 3.5f))
            }
            "robot_tank" -> {
                // Rocket Barrage: 3 rockets on frontliners
                val targets = enemyTeam.filter { it.isAlive }
                val count = targets.take(3)
                count.forEach { enemy ->
                    applyDamage(enemy, 150, isPureMagic = true, attacker = caster, isSkill = true)
                    enemy.activeEffects.add(ActiveStatusEffect(StatusType.BURN, 3.0f, value = 30f))
                }
            }
            "tas_golemi" -> {
                // Seismic Slam: damage + stun frontliners
                enemyTeam.filter { it.isAlive && it.isFrontline }.forEach { enemy ->
                    applyDamage(enemy, 160, isPureMagic = true, attacker = caster, isSkill = true)
                    enemy.activeEffects.add(ActiveStatusEffect(StatusType.STUN, 2.0f))
                }
                enemyTeam.filter { it.isAlive && !it.isFrontline }.forEach { enemy ->
                    applyDamage(enemy, 90, isPureMagic = true, attacker = caster, isSkill = true)
                }
            }
            "alev_buyucusu" -> {
                // Pyroblast on highest HP enemy
                val target = enemyTeam.filter { it.isAlive }.maxByOrNull { it.currentHp }
                if (target != null) {
                    applyDamage(target, 340, isPureMagic = true, attacker = caster, isCrit = true, isSkill = true)
                    target.activeEffects.add(ActiveStatusEffect(StatusType.BURN, 3.5f, value = 40f))
                }
            }
            "golge_suikastci" -> {
                // Shadow strike on squishiest enemy
                val target = enemyTeam.filter { it.isAlive }.minByOrNull { it.currentHp }
                if (target != null) {
                    applyDamage(target, 370, isPureMagic = false, attacker = caster, isCrit = true, isSkill = true)
                }
            }
            "sifaci_peri" -> {
                // Spring of Life: heal 2 lowest allies
                val injuredAllies = allyTeam.filter { it.isAlive }.sortedBy { it.hpPercentage }.take(2)
                injuredAllies.forEach { ally ->
                    healHero(ally, 300)
                    ally.activeEffects.add(ActiveStatusEffect(StatusType.BUFF, 3.0f))
                }
                caster.totalHealingDone += 600
            }
            "buz_cadisi" -> {
                // Glacial Blizzard: AOE frost + freeze
                enemyTeam.filter { it.isAlive }.forEach { enemy ->
                    applyDamage(enemy, 175, isPureMagic = true, attacker = caster, isSkill = true)
                    enemy.activeEffects.add(ActiveStatusEffect(StatusType.FREEZE, 2.2f))
                }
            }
        }
    }

    private fun applyDamage(
        target: CombatHero,
        rawAmount: Int,
        isPureMagic: Boolean,
        attacker: CombatHero?,
        isCrit: Boolean = false,
        isSkill: Boolean = false,
        customTag: String? = null
    ): Int {
        if (!target.isAlive) return 0

        target.isTakingDamage = true

        val defense = if (isPureMagic) (target.hero.defense / 2) else target.hero.defense
        val damageReduction = defense.toFloat() / (defense + 100f)
        val finalDamage = ((rawAmount * (1f - damageReduction)).toInt()).coerceAtLeast(10)

        var damageLeft = finalDamage

        // Absorb by shield first
        if (target.currentShield > 0) {
            if (target.currentShield >= damageLeft) {
                target.currentShield -= damageLeft
                damageLeft = 0
            } else {
                damageLeft -= target.currentShield
                target.currentShield = 0
            }
        }

        target.currentHp -= damageLeft
        target.totalDamageTaken += finalDamage

        if (attacker != null) {
            attacker.totalDamageDealt += finalDamage
        }

        // Floating Text
        val text = when {
            isCrit -> "KRİTİK! -$finalDamage"
            isSkill -> "SKİLL! -$finalDamage"
            customTag != null -> "$customTag -$finalDamage"
            else -> "-$finalDamage"
        }
        val colorHex = if (isCrit) 0xFFFF0054 else if (isSkill) 0xFFFFD166 else 0xFFFF4D4D
        showFloating(target, text, colorHex, isCrit = isCrit, isSkill = isSkill)

        if (target.currentHp <= 0) {
            target.currentHp = 0
            target.isAlive = false
            if (attacker != null) {
                attacker.kills++
            }
            addCombatLog(
                message = "${target.hero.name} düştü! (Katil: ${attacker?.hero?.name ?: "Büyü/Etki"})",
                tag = "ÖLÜM",
                colorHex = 0xFFFF4D4D
            )
        }

        return finalDamage
    }

    private fun healHero(target: CombatHero, amount: Int) {
        if (!target.isAlive) return
        val missingHp = target.maxHp - target.currentHp
        val actualHeal = amount.coerceAtMost(missingHp)
        target.currentHp += actualHeal
        target.totalHealingDone += actualHeal

        showFloating(target, "+$actualHeal", 0xFF06D6A0, isHeal = true)

        addCombatLog(
            message = "${target.hero.name} $actualHeal can yeniledi!",
            tag = "ŞİFA",
            colorHex = 0xFF06D6A0
        )
    }

    private fun showFloating(
        hero: CombatHero,
        text: String,
        colorHex: Long,
        isCrit: Boolean = false,
        isHeal: Boolean = false,
        isSkill: Boolean = false
    ) {
        onFloatingText(
            FloatingCombatText(
                id = nextFloatingTextId++,
                heroUid = hero.uid,
                text = text,
                colorHex = colorHex,
                isCrit = isCrit,
                isHeal = isHeal,
                isSkill = isSkill
            )
        )
    }

    private fun addCombatLog(message: String, tag: String, colorHex: Long) {
        onEvent(
            CombatLogEntry(
                id = nextLogId++,
                timestampSeconds = elapsedTimeSeconds,
                message = message,
                tag = tag,
                colorHex = colorHex
            )
        )
    }

    private fun checkWinCondition() {
        if (isFinished) return

        val team1Alive = heroesTeam1.any { it.isAlive }
        val team2Alive = heroesTeam2.any { it.isAlive }

        if (!team1Alive || !team2Alive) {
            isFinished = true
            winnerTeam = when {
                team1Alive && !team2Alive -> TeamId.TEAM_1
                !team1Alive && team2Alive -> TeamId.TEAM_2
                else -> TeamId.TEAM_1 // Fallback draw
            }

            addCombatLog(
                message = "SAVAŞ SONA ERDİ! Kazanan: ${winnerTeam?.displayName}",
                tag = "ZAFER",
                colorHex = 0xFFFFD166
            )
        }
    }

    fun getStats(): BattleStats {
        val all = allHeroes()
        val mvp = all.maxByOrNull { it.totalDamageDealt + (it.totalHealingDone * 1.2f).toInt() }?.uid ?: ""
        return BattleStats(
            winnerTeam = winnerTeam ?: TeamId.TEAM_1,
            mvpHeroUid = mvp,
            durationSeconds = elapsedTimeSeconds,
            allHeroes = all
        )
    }
}
