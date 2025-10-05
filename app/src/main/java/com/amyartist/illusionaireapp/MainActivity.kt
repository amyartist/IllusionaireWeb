package com.amyartist.illusionaireapp

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.amyartist.illusionaireapp.composables.SparklingBackground
import com.amyartist.illusionaireapp.data.GameViewModel
import com.amyartist.illusionaireapp.ui.theme.IllusionaireAppTheme
import com.amyartist.illusionaireapp.utils.hideSystemUI

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUI(window, window.decorView)
        gameViewModel.startBackgroundMusic()
        setContent {
            IllusionaireAppTheme {
                val navController = rememberNavController()

                Box(modifier = Modifier.fillMaxSize()) {
                    SparklingBackground(
                        modifier = Modifier.fillMaxSize(),
                        sparkleColors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.LightGray.copy(alpha = 0.7f),
                            Color(0xFFFFFACD).copy(alpha = 0.7f),
                            Color(0xFFE6E6FA).copy(alpha = 0.6f)
                        )
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        AppNavigation(navController = navController, gameViewModel = gameViewModel)
                    }
                }
            }
        }
    }
}