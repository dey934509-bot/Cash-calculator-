package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CashCounterScreen
import com.example.ui.screens.ExtraAndHandScreen
import com.example.ui.theme.Local3DThemeColors
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CashViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CashViewModel = viewModel()
            val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(selectedTheme = selectedTheme) {
                val themeColors = Local3DThemeColors.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = themeColors.background
                ) {
                    CashAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CashAppNavigation(viewModel: CashViewModel) {
    var currentScreen by remember { mutableStateOf("counter") }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState == "extra_and_hand") {
                slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
            } else {
                slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
            }
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            "counter" -> {
                CashCounterScreen(
                    viewModel = viewModel,
                    onNavigateToExtraAndHand = {
                        currentScreen = "extra_and_hand"
                    }
                )
            }
            "extra_and_hand" -> {
                ExtraAndHandScreen(
                    viewModel = viewModel,
                    onBack = {
                        currentScreen = "counter"
                    }
                )
            }
        }
    }
}
