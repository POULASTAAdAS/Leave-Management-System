package com.poulastaa.lms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.poulastaa.lms.navigation.Navigation
import com.poulastaa.lms.ui.theme.LMSTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setupSplashScreen()

        setContent {
            LMSTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startDestination by viewModel.startDestination.collectAsState()

                    startDestination?.let {
                        val navController = rememberNavController()

                        Navigation(
                            navController = navController,
                            startDestination = it
                        )
                    }
                }
            }
        }
    }

    private fun setupSplashScreen() {
        var keepSplashOpened = true

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.keepSplashOn.collectLatest {
                    keepSplashOpened = it
                }
            }
        }

        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
    }
}