package com.quleme.domain.model

data class UserSettings(
    val age: Int,
    val lockEnabled: Boolean,
    val birthDate: String = "",
    val webDavUrl: String = "",
    val webDavUsername: String = "",
    val webDavPassword: String = "",
    val webDavDirectory: String = "",
    val appProfile: String = "BOY"
)
