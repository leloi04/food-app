package com.example.jetpackcompose.presentation.screen.cart

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpackcompose.data.model.CartItem
import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.model.Topping
import com.example.jetpackcompose.data.model.Variant
import com.example.jetpackcompose.presentation.screen.home.AddToCartDialog
import com.example.jetpackcompose.utils.Constants
import com.example.jetpackcompose.utils.UiState
import com.example.jetpackcompose.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val items by viewModel.cartItems.collectAsState()
    val totalItemPrice by viewModel.totalItemPrice.collectAsState()
    val editingDetails by viewModel.editingItemDetails.collectAsState()
    
    var editingItem by remember { mutableStateOf<CartItem?>(null) }

    LaunchedEffect(editingItem) {
        if (editingItem != null) {
            viewModel.fetchItemDetails(editingItem!!.id)
        } else {
            viewModel.clearEditingState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Xóa tất cả", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    tonalElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tổng tiền món", fontWeight = FontWeight.Medium)
                            Text(
                                "${totalItemPrice}đ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFFFF9800)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Thanh toán", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyCartView(onContinueShopping = onBack)
                }
            } else {
                items(items) { item ->
                    CartItemCard(
                        item = item,
                        onUpdateQuantity = { delta -> viewModel.updateQuantity(item, delta) },
                        onEdit = { editingItem = item },
                        onRemove = { viewModel.removeItem(item) }
                    )
                }
            }
        }
    }

    if (editingItem != null && editingDetails is UiState.Success) {
        val item = editingItem!!
        val details = (editingDetails as UiState.Success).data
        AddToCartDialog(
            menuItem = details,
            initialQuantity = item.quantity,
            initialVariant = item.variant,
            initialToppings = item.toppings,
            onDismiss = { editingItem = null },
            onConfirm = { quantity, variant, toppings ->
                viewModel.updateItem(
                    oldItem = item,
                    newItem = item.copy(
                        quantity = quantity,
                        variant = variant,
                        toppings = toppings,
                        price = variant?.price ?: details.price
                    )
                )
                editingItem = null
            }
        )
    }
}

@Composable
fun EmptyCartView(onContinueShopping: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Giỏ hàng của bạn đang trống", color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onContinueShopping, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) {
                Text("Tiếp tục mua sắm")
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem, 
    onUpdateQuantity: (Int) -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = Constants.getImageUrl(item.image, "menu"),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                if (item.variant != null) {
                    Text("Size: ${item.variant.label}", fontSize = 12.sp, color = Color.Gray)
                }
                if (item.toppings.isNotEmpty()) {
                    Text("Toppings: ${item.toppings.joinToString { it.name }}", fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                }
                Text("${item.getTotalPrice()}đ", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onEdit, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(30.dp)) {
                        Text("Sửa", color = Color(0xFFFF9800), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(Color(0xFFF5F5F5), CircleShape).padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { onUpdateQuantity(-1) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Remove, null, modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                        }
                        Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        IconButton(onClick = { onUpdateQuantity(1) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
