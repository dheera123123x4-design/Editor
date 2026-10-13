package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StudioBottomNav
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioDarkBg
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    private val studioViewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MotionAiApp(viewModel = studioViewModel)
            }
        }
    }
}

@Composable
fun MotionAiApp(viewModel: StudioViewModel) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var hasSeenOnboarding by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Handle system back gesture
    if (currentScreen != StudioScreen.MAIN_TABS) {
        BackHandler {
            viewModel.navigateBack()
        }
    }

    if (!hasSeenOnboarding) {
        OnboardingScreen(
            onDismiss = { hasSeenOnboarding = true }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = StudioDarkBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (currentScreen == StudioScreen.MAIN_TABS) {
                    StudioBottomNav(
                        activeTab = activeTab,
                        onTabSelected = { tab -> viewModel.selectTab(tab) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(StudioDarkBg)
            ) {
                when (currentScreen) {
                    StudioScreen.MAIN_TABS -> {
                        when (activeTab) {
                            StudioTab.HOME -> HomeScreen(viewModel = viewModel)
                            StudioTab.AI_TOOLS -> AiToolsScreen(viewModel = viewModel)
                            StudioTab.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                            StudioTab.TEMPLATES -> TemplatesScreen(viewModel = viewModel)
                            StudioTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                    StudioScreen.EDITOR -> {
                        EditorScreen(viewModel = viewModel)
                    }
                    StudioScreen.EXPORT -> {
                        ExportScreen(viewModel = viewModel)
                    }
                    StudioScreen.AI_TOOL_DETAIL -> {
                        AiToolsScreen(viewModel = viewModel)
                    }
                    StudioScreen.ONBOARDING -> {
                        OnboardingScreen(onDismiss = { viewModel.openScreen(StudioScreen.MAIN_TABS) })
                    }
                }
            }
        }
    }
}
