package com.quleme.domain.repository

import com.quleme.domain.model.UserSettings

interface UserSettingsRepository {
    suspend fun getSettings(): UserSettings?
    suspend fun saveSettings(settings: UserSettings)
}
