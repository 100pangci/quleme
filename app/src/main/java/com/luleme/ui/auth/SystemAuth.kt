package com.luleme.ui.auth

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.luleme.ui.text.AppText

object SystemAuth {
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

    fun canAuthenticate(context: Context): Boolean {
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return keyguardManager?.isDeviceSecure == true ||
                BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                    BiometricManager.BIOMETRIC_SUCCESS
        }
        return BiometricManager.from(context).canAuthenticate(authenticators) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    fun promptInfo(context: Context): BiometricPrompt.PromptInfo {
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(AppText.AUTH_TITLE)
            .setSubtitle(AppText.AUTH_SUBTITLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setAllowedAuthenticators(authenticators)
        } else if (keyguardManager?.isDeviceSecure == true) {
            @Suppress("DEPRECATION")
            builder.setDeviceCredentialAllowed(true)
        } else {
            builder.setNegativeButtonText(AppText.AUTH_NEGATIVE)
        }

        return builder.build()
    }
}
