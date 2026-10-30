package com.example.pbdassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.pbdassignment.core.navigation.NavGraph
import com.example.pbdassignment.ui.theme.PbdassignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PbdassignmentTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}