package com.luleme.ui.text

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object LauncherProfileManager {

    private const val ALIAS_BOY = "com.luleme.MainActivityBoy"
    private const val ALIAS_GIRL = "com.luleme.MainActivityGirl"

    fun applyProfile(context: Context, profile: AppProfile) {
        val packageManager = context.packageManager
        val boyComponent = ComponentName(context, ALIAS_BOY)
        val girlComponent = ComponentName(context, ALIAS_GIRL)

        when (profile) {
            AppProfile.BOY -> {
                setComponentEnabled(packageManager, boyComponent, true)
                setComponentEnabled(packageManager, girlComponent, false)
            }

            AppProfile.GIRL -> {
                setComponentEnabled(packageManager, boyComponent, false)
                setComponentEnabled(packageManager, girlComponent, true)
            }
        }
    }

    private fun setComponentEnabled(
        packageManager: PackageManager,
        componentName: ComponentName,
        enabled: Boolean
    ) {
        val newState = if (enabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        if (packageManager.getComponentEnabledSetting(componentName) != newState) {
            packageManager.setComponentEnabledSetting(
                componentName,
                newState,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}
