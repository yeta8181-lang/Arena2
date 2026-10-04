package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battle.BattleEngine
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ScreenState {
    MAIN_MENU,
    TEAM_BUILDER,
    ONLINE_ROOM,
    MATCHMAKING,
    BATTLE_ARENA,
    BATTLE_RESULT,
    HERO_GUIDE,
    HERO_3D_VIEWER
}

enum class ActiveBuilderTab {
    TEAM_1,
    TEAM_2
}

data class OnlinePlayerProfile(
    val playerName: String,
    val rankTitle: String,
    val trophies: Int,
    val winRate: Int,
    val teamName: String,
    val heroIds: List<String>
)

data class GameUiState(
    val currentScreen: ScreenState = ScreenState.MAIN_MENU,
    val gameMode: GameMode = GameMode.PASS_AND_PLAY,
    val team1Setup: TeamSetup = TeamSetup(
        teamId = TeamId.TEAM_1,
        teamName = "Mavi Birlik",
        selectedHeroIds = mutableListOf("tas_golemi", "muhafiz", "okcu_elf", "alev_buyucusu")
    ),
    val team2Setup: TeamSetup = TeamSetup(
        teamId = TeamId.TEAM_2,
        teamName = "Kızıl Lejyon",
        selectedHeroIds = mutableListOf("siyah_ork", "zirhli_insan", "robot_tank", "sifaci_peri")
    ),
    val activeBuilderTab: ActiveBuilderTab = ActiveBuilderTab.TEAM_1,
    // Online Room
    val createdRoomCode: String = "ARENA-${Random.nextInt(1000, 9999)}",
    val enteredRoomCode: String = "",
    val roomStatusMessage: String = "",
    val isRoomHost: Boolean = true,
    // Matchmaking
    val isSearchingMatch: Boolean = false,
    val matchedOpponent: OnlinePlayerProfile? = null,
    val playerTrophies: Int = 1250,
    // Battle State
    val isBattleRunning: Boolean = false,
    val isBattlePaused: Boolean = false,
    val battleSpeed: Float = 1.0f,
    val heroesTeam1: List<CombatHero> = emptyList(),
    val heroesTeam2: List<CombatHero> = emptyList(),
    val combatLog: List<CombatLogEntry> = emptyList(),
    val floatingTexts: List<FloatingCombatText> = emptyList(),
    val latestSkillAnnouncement: SkillCastAnnouncement? = null,
    val battleStats: BattleStats? = null,
    val battleDurationSeconds: Float = 0f,
    val selectedHeroForDetail: Hero? = null,
    val selectedHeroFor3D: String = "okcu_elf"
)

enum class GameMode(val title: String, val subtitle: String, val iconEmoji: String) {
    PASS_AND_PLAY("2 Kişilik Kapışma", "Aynı ekranda iki oyuncu takım kurup savaşır", "⚔️"),
    ONLINE_ROOM("Online Oda & Kod", "Oda kodu oluştur veya arkadaşının odasına katıl", "🌐"),
    MATCHMAKING("Hızlı Eşleşme (PvP)", "Online oyuncularla sıralamalı mücadele et", "🏆"),
    PRACTICE("Antrenman Arenası", "Farklı kahraman kombinasyonlarını dene", "🎯")
}

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var battleEngine: BattleEngine? = null
    private var battleJob: Job? = null

    // Rival pool for online matchmaking
    private val onlineRivals = listOf(
        OnlinePlayerProfile("EjderhaSüvarisi", "Usta Savaşçı", 1340, 68, "Ateş Lordları", listOf("tas_golemi", "siyah_ork", "alev_buyucusu", "golge_suikastci")),
        OnlinePlayerProfile("KuzeyMuhafızı", "Buz Şövalyesi", 1290, 62, "Donmuş Taht", listOf("muhafiz", "zirhli_insan", "buz_cadisi", "okcu_elf")),
        OnlinePlayerProfile("GölgeAvcısı", "Gölge Loncası", 1410, 74, "Kara Hançerler", listOf("siyah_ork", "robot_tank", "golge_suikastci", "sifaci_peri")),
        OnlinePlayerProfile("SiberGladyatör", "Tekno Mekanik", 1370, 70, "Demir Yumruk", listOf("robot_tank", "tas_golemi", "okcu_elf", "buz_cadisi")),
        OnlinePlayerProfile("IşıkElçisi", "Işık Şampiyonu", 1450, 77, "Kutsal Birlik", listOf("muhafiz", "zirhli_insan", "sifaci_peri", "alev_buyucusu"))
    )

    fun navigateTo(screen: ScreenState) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun openHero3DViewer(heroId: String = "okcu_elf") {
        _uiState.update { it.copy(selectedHeroFor3D = heroId, currentScreen = ScreenState.HERO_3D_VIEWER) }
    }

    fun selectGameMode(mode: GameMode) {
        _uiState.update { it.copy(gameMode = mode) }
        when (mode) {
            GameMode.PASS_AND_PLAY -> navigateTo(ScreenState.TEAM_BUILDER)
            GameMode.ONLINE_ROOM -> navigateTo(ScreenState.ONLINE_ROOM)
            GameMode.MATCHMAKING -> navigateTo(ScreenState.MATCHMAKING)
            GameMode.PRACTICE -> navigateTo(ScreenState.TEAM_BUILDER)
        }
    }

    fun setActiveBuilderTab(tab: ActiveBuilderTab) {
        _uiState.update { it.copy(activeBuilderTab = tab) }
    }

    fun toggleHeroSelection(heroId: String, forTeam: ActiveBuilderTab) {
        _uiState.update { state ->
            val setup = if (forTeam == ActiveBuilderTab.TEAM_1) state.team1Setup else state.team2Setup
            val list = setup.selectedHeroIds.toMutableList()

            if (list.contains(heroId)) {
                list.remove(heroId)
            } else {
                if (list.size < 4) {
                    list.add(heroId)
                }
            }

            val updatedSetup = setup.copy(selectedHeroIds = list)
            if (forTeam == ActiveBuilderTab.TEAM_1) {
                state.copy(team1Setup = updatedSetup)
            } else {
                state.copy(team2Setup = updatedSetup)
            }
        }
    }

    fun swapHeroPosition(fromIndex: Int, toIndex: Int, forTeam: ActiveBuilderTab) {
        _uiState.update { state ->
            val setup = if (forTeam == ActiveBuilderTab.TEAM_1) state.team1Setup else state.team2Setup
            val list = setup.selectedHeroIds.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices) {
                val temp = list[fromIndex]
                list[fromIndex] = list[toIndex]
                list[toIndex] = temp
            }
            val updated = setup.copy(selectedHeroIds = list)
            if (forTeam == ActiveBuilderTab.TEAM_1) state.copy(team1Setup = updated) else state.copy(team2Setup = updated)
        }
    }

    fun updateTeamName(name: String, forTeam: ActiveBuilderTab) {
        _uiState.update { state ->
            if (forTeam == ActiveBuilderTab.TEAM_1) {
                state.copy(team1Setup = state.team1Setup.copy(teamName = name))
            } else {
                state.copy(team2Setup = state.team2Setup.copy(teamName = name))
            }
        }
    }

    fun showHeroDetail(hero: Hero?) {
        _uiState.update { it.copy(selectedHeroForDetail = hero) }
    }

    // Room Code Functions
    fun setEnteredRoomCode(code: String) {
        _uiState.update { it.copy(enteredRoomCode = code) }
    }

    fun generateNewRoomCode() {
        val newCode = "ARENA-${Random.nextInt(1000, 9999)}"
        _uiState.update { it.copy(createdRoomCode = newCode, roomStatusMessage = "Yeni oda açıldı: $newCode. Arkadaşını bekliyor...") }
    }

    fun joinRoomWithCode() {
        val code = _uiState.value.enteredRoomCode.trim().uppercase()
        if (code.isEmpty()) {
            _uiState.update { it.copy(roomStatusMessage = "Lütfen geçerli bir oda kodu girin!") }
            return
        }

        // Simulate room lookup & opponent load
        val randomRival = onlineRivals.random()
        _uiState.update { state ->
            state.copy(
                team2Setup = state.team2Setup.copy(
                    teamName = "${randomRival.playerName}'in Takımı",
                    selectedHeroIds = randomRival.heroIds.toMutableList()
                ),
                roomStatusMessage = "Odaya bağlanıldı! Rakip bulundu: ${randomRival.playerName}",
                isRoomHost = false
            )
        }

        viewModelScope.launch {
            delay(1200)
            startBattle()
        }
    }

    // Matchmaking logic
    fun startMatchmakingSearch() {
        _uiState.update { it.copy(isSearchingMatch = true, matchedOpponent = null) }
        viewModelScope.launch {
            delay(2200) // realistic matchmaking search time
            val opponent = onlineRivals.random()
            _uiState.update {
                it.copy(
                    isSearchingMatch = false,
                    matchedOpponent = opponent,
                    team2Setup = it.team2Setup.copy(
                        teamName = opponent.teamName,
                        selectedHeroIds = opponent.heroIds.toMutableList()
                    )
                )
            }
            delay(1800) // Show vs splash screen
            startBattle()
        }
    }

    fun cancelMatchmaking() {
        _uiState.update { it.copy(isSearchingMatch = false) }
    }

    // Battle Controls
    fun startBattle() {
        battleJob?.cancel()

        // Ensure both teams have 4 heroes; if not, fill from pool
        ensureFourHeroes(ActiveBuilderTab.TEAM_1)
        ensureFourHeroes(ActiveBuilderTab.TEAM_2)

        val t1 = _uiState.value.team1Setup
        val t2 = _uiState.value.team2Setup

        val logs = mutableListOf<CombatLogEntry>()
        val floatings = mutableListOf<FloatingCombatText>()

        val engine = BattleEngine(
            team1Setup = t1,
            team2Setup = t2,
            onEvent = { log ->
                _uiState.update { s ->
                    val updated = (listOf(log) + s.combatLog).take(30)
                    s.copy(combatLog = updated)
                }
            },
            onFloatingText = { ft ->
                _uiState.update { s ->
                    val updated = (listOf(ft) + s.floatingTexts).take(12)
                    s.copy(floatingTexts = updated)
                }
            },
            onSkillAnnouncement = { ann ->
                _uiState.update { it.copy(latestSkillAnnouncement = ann) }
            }
        )

        battleEngine = engine

        _uiState.update {
            it.copy(
                currentScreen = ScreenState.BATTLE_ARENA,
                isBattleRunning = true,
                isBattlePaused = false,
                heroesTeam1 = engine.heroesTeam1,
                heroesTeam2 = engine.heroesTeam2,
                combatLog = emptyList(),
                floatingTexts = emptyList(),
                latestSkillAnnouncement = null,
                battleStats = null,
                battleDurationSeconds = 0f
            )
        }

        runBattleLoop()
    }

    private fun ensureFourHeroes(tab: ActiveBuilderTab) {
        _uiState.update { state ->
            val setup = if (tab == ActiveBuilderTab.TEAM_1) state.team1Setup else state.team2Setup
            val list = setup.selectedHeroIds.toMutableList()
            if (list.size < 4) {
                val available = HeroRegistry.allHeroes.map { it.id }.filterNot { list.contains(it) }
                for (id in available) {
                    if (list.size >= 4) break
                    list.add(id)
                }
            }
            if (tab == ActiveBuilderTab.TEAM_1) {
                state.copy(team1Setup = setup.copy(selectedHeroIds = list))
            } else {
                state.copy(team2Setup = setup.copy(selectedHeroIds = list))
            }
        }
    }

    private fun runBattleLoop() {
        battleJob = viewModelScope.launch {
            val tickRateMs = 50L
            while (_uiState.value.isBattleRunning && battleEngine != null) {
                val speed = _uiState.value.battleSpeed
                val isPaused = _uiState.value.isBattlePaused

                if (!isPaused) {
                    val deltaSec = (tickRateMs / 1000f) * speed
                    battleEngine?.tick(deltaSec)

                    val engine = battleEngine!!
                    _uiState.update { s ->
                        s.copy(
                            heroesTeam1 = engine.heroesTeam1.map { it.copy() },
                            heroesTeam2 = engine.heroesTeam2.map { it.copy() },
                            battleDurationSeconds = engine.elapsedTimeSeconds
                        )
                    }

                    if (engine.isFinished) {
                        val stats = engine.getStats()
                        val trophyDelta = if (stats.winnerTeam == TeamId.TEAM_1) 25 else -15
                        val newTrophies = (_uiState.value.playerTrophies + trophyDelta).coerceAtLeast(0)

                        _uiState.update { s ->
                            s.copy(
                                isBattleRunning = false,
                                battleStats = stats,
                                playerTrophies = newTrophies
                            )
                        }
                        delay(1200)
                        _uiState.update { it.copy(currentScreen = ScreenState.BATTLE_RESULT) }
                        break
                    }
                }
                delay(tickRateMs)
            }
        }
    }

    fun togglePauseBattle() {
        _uiState.update { it.copy(isBattlePaused = !it.isBattlePaused) }
    }

    fun cycleBattleSpeed() {
        _uiState.update { s ->
            val nextSpeed = when (s.battleSpeed) {
                1.0f -> 2.0f
                2.0f -> 4.0f
                else -> 1.0f
            }
            s.copy(battleSpeed = nextSpeed)
        }
    }

    fun skipBattleToEnd() {
        val engine = battleEngine ?: return
        while (!engine.isFinished) {
            engine.tick(0.1f)
        }
        val stats = engine.getStats()
        _uiState.update {
            it.copy(
                isBattleRunning = false,
                heroesTeam1 = engine.heroesTeam1.map { h -> h.copy() },
                heroesTeam2 = engine.heroesTeam2.map { h -> h.copy() },
                battleStats = stats,
                currentScreen = ScreenState.BATTLE_RESULT
            )
        }
    }

    fun restartBattle() {
        startBattle()
    }

    fun resetToMenu() {
        battleJob?.cancel()
        _uiState.update {
            it.copy(
                currentScreen = ScreenState.MAIN_MENU,
                isBattleRunning = false,
                isBattlePaused = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        battleJob?.cancel()
    }
}
