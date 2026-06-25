package com.example.data

import kotlinx.coroutines.flow.Flow

class StoreRepository(private val db: AppDatabase) {
    // Products
    fun getAllProducts(): Flow<List<Product>> = db.productDao().getAllProducts()
    fun searchProducts(query: String): Flow<List<Product>> = db.productDao().searchProducts(query)
    fun getProduct(id: Int): Flow<Product?> = db.productDao().getProduct(id)
    suspend fun insertProduct(product: Product) = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: Product) = db.productDao().updateProduct(product)
    suspend fun deleteProduct(product: Product) = db.productDao().deleteProduct(product)

    // Reviews
    fun getReviews(productId: Int): Flow<List<Review>> = db.reviewDao().getReviewsForProduct(productId)
    suspend fun insertReview(review: Review) {
        db.reviewDao().insertReview(review)
    }
    suspend fun deleteReview(review: Review) = db.reviewDao().deleteReview(review)

    // Banners
    fun getAllBanners(): Flow<List<Banner>> = db.bannerDao().getAllBanners()
    suspend fun insertBanner(banner: Banner) = db.bannerDao().insertBanner(banner)
    suspend fun deleteBanner(banner: Banner) = db.bannerDao().deleteBanner(banner)

    // Settings
    fun getSettings(): Flow<AppSettings?> = db.settingsDao().getSettings()
    suspend fun saveSettings(settings: AppSettings) = db.settingsDao().insertOrUpdateSettings(settings)
}
