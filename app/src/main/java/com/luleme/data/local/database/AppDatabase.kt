package com.luleme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.luleme.data.local.dao.RecordDao
import com.luleme.data.local.dao.UserSettingsDao
import com.luleme.data.local.entity.RecordEntity
import com.luleme.data.local.entity.UserSettingsEntity

@Database(entities = [RecordEntity::class, UserSettingsEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_settings ADD COLUMN birth_date TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_settings ADD COLUMN app_profile TEXT NOT NULL DEFAULT 'BOY'")
            }
        }
    }
}
