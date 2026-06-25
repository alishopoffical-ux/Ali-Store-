package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.Banner
import com.example.data.Product
import com.example.data.Review
import com.example.data.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoreViewModel(private val repository: StoreRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val settings: StateFlow<AppSettings?> = repository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val banners: StateFlow<List<Banner>> = repository.getAllBanners()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = _searchQuery
        .combine(repository.getAllProducts()) { query, allProducts ->
            if (query.isBlank()) allProducts
            else allProducts.filter { it.name.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currencyIsUsd = MutableStateFlow(false)
    val currencyIsUsd: StateFlow<Boolean> = _currencyIsUsd

    fun toggleCurrency() {
        _currencyIsUsd.value = !_currencyIsUsd.value
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Admin Functions
    fun addProduct(product: Product) = viewModelScope.launch { repository.insertProduct(product) }
    fun updateProduct(product: Product) = viewModelScope.launch { repository.updateProduct(product) }
    fun deleteProduct(product: Product) = viewModelScope.launch { repository.deleteProduct(product) }
    
    fun addBanner(banner: Banner) = viewModelScope.launch { repository.insertBanner(banner) }
    fun deleteBanner(banner: Banner) = viewModelScope.launch { repository.deleteBanner(banner) }
    
    fun updateSettings(settings: AppSettings) = viewModelScope.launch { repository.saveSettings(settings) }

    // Reviews
    fun getReviews(productId: Int): StateFlow<List<Review>> = repository.getReviews(productId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReview(review: Review) = viewModelScope.launch { repository.insertReview(review) }
    fun deleteReview(review: Review) = viewModelScope.launch { repository.deleteReview(review) }
}

class StoreViewModelFactory(private val repository: StoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
