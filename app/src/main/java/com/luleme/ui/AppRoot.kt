package com.luleme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.luleme.ui.navigation.NavGraph
import com.luleme.ui.navigation.Screen

sealed class AppEntryState {
    object Loading : AppEntryState()
    object Home : AppEntryState()
    object Locked : AppEntryState()
}

@Composable
fun AppRoot(
    viewModel: AppRootViewModel = hiltViewModel()
) {
    val entryState by viewModel.entryState.collectAsStateWithLifecycle()

    when (entryState) {
        AppEntryState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
        AppEntryState.Home -> NavGraph(startDestination = Screen.Home.route)
        AppEntryState.Locked -> NavGraph(startDestination = Screen.Lock.route)
    }
}
