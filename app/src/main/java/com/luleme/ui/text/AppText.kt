package com.quleme.ui.text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.quleme.ui.text.boy.BoyText
import com.quleme.ui.text.girl.GirlText

object AppText {
    var currentProfile by mutableStateOf(AppProfile.BOY)
        private set

    fun applyProfile(profile: AppProfile) {
        currentProfile = profile
    }

    private inline fun <T> pick(
        boy: () -> T,
        girl: () -> T
    ): T = when (currentProfile) {
        AppProfile.BOY -> boy()
        AppProfile.GIRL -> girl()
    }

    // Common
    val CANCEL get() = pick({ BoyText.CANCEL }, { GirlText.CANCEL })
    val SAVE get() = pick({ BoyText.SAVE }, { GirlText.SAVE })
    val CONFIRM get() = pick({ BoyText.CONFIRM }, { GirlText.CONFIRM })
    val DELETE get() = pick({ BoyText.DELETE }, { GirlText.DELETE })
    val EDIT get() = pick({ BoyText.EDIT }, { GirlText.EDIT })

    // Navigation
    val NAV_HOME get() = pick({ BoyText.NAV_HOME }, { GirlText.NAV_HOME })
    val NAV_STATISTICS get() = pick({ BoyText.NAV_STATISTICS }, { GirlText.NAV_STATISTICS })
    val NAV_SETTINGS get() = pick({ BoyText.NAV_SETTINGS }, { GirlText.NAV_SETTINGS })

    // Auth / Lock
    val AUTH_TITLE get() = pick({ BoyText.AUTH_TITLE }, { GirlText.AUTH_TITLE })
    val AUTH_SUBTITLE get() = pick({ BoyText.AUTH_SUBTITLE }, { GirlText.AUTH_SUBTITLE })
    val AUTH_NEGATIVE get() = pick({ BoyText.AUTH_NEGATIVE }, { GirlText.AUTH_NEGATIVE })
    val LOCK_HINT_USE_SYSTEM_AUTH get() = pick({ BoyText.LOCK_HINT_USE_SYSTEM_AUTH }, { GirlText.LOCK_HINT_USE_SYSTEM_AUTH })
    val LOCK_HINT_UNSUPPORTED get() = pick({ BoyText.LOCK_HINT_UNSUPPORTED }, { GirlText.LOCK_HINT_UNSUPPORTED })
    val LOCK_HINT_ENABLE_SYSTEM_AUTH get() = pick({ BoyText.LOCK_HINT_ENABLE_SYSTEM_AUTH }, { GirlText.LOCK_HINT_ENABLE_SYSTEM_AUTH })
    val LOCK_HINT_FAILED get() = pick({ BoyText.LOCK_HINT_FAILED }, { GirlText.LOCK_HINT_FAILED })
    val LOCK_WELCOME_BACK get() = pick({ BoyText.LOCK_WELCOME_BACK }, { GirlText.LOCK_WELCOME_BACK })
    val LOCK_RETRY get() = pick({ BoyText.LOCK_RETRY }, { GirlText.LOCK_RETRY })
    val LOCK_GO_TO_SYSTEM_SETTINGS get() = pick({ BoyText.LOCK_GO_TO_SYSTEM_SETTINGS }, { GirlText.LOCK_GO_TO_SYSTEM_SETTINGS })

    // Home
    val HOME_TAKEOFF_ANIMATION_STYLE get() = pick({ BoyText.HOME_TAKEOFF_ANIMATION_STYLE }, { GirlText.HOME_TAKEOFF_ANIMATION_STYLE })
    val HOME_ERROR_PREFIX get() = pick({ BoyText.HOME_ERROR_PREFIX }, { GirlText.HOME_ERROR_PREFIX })
    val HOME_WEEK_OVERVIEW get() = pick({ BoyText.HOME_WEEK_OVERVIEW }, { GirlText.HOME_WEEK_OVERVIEW })
    val HOME_STATS_COUNT get() = pick({ BoyText.HOME_STATS_COUNT }, { GirlText.HOME_STATS_COUNT })
    val HOME_STATS_STATUS get() = pick({ BoyText.HOME_STATS_STATUS }, { GirlText.HOME_STATS_STATUS })
    val HOME_STATUS_SAGE_MODE get() = pick({ BoyText.HOME_STATUS_SAGE_MODE }, { GirlText.HOME_STATUS_SAGE_MODE })
    val HOME_STATUS_ACTIVE get() = pick({ BoyText.HOME_STATUS_ACTIVE }, { GirlText.HOME_STATUS_ACTIVE })
    val HOME_TAKEOFF get() = pick({ BoyText.HOME_TAKEOFF }, { GirlText.HOME_TAKEOFF })
    val HOME_TAKEOFF_AGAIN get() = pick({ BoyText.HOME_TAKEOFF_AGAIN }, { GirlText.HOME_TAKEOFF_AGAIN })
    val HOME_GREETING_DAWN get() = pick({ BoyText.HOME_GREETING_DAWN }, { GirlText.HOME_GREETING_DAWN })
    val HOME_GREETING_MORNING get() = pick({ BoyText.HOME_GREETING_MORNING }, { GirlText.HOME_GREETING_MORNING })
    val HOME_GREETING_NOON get() = pick({ BoyText.HOME_GREETING_NOON }, { GirlText.HOME_GREETING_NOON })
    val HOME_GREETING_AFTERNOON get() = pick({ BoyText.HOME_GREETING_AFTERNOON }, { GirlText.HOME_GREETING_AFTERNOON })
    val HOME_GREETING_EVENING get() = pick({ BoyText.HOME_GREETING_EVENING }, { GirlText.HOME_GREETING_EVENING })
    val HOME_TODAY_NOT_YET get() = pick({ BoyText.HOME_TODAY_NOT_YET }, { GirlText.HOME_TODAY_NOT_YET })
    val HOME_KEEP_MOOD get() = pick({ BoyText.HOME_KEEP_MOOD }, { GirlText.HOME_KEEP_MOOD })
    val HOME_DONT_FORGET_LOVE get() = pick({ BoyText.HOME_DONT_FORGET_LOVE }, { GirlText.HOME_DONT_FORGET_LOVE })
    val HOME_HEALTH_TIP_TITLE get() = pick({ BoyText.HOME_HEALTH_TIP_TITLE }, { GirlText.HOME_HEALTH_TIP_TITLE })
    val HOME_HEALTH_MSG_TODAY_MANY get() = pick({ BoyText.HOME_HEALTH_MSG_TODAY_MANY }, { GirlText.HOME_HEALTH_MSG_TODAY_MANY })
    val HOME_HEALTH_MSG_TODAY_ONCE get() = pick({ BoyText.HOME_HEALTH_MSG_TODAY_ONCE }, { GirlText.HOME_HEALTH_MSG_TODAY_ONCE })
    val HOME_HEALTH_MSG_FREQUENT get() = pick({ BoyText.HOME_HEALTH_MSG_FREQUENT }, { GirlText.HOME_HEALTH_MSG_FREQUENT })
    val HOME_HEALTH_MSG_HEALTHY get() = pick({ BoyText.HOME_HEALTH_MSG_HEALTHY }, { GirlText.HOME_HEALTH_MSG_HEALTHY })
    val HOME_UNKNOWN_ERROR get() = pick({ BoyText.HOME_UNKNOWN_ERROR }, { GirlText.HOME_UNKNOWN_ERROR })
    val HOME_DATE_PATTERN get() = pick({ BoyText.HOME_DATE_PATTERN }, { GirlText.HOME_DATE_PATTERN })

    fun homeTodayRecorded(count: Int): String = pick(
        { BoyText.homeTodayRecorded(count) },
        { GirlText.homeTodayRecorded(count) }
    )

    // Statistics
    val STAT_TAB_WEEK get() = pick({ BoyText.STAT_TAB_WEEK }, { GirlText.STAT_TAB_WEEK })
    val STAT_TAB_MONTH get() = pick({ BoyText.STAT_TAB_MONTH }, { GirlText.STAT_TAB_MONTH })
    val STAT_TAB_ALL get() = pick({ BoyText.STAT_TAB_ALL }, { GirlText.STAT_TAB_ALL })
    val STAT_ADD_RECORD get() = pick({ BoyText.STAT_ADD_RECORD }, { GirlText.STAT_ADD_RECORD })
    val STAT_EDIT_RECORD get() = pick({ BoyText.STAT_EDIT_RECORD }, { GirlText.STAT_EDIT_RECORD })
    val STAT_DELETE_CONFIRM_TITLE get() = pick({ BoyText.STAT_DELETE_CONFIRM_TITLE }, { GirlText.STAT_DELETE_CONFIRM_TITLE })
    val STAT_DELETE_CONFIRM_TEXT get() = pick({ BoyText.STAT_DELETE_CONFIRM_TEXT }, { GirlText.STAT_DELETE_CONFIRM_TEXT })
    val STAT_WEEK_RECORD get() = pick({ BoyText.STAT_WEEK_RECORD }, { GirlText.STAT_WEEK_RECORD })
    val STAT_CD_PREV_MONTH get() = pick({ BoyText.STAT_CD_PREV_MONTH }, { GirlText.STAT_CD_PREV_MONTH })
    val STAT_CD_NEXT_MONTH get() = pick({ BoyText.STAT_CD_NEXT_MONTH }, { GirlText.STAT_CD_NEXT_MONTH })
    val STAT_BACK_TO_CURRENT_MONTH get() = pick({ BoyText.STAT_BACK_TO_CURRENT_MONTH }, { GirlText.STAT_BACK_TO_CURRENT_MONTH })
    val STAT_NO_RECORD get() = pick({ BoyText.STAT_NO_RECORD }, { GirlText.STAT_NO_RECORD })
    val STAT_ADD_ONE_RECORD get() = pick({ BoyText.STAT_ADD_ONE_RECORD }, { GirlText.STAT_ADD_ONE_RECORD })
    val STAT_LABEL_DATE get() = pick({ BoyText.STAT_LABEL_DATE }, { GirlText.STAT_LABEL_DATE })
    val STAT_LABEL_TIME get() = pick({ BoyText.STAT_LABEL_TIME }, { GirlText.STAT_LABEL_TIME })
    val STAT_LABEL_NOTE_OPTIONAL get() = pick({ BoyText.STAT_LABEL_NOTE_OPTIONAL }, { GirlText.STAT_LABEL_NOTE_OPTIONAL })
    val STAT_ERROR_DATE_TIME_INVALID get() = pick({ BoyText.STAT_ERROR_DATE_TIME_INVALID }, { GirlText.STAT_ERROR_DATE_TIME_INVALID })
    val STAT_TOTAL get() = pick({ BoyText.STAT_TOTAL }, { GirlText.STAT_TOTAL })
    val STAT_MAX_STREAK get() = pick({ BoyText.STAT_MAX_STREAK }, { GirlText.STAT_MAX_STREAK })
    val STAT_AVG_FREQUENCY get() = pick({ BoyText.STAT_AVG_FREQUENCY }, { GirlText.STAT_AVG_FREQUENCY })
    val STAT_UNIT_TIMES get() = pick({ BoyText.STAT_UNIT_TIMES }, { GirlText.STAT_UNIT_TIMES })
    val STAT_UNIT_DAYS get() = pick({ BoyText.STAT_UNIT_DAYS }, { GirlText.STAT_UNIT_DAYS })
    val STAT_UNIT_TIMES_PER_WEEK get() = pick({ BoyText.STAT_UNIT_TIMES_PER_WEEK }, { GirlText.STAT_UNIT_TIMES_PER_WEEK })
    val STAT_ADVICE_LOW get() = pick({ BoyText.STAT_ADVICE_LOW }, { GirlText.STAT_ADVICE_LOW })
    val STAT_ADVICE_MEDIUM get() = pick({ BoyText.STAT_ADVICE_MEDIUM }, { GirlText.STAT_ADVICE_MEDIUM })
    val STAT_ADVICE_HIGH get() = pick({ BoyText.STAT_ADVICE_HIGH }, { GirlText.STAT_ADVICE_HIGH })
    val STAT_ADVICE_DEFAULT get() = pick({ BoyText.STAT_ADVICE_DEFAULT }, { GirlText.STAT_ADVICE_DEFAULT })
    val STAT_ADVICE_TITLE get() = pick({ BoyText.STAT_ADVICE_TITLE }, { GirlText.STAT_ADVICE_TITLE })
    val STAT_WEEKDAY_HEADERS get() = pick({ BoyText.STAT_WEEKDAY_HEADERS }, { GirlText.STAT_WEEKDAY_HEADERS })

    fun statMonthTitle(year: Int, month: Int): String = pick(
        { BoyText.statMonthTitle(year, month) },
        { GirlText.statMonthTitle(year, month) }
    )

    fun statMonthTotal(total: Int): String = pick(
        { BoyText.statMonthTotal(total) },
        { GirlText.statMonthTotal(total) }
    )

    fun statDayTitle(month: Int, day: Int): String = pick(
        { BoyText.statDayTitle(month, day) },
        { GirlText.statDayTitle(month, day) }
    )

    fun statDayCount(count: Int): String = pick(
        { BoyText.statDayCount(count) },
        { GirlText.statDayCount(count) }
    )

    // Settings
    val SETTINGS_ENABLE_SYSTEM_AUTH_FIRST get() = pick({ BoyText.SETTINGS_ENABLE_SYSTEM_AUTH_FIRST }, { GirlText.SETTINGS_ENABLE_SYSTEM_AUTH_FIRST })
    val SETTINGS_EXPORT_SUCCESS get() = pick({ BoyText.SETTINGS_EXPORT_SUCCESS }, { GirlText.SETTINGS_EXPORT_SUCCESS })
    val SETTINGS_EXPORT_FAILED get() = pick({ BoyText.SETTINGS_EXPORT_FAILED }, { GirlText.SETTINGS_EXPORT_FAILED })
    val SETTINGS_RESTORE_SUCCESS get() = pick({ BoyText.SETTINGS_RESTORE_SUCCESS }, { GirlText.SETTINGS_RESTORE_SUCCESS })
    val SETTINGS_RESTORE_INVALID get() = pick({ BoyText.SETTINGS_RESTORE_INVALID }, { GirlText.SETTINGS_RESTORE_INVALID })
    val SETTINGS_RESTORE_FAILED get() = pick({ BoyText.SETTINGS_RESTORE_FAILED }, { GirlText.SETTINGS_RESTORE_FAILED })
    val SETTINGS_CLEAR_ALL_TITLE get() = pick({ BoyText.SETTINGS_CLEAR_ALL_TITLE }, { GirlText.SETTINGS_CLEAR_ALL_TITLE })
    val SETTINGS_CLEAR_ALL_TEXT get() = pick({ BoyText.SETTINGS_CLEAR_ALL_TEXT }, { GirlText.SETTINGS_CLEAR_ALL_TEXT })
    val SETTINGS_CLEARED get() = pick({ BoyText.SETTINGS_CLEARED }, { GirlText.SETTINGS_CLEARED })
    val SETTINGS_CLEAR_CONFIRM get() = pick({ BoyText.SETTINGS_CLEAR_CONFIRM }, { GirlText.SETTINGS_CLEAR_CONFIRM })
    val SETTINGS_THINK_AGAIN get() = pick({ BoyText.SETTINGS_THINK_AGAIN }, { GirlText.SETTINGS_THINK_AGAIN })
    val SETTINGS_WEBDAV_CONFIG_TITLE get() = pick({ BoyText.SETTINGS_WEBDAV_CONFIG_TITLE }, { GirlText.SETTINGS_WEBDAV_CONFIG_TITLE })
    val SETTINGS_WEBDAV_SERVER_URL get() = pick({ BoyText.SETTINGS_WEBDAV_SERVER_URL }, { GirlText.SETTINGS_WEBDAV_SERVER_URL })
    val SETTINGS_WEBDAV_USERNAME get() = pick({ BoyText.SETTINGS_WEBDAV_USERNAME }, { GirlText.SETTINGS_WEBDAV_USERNAME })
    val SETTINGS_WEBDAV_PASSWORD get() = pick({ BoyText.SETTINGS_WEBDAV_PASSWORD }, { GirlText.SETTINGS_WEBDAV_PASSWORD })
    val SETTINGS_WEBDAV_NEW_PASSWORD get() = pick({ BoyText.SETTINGS_WEBDAV_NEW_PASSWORD }, { GirlText.SETTINGS_WEBDAV_NEW_PASSWORD })
    val SETTINGS_WEBDAV_HINT get() = pick({ BoyText.SETTINGS_WEBDAV_HINT }, { GirlText.SETTINGS_WEBDAV_HINT })
    val SETTINGS_WEBDAV_DIRECTORY_OPTIONAL get() = pick({ BoyText.SETTINGS_WEBDAV_DIRECTORY_OPTIONAL }, { GirlText.SETTINGS_WEBDAV_DIRECTORY_OPTIONAL })
    val SETTINGS_WEBDAV_CONFIG_SAVED get() = pick({ BoyText.SETTINGS_WEBDAV_CONFIG_SAVED }, { GirlText.SETTINGS_WEBDAV_CONFIG_SAVED })
    val SETTINGS_WEBDAV_CONFIG_SAVE_FAILED get() = pick({ BoyText.SETTINGS_WEBDAV_CONFIG_SAVE_FAILED }, { GirlText.SETTINGS_WEBDAV_CONFIG_SAVE_FAILED })
    val SETTINGS_WEBDAV_RESTORE_TITLE get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE_TITLE }, { GirlText.SETTINGS_WEBDAV_RESTORE_TITLE })
    val SETTINGS_WEBDAV_RESTORE_TEXT get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE_TEXT }, { GirlText.SETTINGS_WEBDAV_RESTORE_TEXT })
    val SETTINGS_WEBDAV_RESTORE_CONFIRM get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE_CONFIRM }, { GirlText.SETTINGS_WEBDAV_RESTORE_CONFIRM })
    val SETTINGS_WEBDAV_RESTORE_SUCCESS get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE_SUCCESS }, { GirlText.SETTINGS_WEBDAV_RESTORE_SUCCESS })
    val SETTINGS_WEBDAV_RESTORE_FAILED get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE_FAILED }, { GirlText.SETTINGS_WEBDAV_RESTORE_FAILED })
    val SETTINGS_TITLE get() = pick({ BoyText.SETTINGS_TITLE }, { GirlText.SETTINGS_TITLE })
    val SETTINGS_GROUP_PROFILE get() = pick({ BoyText.SETTINGS_GROUP_PROFILE }, { GirlText.SETTINGS_GROUP_PROFILE })
    val SETTINGS_BIRTHDAY_PICK get() = pick({ BoyText.SETTINGS_BIRTHDAY_PICK }, { GirlText.SETTINGS_BIRTHDAY_PICK })
    val SETTINGS_CURRENT_AGE get() = pick({ BoyText.SETTINGS_CURRENT_AGE }, { GirlText.SETTINGS_CURRENT_AGE })
    val SETTINGS_GROUP_SECURITY get() = pick({ BoyText.SETTINGS_GROUP_SECURITY }, { GirlText.SETTINGS_GROUP_SECURITY })
    val SETTINGS_APP_LOCK get() = pick({ BoyText.SETTINGS_APP_LOCK }, { GirlText.SETTINGS_APP_LOCK })
    val SETTINGS_APP_LOCK_SUBTITLE get() = pick({ BoyText.SETTINGS_APP_LOCK_SUBTITLE }, { GirlText.SETTINGS_APP_LOCK_SUBTITLE })
    val SETTINGS_GROUP_DATA get() = pick({ BoyText.SETTINGS_GROUP_DATA }, { GirlText.SETTINGS_GROUP_DATA })
    val SETTINGS_BACKUP_DATA get() = pick({ BoyText.SETTINGS_BACKUP_DATA }, { GirlText.SETTINGS_BACKUP_DATA })
    val SETTINGS_BACKUP_DATA_SUBTITLE get() = pick({ BoyText.SETTINGS_BACKUP_DATA_SUBTITLE }, { GirlText.SETTINGS_BACKUP_DATA_SUBTITLE })
    val SETTINGS_RESTORE_DATA get() = pick({ BoyText.SETTINGS_RESTORE_DATA }, { GirlText.SETTINGS_RESTORE_DATA })
    val SETTINGS_RESTORE_DATA_SUBTITLE get() = pick({ BoyText.SETTINGS_RESTORE_DATA_SUBTITLE }, { GirlText.SETTINGS_RESTORE_DATA_SUBTITLE })
    val SETTINGS_CLEAR_ALL get() = pick({ BoyText.SETTINGS_CLEAR_ALL }, { GirlText.SETTINGS_CLEAR_ALL })
    val SETTINGS_CLEAR_ALL_SUBTITLE get() = pick({ BoyText.SETTINGS_CLEAR_ALL_SUBTITLE }, { GirlText.SETTINGS_CLEAR_ALL_SUBTITLE })
    val SETTINGS_GROUP_WEBDAV get() = pick({ BoyText.SETTINGS_GROUP_WEBDAV }, { GirlText.SETTINGS_GROUP_WEBDAV })
    val SETTINGS_WEBDAV_CONFIG get() = pick({ BoyText.SETTINGS_WEBDAV_CONFIG }, { GirlText.SETTINGS_WEBDAV_CONFIG })
    val SETTINGS_WEBDAV_NOT_CONFIGURED get() = pick({ BoyText.SETTINGS_WEBDAV_NOT_CONFIGURED }, { GirlText.SETTINGS_WEBDAV_NOT_CONFIGURED })
    val SETTINGS_WEBDAV_TEST get() = pick({ BoyText.SETTINGS_WEBDAV_TEST }, { GirlText.SETTINGS_WEBDAV_TEST })
    val SETTINGS_WEBDAV_TEST_SUBTITLE get() = pick({ BoyText.SETTINGS_WEBDAV_TEST_SUBTITLE }, { GirlText.SETTINGS_WEBDAV_TEST_SUBTITLE })
    val SETTINGS_WEBDAV_TEST_OK get() = pick({ BoyText.SETTINGS_WEBDAV_TEST_OK }, { GirlText.SETTINGS_WEBDAV_TEST_OK })
    val SETTINGS_WEBDAV_TEST_FAILED get() = pick({ BoyText.SETTINGS_WEBDAV_TEST_FAILED }, { GirlText.SETTINGS_WEBDAV_TEST_FAILED })
    val SETTINGS_WEBDAV_BACKUP get() = pick({ BoyText.SETTINGS_WEBDAV_BACKUP }, { GirlText.SETTINGS_WEBDAV_BACKUP })
    val SETTINGS_WEBDAV_BACKUP_SUBTITLE get() = pick({ BoyText.SETTINGS_WEBDAV_BACKUP_SUBTITLE }, { GirlText.SETTINGS_WEBDAV_BACKUP_SUBTITLE })
    val SETTINGS_WEBDAV_BACKUP_OK get() = pick({ BoyText.SETTINGS_WEBDAV_BACKUP_OK }, { GirlText.SETTINGS_WEBDAV_BACKUP_OK })
    val SETTINGS_WEBDAV_BACKUP_FAILED get() = pick({ BoyText.SETTINGS_WEBDAV_BACKUP_FAILED }, { GirlText.SETTINGS_WEBDAV_BACKUP_FAILED })
    val SETTINGS_WEBDAV_RESTORE get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE }, { GirlText.SETTINGS_WEBDAV_RESTORE })
    val SETTINGS_WEBDAV_RESTORE_SUBTITLE get() = pick({ BoyText.SETTINGS_WEBDAV_RESTORE_SUBTITLE }, { GirlText.SETTINGS_WEBDAV_RESTORE_SUBTITLE })

    fun settingsBirthDateText(date: String): String = pick(
        { BoyText.settingsBirthDateText(date) },
        { GirlText.settingsBirthDateText(date) }
    )

    fun settingsAgeText(age: Int): String = pick(
        { BoyText.settingsAgeText(age) },
        { GirlText.settingsAgeText(age) }
    )

    // Data layer errors
    val WEBDAV_AUTH_FAILED get() = pick({ BoyText.WEBDAV_AUTH_FAILED }, { GirlText.WEBDAV_AUTH_FAILED })
    val WEBDAV_NOT_FOUND get() = pick({ BoyText.WEBDAV_NOT_FOUND }, { GirlText.WEBDAV_NOT_FOUND })

    fun webDavConnectionFailed(code: Int): String = pick(
        { BoyText.webDavConnectionFailed(code) },
        { GirlText.webDavConnectionFailed(code) }
    )

    fun webDavUploadFailed(code: Int): String = pick(
        { BoyText.webDavUploadFailed(code) },
        { GirlText.webDavUploadFailed(code) }
    )

    fun webDavDownloadFailed(code: Int): String = pick(
        { BoyText.webDavDownloadFailed(code) },
        { GirlText.webDavDownloadFailed(code) }
    )

    // profile UI (shared fixed text)
    const val SETTINGS_GROUP_STYLE = "外观与风格"
    const val SETTINGS_PROFILE_SWITCH_TITLE = "性别配置"
    const val SETTINGS_PROFILE_SWITCH_SUBTITLE = "你是绅士还是淑女？"
    const val SETTINGS_PROFILE_BOY = "绅士"
    const val SETTINGS_PROFILE_GIRL = "淑女"
}
