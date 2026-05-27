package com.quleme.data.repository

import com.quleme.data.local.dao.UserSettingsDao
import com.quleme.data.local.entity.UserSettingsEntity
import com.quleme.domain.model.UserSettings
import com.quleme.domain.repository.UserSettingsRepository
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val dao: UserSettingsDao
) : UserSettingsRepository {

    override suspend fun getSettings(): UserSettings? {
        return dao.getSettings()?.toDomain()
    }

    override suspend fun saveSettings(settings: UserSettings) {
        dao.saveSettings(settings.toEntity())
    }

    private fun UserSettingsEntity.toDomain(): UserSettings {
        return UserSettings(
            age = this.age,
            lockEnabled = this.lockEnabled,
            birthDate = this.birthDate,
            webDavUrl = this.webDavUrl,
            webDavUsername = this.webDavUsername,
            webDavPassword = this.webDavPassword,
            webDavDirectory = this.webDavDirectory,
            appProfile = this.appProfile
        )
    }

    private fun UserSettings.toEntity(): UserSettingsEntity {
        return UserSettingsEntity(
            age = this.age,
            lockEnabled = this.lockEnabled,
            birthDate = this.birthDate,
            webDavUrl = this.webDavUrl,
            webDavUsername = this.webDavUsername,
            webDavPassword = this.webDavPassword,
            webDavDirectory = this.webDavDirectory,
            appProfile = this.appProfile
        )
    }
}
