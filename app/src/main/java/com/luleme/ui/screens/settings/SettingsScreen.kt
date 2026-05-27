package com.quleme.ui.screens.settings

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quleme.BuildConfig
import com.quleme.ui.components.CuteSwitch
import com.quleme.ui.components.SettingGroup
import com.quleme.ui.components.SettingItem
import com.quleme.ui.auth.SystemAuth
import com.quleme.ui.text.AppProfile
import com.quleme.ui.text.AppText
import com.quleme.ui.theme.CutePink
import com.quleme.ui.theme.SecondaryLight
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    
    var showClearDialog by remember { mutableStateOf(false) }
    var showWebDavDialog by remember { mutableStateOf(false) }
    var showWebDavRestoreDialog by remember { mutableStateOf(false) }
    var showBirthDateDialog by remember { mutableStateOf(false) }
    var webDavBusy by remember { mutableStateOf(false) }

    fun toggleSystemLock(enabled: Boolean) {
        if (enabled) {
            if (!SystemAuth.canAuthenticate(context)) {
                Toast.makeText(context, AppText.SETTINGS_ENABLE_SYSTEM_AUTH_FIRST, Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                return
            }
        }
        viewModel.toggleLock(enabled)
    }

    // Export/Import Launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = viewModel.getAllRecordsJson()
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        stream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: throw IllegalStateException("Unable to open backup file")
                    Toast.makeText(context, AppText.SETTINGS_EXPORT_SUCCESS, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, AppText.SETTINGS_EXPORT_FAILED, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val json = stream.bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
                        if (viewModel.restoreData(json)) {
                            Toast.makeText(context, AppText.SETTINGS_RESTORE_SUCCESS, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, AppText.SETTINGS_RESTORE_INVALID, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, AppText.SETTINGS_RESTORE_FAILED, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(AppText.SETTINGS_CLEAR_ALL_TITLE) },
            text = { Text(AppText.SETTINGS_CLEAR_ALL_TEXT) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                        Toast.makeText(context, AppText.SETTINGS_CLEARED, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(AppText.SETTINGS_CLEAR_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(AppText.SETTINGS_THINK_AGAIN) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showWebDavDialog) {
        var url by remember(showWebDavDialog, uiState.webDavUrl) { mutableStateOf(uiState.webDavUrl) }
        var username by remember(showWebDavDialog, uiState.webDavUsername) { mutableStateOf(uiState.webDavUsername) }
        var password by remember(showWebDavDialog) { mutableStateOf("") }
        var directory by remember(showWebDavDialog, uiState.webDavDirectory) { mutableStateOf(uiState.webDavDirectory) }

        AlertDialog(
            onDismissRequest = { if (!webDavBusy) showWebDavDialog = false },
            title = { Text(AppText.SETTINGS_WEBDAV_CONFIG_TITLE) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text(AppText.SETTINGS_WEBDAV_SERVER_URL) },
                        placeholder = { Text("https://example.com/dav") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(AppText.SETTINGS_WEBDAV_USERNAME) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (uiState.webDavPasswordSaved) AppText.SETTINGS_WEBDAV_NEW_PASSWORD else AppText.SETTINGS_WEBDAV_PASSWORD) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = AppText.SETTINGS_WEBDAV_HINT,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = directory,
                        onValueChange = { directory = it },
                        label = { Text(AppText.SETTINGS_WEBDAV_DIRECTORY_OPTIONAL) },
                        placeholder = { Text("quleme") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            webDavBusy = true
                            val saved = viewModel.saveWebDavConfig(url, username, password, directory)
                            webDavBusy = false
                            if (saved) {
                                showWebDavDialog = false
                                Toast.makeText(context, AppText.SETTINGS_WEBDAV_CONFIG_SAVED, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, AppText.SETTINGS_WEBDAV_CONFIG_SAVE_FAILED, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !webDavBusy
                ) { Text(AppText.SAVE) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWebDavDialog = false },
                    enabled = !webDavBusy
                ) { Text(AppText.CANCEL) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showWebDavRestoreDialog) {
        AlertDialog(
            onDismissRequest = { if (!webDavBusy) showWebDavRestoreDialog = false },
            title = { Text(AppText.SETTINGS_WEBDAV_RESTORE_TITLE) },
            text = { Text(AppText.SETTINGS_WEBDAV_RESTORE_TEXT) },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            webDavBusy = true
                            val restored = viewModel.restoreFromWebDav()
                            webDavBusy = false
                            showWebDavRestoreDialog = false
                            Toast.makeText(
                                context,
                                if (restored) AppText.SETTINGS_WEBDAV_RESTORE_SUCCESS else AppText.SETTINGS_WEBDAV_RESTORE_FAILED,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !webDavBusy
                ) { Text(AppText.SETTINGS_WEBDAV_RESTORE_CONFIRM) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWebDavRestoreDialog = false },
                    enabled = !webDavBusy
                ) { Text(AppText.CANCEL) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val persistedBirthDate = remember(uiState.birthDate) {
        runCatching { LocalDate.parse(uiState.birthDate, DateTimeFormatter.ISO_DATE) }.getOrNull()
    }
    var birthDate by remember(uiState.birthDate) {
        mutableStateOf(
            persistedBirthDate ?: LocalDate.now().minusYears(uiState.age.toLong())
        )
    }
    val displayAge = remember(birthDate) {
        Period.between(birthDate, LocalDate.now()).years.coerceIn(0, 120)
    }

    if (showBirthDateDialog) {
        val initialMillis = remember(birthDate) {
            birthDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showBirthDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val selectedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            birthDate = selectedDate
                            viewModel.updateBirthDate(selectedDate)
                        }
                        showBirthDateDialog = false
                    }
                ) { Text(AppText.CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { showBirthDateDialog = false }) { Text(AppText.CANCEL) }
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp)
    ) {
        item {
            Text(
                text = AppText.SETTINGS_TITLE,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        item {
            SettingGroup(title = AppText.SETTINGS_GROUP_PROFILE) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBirthDateDialog = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Column {
                            Text(
                                text = AppText.settingsBirthDateText(birthDate.format(dateFormatter)),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = AppText.SETTINGS_BIRTHDAY_PICK,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppText.SETTINGS_CURRENT_AGE,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = AppText.settingsAgeText(displayAge),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item {
            SettingGroup(title = AppText.SETTINGS_GROUP_STYLE) {
                SettingItem(
                    icon = Icons.Rounded.Face,
                    title = AppText.SETTINGS_PROFILE_SWITCH_TITLE,
                    subtitle = AppText.SETTINGS_PROFILE_SWITCH_SUBTITLE,
                    iconTint = MaterialTheme.colorScheme.primary,
                    trailingContent = {
                        CuteSwitch(
                            checked = uiState.appProfile == AppProfile.GIRL,
                            onCheckedChange = { checked ->
                                viewModel.switchProfile(if (checked) AppProfile.GIRL else AppProfile.BOY)
                            }
                        )
                    },
                    onClick = {
                        val target = if (uiState.appProfile == AppProfile.BOY) AppProfile.GIRL else AppProfile.BOY
                        viewModel.switchProfile(target)
                    }
                )
            }
        }

        item {
            SettingGroup(title = AppText.SETTINGS_GROUP_SECURITY) {
                SettingItem(
                    icon = Icons.Rounded.Lock,
                    title = AppText.SETTINGS_APP_LOCK,
                    subtitle = AppText.SETTINGS_APP_LOCK_SUBTITLE,
                    iconTint = CutePink,
                    trailingContent = {
                        CuteSwitch(
                            checked = uiState.lockEnabled,
                            onCheckedChange = { toggleSystemLock(it) }
                        )
                    },
                    onClick = { toggleSystemLock(!uiState.lockEnabled) }
                )
            }
        }

        item {
            SettingGroup(title = AppText.SETTINGS_GROUP_DATA) {
                SettingItem(
                    icon = Icons.Rounded.Download,
                    title = AppText.SETTINGS_BACKUP_DATA,
                    subtitle = AppText.SETTINGS_BACKUP_DATA_SUBTITLE,
                    iconTint = SecondaryLight,
                    onClick = { exportLauncher.launch("quleme_data.json") }
                )
                
                SettingItem(
                    icon = Icons.Rounded.Upload,
                    title = AppText.SETTINGS_RESTORE_DATA,
                    subtitle = AppText.SETTINGS_RESTORE_DATA_SUBTITLE,
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/json", "text/plain")) }
                )
                
                SettingItem(
                    icon = Icons.Rounded.Delete,
                    title = AppText.SETTINGS_CLEAR_ALL,
                    subtitle = AppText.SETTINGS_CLEAR_ALL_SUBTITLE,
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = { showClearDialog = true }
                )
            }
        }

        item {
            SettingGroup(title = AppText.SETTINGS_GROUP_WEBDAV) {
                SettingItem(
                    icon = Icons.Rounded.Lock,
                    title = AppText.SETTINGS_WEBDAV_CONFIG,
                    subtitle = if (uiState.webDavUrl.isBlank()) AppText.SETTINGS_WEBDAV_NOT_CONFIGURED else uiState.webDavUrl,
                    iconTint = CutePink,
                    onClick = { showWebDavDialog = true }
                )

                SettingItem(
                    icon = Icons.Rounded.Upload,
                    title = AppText.SETTINGS_WEBDAV_TEST,
                    subtitle = AppText.SETTINGS_WEBDAV_TEST_SUBTITLE,
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (!webDavBusy) {
                            scope.launch {
                                webDavBusy = true
                                val ok = viewModel.testWebDavConnection()
                                webDavBusy = false
                                Toast.makeText(
                                    context,
                                    if (ok) AppText.SETTINGS_WEBDAV_TEST_OK else AppText.SETTINGS_WEBDAV_TEST_FAILED,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                SettingItem(
                    icon = Icons.Rounded.Upload,
                    title = AppText.SETTINGS_WEBDAV_BACKUP,
                    subtitle = AppText.SETTINGS_WEBDAV_BACKUP_SUBTITLE,
                    iconTint = SecondaryLight,
                    onClick = {
                        if (!webDavBusy) {
                            scope.launch {
                                webDavBusy = true
                                val ok = viewModel.backupToWebDav()
                                webDavBusy = false
                                Toast.makeText(
                                    context,
                                    if (ok) AppText.SETTINGS_WEBDAV_BACKUP_OK else AppText.SETTINGS_WEBDAV_BACKUP_FAILED,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                SettingItem(
                    icon = Icons.Rounded.Download,
                    title = AppText.SETTINGS_WEBDAV_RESTORE,
                    subtitle = AppText.SETTINGS_WEBDAV_RESTORE_SUBTITLE,
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = { if (!webDavBusy) showWebDavRestoreDialog = true }
                )
            }
        }
        
        item {
            Text(
                text = "quleme v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clickable { uriHandler.openUri("https://github.com/sky22333/quleme") },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
