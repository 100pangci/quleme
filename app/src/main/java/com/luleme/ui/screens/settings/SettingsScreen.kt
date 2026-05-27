package com.quleme.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quleme.BuildConfig
import com.quleme.ui.auth.SystemAuth
import com.quleme.ui.components.CuteSwitch
import com.quleme.ui.components.SettingGroup
import com.quleme.ui.components.SettingItem
import com.quleme.ui.text.AppProfile
import com.quleme.ui.text.AppText
import com.quleme.ui.theme.CutePink
import com.quleme.ui.theme.SecondaryLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// 静态的 Formatter，不需要每次重组都创建
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    // --- UI 弹窗状态 ---
    var showClearDialog by remember { mutableStateOf(false) }
    var showWebDavDialog by remember { mutableStateOf(false) }
    var showWebDavRestoreDialog by remember { mutableStateOf(false) }
    var showBirthDateDialog by remember { mutableStateOf(false) }
    var webDavBusy by remember { mutableStateOf(false) }

    // --- 计算属性 ---
    val birthDate = remember(uiState.birthDate, uiState.age) {
        runCatching { LocalDate.parse(uiState.birthDate, DateTimeFormatter.ISO_DATE) }
            .getOrNull() ?: LocalDate.now().minusYears(uiState.age.toLong())
    }
    val displayAge = remember(birthDate) {
        Period.between(birthDate, LocalDate.now()).years.coerceIn(0, 120)
    }

    // --- 逻辑辅助函数 ---
    fun showToast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun toggleSystemLock(enabled: Boolean) {
        if (enabled && !SystemAuth.canAuthenticate(context)) {
            showToast(AppText.SETTINGS_ENABLE_SYSTEM_AUTH_FIRST)
            context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
            return
        }
        viewModel.toggleLock(enabled)
    }

    // --- 文件导入/导出 Launchers ---
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = viewModel.getAllRecordsJson()
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        stream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: throw IllegalStateException("Stream is null")
                    showToast(AppText.SETTINGS_EXPORT_SUCCESS)
                } catch (e: Exception) {
                    showToast(AppText.SETTINGS_EXPORT_FAILED)
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val json = stream.bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
                        val success = viewModel.restoreData(json)
                        showToast(if (success) AppText.SETTINGS_RESTORE_SUCCESS else AppText.SETTINGS_RESTORE_INVALID)
                    }
                } catch (e: Exception) {
                    showToast(AppText.SETTINGS_RESTORE_FAILED)
                }
            }
        }
    }

    // --- 独立的 Dialog 组件 (见下方定义) ---
    if (showClearDialog) {
        ClearDataDialog(
            onDismiss = { showClearDialog = false },
            onConfirm = {
                viewModel.clearAllData()
                showClearDialog = false
                showToast(AppText.SETTINGS_CLEARED)
            }
        )
    }

    if (showWebDavDialog) {
        WebDavConfigDialog(
            uiState = uiState,
            isBusy = webDavBusy,
            onDismiss = { if (!webDavBusy) showWebDavDialog = false },
            onSave = { url, user, pwd, dir ->
                scope.launch {
                    webDavBusy = true
                    val saved = viewModel.saveWebDavConfig(url, user, pwd, dir)
                    webDavBusy = false
                    if (saved) {
                        showWebDavDialog = false
                        showToast(AppText.SETTINGS_WEBDAV_CONFIG_SAVED)
                    } else {
                        showToast(AppText.SETTINGS_WEBDAV_CONFIG_SAVE_FAILED)
                    }
                }
            }
        )
    }

    if (showWebDavRestoreDialog) {
        WebDavRestoreDialog(
            isBusy = webDavBusy,
            onDismiss = { if (!webDavBusy) showWebDavRestoreDialog = false },
            onConfirm = {
                scope.launch {
                    webDavBusy = true
                    val restored = viewModel.restoreFromWebDav()
                    webDavBusy = false
                    showWebDavRestoreDialog = false
                    showToast(if (restored) AppText.SETTINGS_WEBDAV_RESTORE_SUCCESS else AppText.SETTINGS_WEBDAV_RESTORE_FAILED)
                }
            }
        )
    }

    if (showBirthDateDialog) {
        BirthDateDialog(
            initialDate = birthDate,
            onDismiss = { showBirthDateDialog = false },
            onConfirm = { selectedDate ->
                viewModel.updateBirthDate(selectedDate)
                showBirthDateDialog = false
            }
        )
    }

    // --- 主 UI 布局 ---
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
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
                        Spacer(modifier = Modifier.width(16.dp))
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppText.SETTINGS_CURRENT_AGE,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                    icon = Icons.Rounded.SwapHoriz,
                    title = AppText.SETTINGS_PROFILE_SWITCH_TITLE,
                    subtitle = AppText.SETTINGS_PROFILE_SWITCH_SUBTITLE,
                    iconTint = MaterialTheme.colorScheme.primary,
                    trailingContent = {
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                selected = uiState.appProfile == AppProfile.BOY,
                                onClick = { viewModel.switchProfile(AppProfile.BOY) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                icon = { SegmentedButtonDefaults.Icon(uiState.appProfile == AppProfile.BOY) },
                                label = { Text(AppText.SETTINGS_PROFILE_BOY) }
                            )
                            SegmentedButton(
                                selected = uiState.appProfile == AppProfile.GIRL,
                                onClick = { viewModel.switchProfile(AppProfile.GIRL) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                icon = { SegmentedButtonDefaults.Icon(uiState.appProfile == AppProfile.GIRL) },
                                label = { Text(AppText.SETTINGS_PROFILE_GIRL) }
                            )
                        }
                    },
                    onClick = {}
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
                                showToast(if (ok) AppText.SETTINGS_WEBDAV_TEST_OK else AppText.SETTINGS_WEBDAV_TEST_FAILED)
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
                                showToast(if (ok) AppText.SETTINGS_WEBDAV_BACKUP_OK else AppText.SETTINGS_WEBDAV_BACKUP_FAILED)
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
                    .clickable { uriHandler.openUri("https://github.com/sky22333/luleme") },
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 提取出的独立 Dialog 组件，保持主函数整洁
// ==========================================

@Composable
private fun ClearDataDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppText.SETTINGS_CLEAR_ALL_TITLE) },
        text = { Text(AppText.SETTINGS_CLEAR_ALL_TEXT) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(AppText.SETTINGS_CLEAR_CONFIRM) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppText.SETTINGS_THINK_AGAIN) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun WebDavConfigDialog(
    uiState: SettingsUiState,
    isBusy: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    // 将状态下沉到这个专门的 Dialog 里
    var url by remember { mutableStateOf(uiState.webDavUrl) }
    var username by remember { mutableStateOf(uiState.webDavUsername) }
    var password by remember { mutableStateOf("") }
    var directory by remember { mutableStateOf(uiState.webDavDirectory) }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                onClick = { onSave(url, username, password, directory) },
                enabled = !isBusy
            ) {
                if (isBusy) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Text(AppText.SAVE)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isBusy) { Text(AppText.CANCEL) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun WebDavRestoreDialog(
    isBusy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppText.SETTINGS_WEBDAV_RESTORE_TITLE) },
        text = { Text(AppText.SETTINGS_WEBDAV_RESTORE_TEXT) },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isBusy) {
                if (isBusy) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Text(AppText.SETTINGS_WEBDAV_RESTORE_CONFIRM)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isBusy) { Text(AppText.CANCEL) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDateDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val initialMillis = remember(initialDate) {
        initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onConfirm(selectedDate)
                    }
                }
            ) { Text(AppText.CONFIRM) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppText.CANCEL) }
        },
        shape = RoundedCornerShape(24.dp)
    ) {
        DatePicker(state = datePickerState)
    }
}
