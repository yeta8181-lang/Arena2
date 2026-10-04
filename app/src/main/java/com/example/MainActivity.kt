package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.ArenaDarkBg
import com.example.ui.theme.HeroArenaTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeroArenaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArenaDarkBg
                ) {
                    HeroArenaApp()
                }
            }
        }
    }
}

@Composable
fun HeroArenaApp(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.currentScreen) {
        ScreenState.MAIN_MENU -> {
            MainMenuScreen(
                state = uiState,
                onSelectMode = { mode -> viewModel.selectGameMode(mode) },
                onOpenGuide = { viewModel.navigateTo(ScreenState.HERO_GUIDE) },
                onOpen3DViewer = { viewModel.openHero3DViewer("okcu_elf") },
                onFastBattle = { viewModel.startBattle() }
            )
        }
        ScreenState.TEAM_BUILDER -> {
            TeamBuilderScreen(
                state = uiState,
                onTabSelect = { tab -> viewModel.setActiveBuilderTab(tab) },
                onToggleHero = { id, tab -> viewModel.toggleHeroSelection(id, tab) },
                onSwapPosition = { from, to, tab -> viewModel.swapHeroPosition(from, to, tab) },
                onUpdateTeamName = { name, tab -> viewModel.updateTeamName(name, tab) },
                onShowHeroDetail = { hero -> viewModel.showHeroDetail(hero) },
                onInspectHero3D = { heroId -> viewModel.openHero3DViewer(heroId) },
                onStartBattle = { viewModel.startBattle() },
                onBackToMenu = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
        ScreenState.ONLINE_ROOM -> {
            OnlineRoomScreen(
                state = uiState,
                onGenerateCode = { viewModel.generateNewRoomCode() },
                onCodeChange = { code -> viewModel.setEnteredRoomCode(code) },
                onJoinRoom = { viewModel.joinRoomWithCode() },
                onStartBattle = { viewModel.startBattle() },
                onBackToMenu = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
        ScreenState.MATCHMAKING -> {
            MatchmakingScreen(
                state = uiState,
                onStartSearch = { viewModel.startMatchmakingSearch() },
                onCancelSearch = { viewModel.cancelMatchmaking() },
                onBackToMenu = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
        ScreenState.BATTLE_ARENA -> {
            BattleArenaScreen(
                state = uiState,
                onTogglePause = { viewModel.togglePauseBattle() },
                onCycleSpeed = { viewModel.cycleBattleSpeed() },
                onSkipToEnd = { viewModel.skipBattleToEnd() },
                onBackToMenu = { viewModel.resetToMenu() }
            )
        }
        ScreenState.BATTLE_RESULT -> {
            BattleResultScreen(
                state = uiState,
                onRematch = { viewModel.restartBattle() },
                onEditTeams = { viewModel.navigateTo(ScreenState.TEAM_BUILDER) },
                onHome = { viewModel.resetToMenu() }
            )
        }
        ScreenState.HERO_GUIDE -> {
            HeroGuideScreen(
                onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
        ScreenState.HERO_3D_VIEWER -> {
            Hero3DViewerScreen(
                initialHeroId = uiState.selectedHeroFor3D,
                onBack = { viewModel.navigateTo(ScreenState.MAIN_MENU) }
            )
        }
    }
}
