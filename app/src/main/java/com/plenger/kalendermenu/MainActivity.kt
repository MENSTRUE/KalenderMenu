package com.plenger.kalendermenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.plenger.kalendermenu.ui.navigation.KalenderMenuNavGraph
import com.plenger.kalendermenu.ui.theme.KalenderMenuTheme
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalenderMenuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NeutralBackground
                ) {
                    val navController = rememberNavController()
                    KalenderMenuNavGraph(navController = navController)
                }
            }
        }
    }
}