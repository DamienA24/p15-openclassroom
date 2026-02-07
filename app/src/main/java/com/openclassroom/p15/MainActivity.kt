package com.openclassroom.p15

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.openclassroom.p15.navigation.AppNavigation
import com.openclassroom.p15.ui.theme.P15Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P15Theme {
                AppNavigation()
            }
        }
    }
}