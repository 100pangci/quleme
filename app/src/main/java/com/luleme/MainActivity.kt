package com.luleme

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.luleme.ui.AppRoot
import com.luleme.ui.theme.BackgroundDarkArgb
import com.luleme.ui.theme.BackgroundLightArgb
import com.luleme.ui.theme.LulemeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = BackgroundLightArgb,
                darkScrim = BackgroundDarkArgb
            )
        )
        setContent {
            LulemeTheme {
                AppRoot()
            }
        }
    }
}
