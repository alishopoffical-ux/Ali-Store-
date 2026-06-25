package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.StoreRepository
import com.example.data.AppSettings
import com.example.data.Banner
import com.example.data.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StoreApplication : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: StoreRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "store_database"
        ).build()
        repository = StoreRepository(database)

        // Seed default settings and mock data if empty
        CoroutineScope(Dispatchers.IO).launch {
            val settings = repository.getSettings().firstOrNull()
            if (settings == null) {
                repository.saveSettings(AppSettings())
            }
            
            val banners = repository.getAllBanners().firstOrNull()
            if (banners.isNullOrEmpty()) {
                repository.insertBanner(Banner(imageUrl = "android.resource://$packageName/drawable/ai_tools_banner_1782279035822"))
            }

            val products = repository.getAllProducts().firstOrNull()
            if (products.isNullOrEmpty()) {
                repository.insertProduct(Product(
                    name = "Gemini Pro Assistant",
                    imageUrl = "android.resource://$packageName/drawable/ali_store_logo_1782279014665",
                    shortDescription = "Advanced AI coding and writing assistant.",
                    fullDetails = "Features:\\n- Subscription Duration: Lifetime\\n- Unlimited queries\\n- Premium Benefits\\n- Free Updates\\n- 24/7 Support",
                    pricePkr = 5000.0,
                    priceUsd = 18.0,
                    buyLink = "https://example.com/buy",
                    whatsappLink = "https://wa.me/1234567890",
                    externalLink = "https://example.com",
                    rating = 4.8f
                ))
            }
        }
    }
}
