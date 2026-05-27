package com.quleme.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quleme.domain.repository.UserSettingsRepository
import com.quleme.ui.text.AppProfile
import com.quleme.ui.text.AppText
import com.quleme.ui.text.LauncherProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppRootViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _entryState = MutableStateFlow<AppEntryState>(AppEntryState.Loading)
    val entryState: StateFlow<AppEntryState> = _entryState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = userSettingsRepository.getSettings()
            val profile = AppProfile.fromRaw(settings?.appProfile)
            AppText.applyProfile(profile)
            LauncherProfileManager.applyProfile(appContext, profile)

            _entryState.value = if (settings?.lockEnabled == true) {
                AppEntryState.Locked
            } else {
                AppEntryState.Home
            }
        }
    }
}
