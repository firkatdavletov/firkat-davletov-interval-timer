package com.firkat.intervaltraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.firkat.intervaltraining.app.navigation.IntervalNavHost
import com.firkat.intervaltraining.ui.theme.IntervalTrainingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntervalTrainingTheme {
                IntervalNavHost()
            }
        }
    }
}