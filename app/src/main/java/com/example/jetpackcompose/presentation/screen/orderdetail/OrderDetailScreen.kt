package com.example.jetpackcompose.presentation.screen.orderdetail

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpackcompose.utils.Constants
import com.example.jetpackcompose.utils.UiState
import com.example.jetpackcompose.viewmodel.FoodDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: FoodDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val selectedVariant by viewModel.selectedVariant.collectAsState()
    val selectedToppings by viewModel.selectedToppings.collectAsState()
    
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.addToCartEvent.collect { success ->
            if (success) {
                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (state is UiState.Success) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Quantity Selector in bottom bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFF5F5F5), CircleShape)
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.decrementQuantity() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, null, tint = Color(0xFFFF9800))
                            }
                            Text(
                                text = quantity.toString(),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.widthIn(min = 30.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )
                            IconButton(
                                onClick = { viewModel.incrementQuantity() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color(0xFFFF9800))
                            }
                        }

                        Button(
                            onClick = { viewModel.addToCart() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            val basePrice = selectedVariant?.price ?: (state as UiState.Success).data.price
                            val totalPrice = (basePrice + selectedToppings.sumOf { it.price }) * quantity
                            
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
                                Text(
                                    "Thêm vào giỏ - ${totalPrice}đ",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())) {
            
            when (val s = state) {
                is UiState.Success -> {
                    val item = s.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Parallax Image Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .graphicsLayer {
                                    alpha = 1f - (scrollState.value.toFloat() / 1000f).coerceIn(0f, 1f)
                                    translationY = scrollState.value.toFloat() * 0.5f
                                }
                        ) {
                            AsyncImage(
                                model = Constants.getImageUrl(item.image, "menu"),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item.averageRating.toString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF9800),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "${item.price}đ",
                                color = Color(0xFFFF9800),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Mô tả",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.DarkGray,
                                lineHeight = 24.sp
                            )

                            // Variants Section
                            if (item.variants.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text("Kích cỡ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                item.variants.forEach { variant ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectVariant(variant) }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedVariant?.id == variant.id,
                                            onClick = { viewModel.selectVariant(variant) },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF9800))
                                        )
                                        Text(variant.label, modifier = Modifier.weight(1f), fontSize = 16.sp)
                                        Text("${variant.price}đ", fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            // Toppings Section
                            if (item.toppings.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text("Toppings thêm", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                item.toppings.forEach { topping ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.toggleTopping(topping) }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Checkbox(
                                            checked = selectedToppings.any { it.id == topping.id },
                                            onCheckedChange = { viewModel.toggleTopping(topping) },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF9800))
                                        )
                                        Text(topping.name, modifier = Modifier.weight(1f), fontSize = 16.sp)
                                        Text("+${topping.price}đ", color = Color.Gray)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            Text("Đánh giá từ khách hàng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            // Placeholder for reviews
                            repeat(3) {
                                ReviewItem()
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFFF9800))
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(s.message, color = Color.Red)
                    }
                }
                else -> {}
            }

            // Top Buttons (Back)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReviewItem() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.LightGray))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Người dùng ẩn danh", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row {
                repeat(5) { Icon(Icons.Default.Star, null, tint = Color(0xFFFF9800), modifier = Modifier.size(12.dp)) }
            }
            Text("Món ăn rất ngon, đóng gói cẩn thận. Giao hàng nhanh!", fontSize = 13.sp, color = Color.Gray)
        }
    }
}
