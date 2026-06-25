package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Product::class, Review::class, Banner::class, AppSettings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun reviewDao(): ReviewDao
    abstract fun bannerDao(): BannerDao
    abstract fun settingsDao(): SettingsDao
}
