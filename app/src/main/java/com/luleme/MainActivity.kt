package com.quleme

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.quleme.ui.AppRoot
import com.quleme.ui.theme.BackgroundDarkArgb
import com.quleme.ui.theme.BackgroundLightArgb
import com.quleme.ui.theme.qulemeTheme
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
            qulemeTheme {
                AppRoot()
            }
        }
    }
}
