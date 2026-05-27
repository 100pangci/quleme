package com.luleme.ui.text

enum class AppProfile {
    BOY,
    GIRL;

    companion object {
        fun fromRaw(value: String?): AppProfile {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BOY
        }
    }
}
