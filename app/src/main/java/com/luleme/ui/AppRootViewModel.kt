package com.quleme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quleme.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppRootViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _entryState = MutableStateFlow<AppEntryState>(AppEntryState.Loading)
    val entryState: StateFlow<AppEntryState> = _entryState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = userSettingsRepository.getSettings()
            _entryState.value = if (settings?.lockEnabled == true) {
                AppEntryState.Locked
            } else {
                AppEntryState.Home
            }
        }
    }
}
