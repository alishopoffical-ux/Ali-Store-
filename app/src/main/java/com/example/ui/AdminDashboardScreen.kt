package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppSettings
import com.example.data.Banner
import com.example.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: StoreViewModel, onNavigateBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Products", "Banners", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> AdminProductsScreen(viewModel)
                1 -> AdminBannersScreen(viewModel)
                2 -> AdminSettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun AdminProductsScreen(viewModel: StoreViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
            items(products) { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("PKR: ${product.pricePkr} | USD: ${product.priceUsd}") },
                    trailingContent = {
                        IconButton(onClick = { viewModel.deleteProduct(product) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
        
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.padding(16.dp).align(androidx.compose.ui.Alignment.BottomEnd)
        ) {
            Text("+", modifier = Modifier.padding(horizontal = 16.dp))
        }

        if (showAddDialog) {
            // Very simplified add product dialog
            var name by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Dummy Product") },
                text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }) },
                confirmButton = {
                    Button(onClick = {
                        viewModel.addProduct(Product(
                            name = name.ifEmpty { "New Product" },
                            imageUrl = "https://picsum.photos/200",
                            shortDescription = "Short Desc",
                            fullDetails = "Full details here",
                            pricePkr = 1000.0,
                            priceUsd = 4.0,
                            buyLink = "https://example.com"
                        ))
                        showAddDialog = false
                    }) { Text("Add") }
                }
            )
        }
    }
}

@Composable
fun AdminBannersScreen(viewModel: StoreViewModel) {
    val banners by viewModel.banners.collectAsStateWithLifecycle()
    
    LazyColumn {
        item {
            Button(
                onClick = { viewModel.addBanner(Banner(imageUrl = "https://picsum.photos/400/200")) },
                modifier = Modifier.padding(16.dp)
            ) { Text("Add Dummy Banner") }
        }
        items(banners) { banner ->
            ListItem(
                headlineContent = { Text("Banner ID: ${banner.id}") },
                supportingContent = { Text(banner.imageUrl) },
                trailingContent = {
                    IconButton(onClick = { viewModel.deleteBanner(banner) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun AdminSettingsScreen(viewModel: StoreViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    if (settings == null) return

    var appName by remember { mutableStateOf(settings!!.appName) }
    var telegramLink by remember { mutableStateOf(settings!!.telegramLink) }
    var currencyEnabled by remember { mutableStateOf(settings!!.currencyEnabled) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("App Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = telegramLink, onValueChange = { telegramLink = it }, label = { Text("Telegram Link") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = currencyEnabled, onCheckedChange = { currencyEnabled = it })
            Text("Enable Currency Switch")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateSettings(settings!!.copy(
                appName = appName,
                telegramLink = telegramLink,
                currencyEnabled = currencyEnabled
            ))
        }) { Text("Save Settings") }
    }
}
