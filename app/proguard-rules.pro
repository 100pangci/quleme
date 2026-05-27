# ProGuard Rules for quleme App

# Room
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt/Dagger
-keep class com.quleme.qulemeApplication
-keep class com.quleme.di.** { *; }
-keepnames class dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper$LayoutInflaterFactoryWrapper

# Data Classes (Gson serialization)
-keep class com.quleme.domain.model.** { *; }

# Compose
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.material3.** { *; }
