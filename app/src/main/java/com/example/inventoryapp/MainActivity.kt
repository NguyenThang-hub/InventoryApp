package com.example.inventoryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inventoryapp.ui.theme.InventoryAppTheme
import java.util.UUID

// --- DATA MODEL ---
data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventoryAppTheme {
                InventoryApp()
            }
        }
    }
}

@Composable
fun InventoryApp() {
    // Top-level state holding our inventory list
    val inventoryList = remember { mutableStateListOf<InventoryItem>() }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                inventoryList = inventoryList,
                onNavigateToAddItem = { navController.navigate("add") },
                onDeleteItem = { item -> inventoryList.remove(item) }
            )
        }
        composable("add") {
            AddScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddItem = { name, quantity ->
                    inventoryList.add(InventoryItem(name = name, quantity = quantity))
                    navController.popBackStack()
                }
            )
        }
    }
}

// --- HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    inventoryList: List<InventoryItem>,
    onNavigateToAddItem: () -> Unit,
    onDeleteItem: (InventoryItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý kho hàng") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddItem) {
                Icon(Icons.Filled.Add, contentDescription = "Thêm sản phẩm")
            }
        }
    ) { innerPadding ->
        if (inventoryList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có sản phẩm nào. Hãy thêm mới!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(inventoryList, key = { it.id }) { item ->
                    InventoryItemCard(item = item, onDelete = { onDeleteItem(item) })
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: InventoryItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name, 
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Số lượng: ${item.quantity}", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Xóa sản phẩm",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// --- ADD ITEM SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onNavigateBack: () -> Unit,
    onAddItem: (name: String, quantity: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm Sản Phẩm") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    errorMessage = null
                },
                label = { Text("Tên sản phẩm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = quantityText,
                onValueChange = { 
                    quantityText = it
                    errorMessage = null
                },
                label = { Text("Số lượng") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hủy")
                }
                
                Button(
                    onClick = {
                        val qty = quantityText.toIntOrNull()
                        if (name.isBlank()) {
                            errorMessage = "Tên không được để trống"
                        } else if (qty == null || qty < 0) {
                            errorMessage = "Số lượng không hợp lệ"
                        } else {
                            errorMessage = null
                            onAddItem(name, qty)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Thêm mới")
                }
            }
        }
    }
}
