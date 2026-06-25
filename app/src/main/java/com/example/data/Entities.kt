package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageUrl: String,
    val shortDescription: String,
    val fullDetails: String,
    val pricePkr: Double,
    val priceUsd: Double,
    val buyLink: String,
    val whatsappLink: String = "",
    val externalLink: String = "",
    val rating: Float = 0f
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val username: String,
    val comment: String,
    val rating: Int,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "banners")
data class Banner(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val displayOrder: Int = 0
)

@Entity(tableName = "settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val appLogoUrl: String = "",
    val appName: String = "Ali Store",
    val telegramLink: String = "",
    val whatsappLink: String = "",
    val currencyEnabled: Boolean = true,
    val showFloatingWhatsapp: Boolean = true,
    val floatingWhatsappNumber: String = ""
)
