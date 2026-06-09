package com.example.jetpackcompose.presentation.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpackcompose.data.model.*
import com.example.jetpackcompose.utils.Constants
import com.example.jetpackcompose.utils.UiState
import com.example.jetpackcompose.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    val promotionsState by viewModel.promotionsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val menuItemsState by viewModel.filteredMenuItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedItemForDialog by remember { mutableStateOf<MenuItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        HomeHeader(user.name ?: "User", "123 Đường ABC, Quận 1")

        // Banner Slider
        PromotionSlider(promotionsState)

        // Features
        FeatureSection()

        // Categories
        CategorySection(categoriesState, selectedCategory) {
            viewModel.onCategorySelected(it)
        }

        // Menu Items
        MenuItemSection(
            state = menuItemsState, 
            onNavigate = onNavigateToDetail,
            onAddToCart = { selectedItemForDialog = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }

    selectedItemForDialog?.let { item ->
        AddToCartDialog(
            menuItem = item,
            onDismiss = { selectedItemForDialog = null },
            onConfirm = { quantity, variant, toppings ->
                viewModel.addToCart(item, quantity, variant, toppings)
                selectedItemForDialog = null
            }
        )
    }
}

@Composable
fun HomeHeader(name: String, address: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
                )
            )
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = "https://cdn-icons-png.flaticon.com/512/149/149071.png",
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Xin chào, $name", 
                    color = Color.White, 
                    fontSize = 15.sp, 
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = address,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = { /* Notification */ }) {
                Icon(Icons.Default.Notifications, null, tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromotionSlider(state: UiState<List<Promotion>>) {
    when (state) {
        is UiState.Success -> {
            val promotions = state.data
            val pagerState = rememberPagerState(pageCount = { promotions.size })

            LaunchedEffect(Unit) {
                while (true) {
                    delay(3000)
                    if (promotions.isNotEmpty()) {
                        val nextPager = (pagerState.currentPage + 1) % promotions.size
                        pagerState.animateScrollToPage(nextPager)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) { page ->
                    val promo = promotions[page]
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = Constants.getImageUrl(promo.image, "promotion"),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(promo.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(promo.description, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        else -> { /* Loading or Error shimmer */ }
    }
}

@Composable
fun FeatureSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FeatureCard("🚀", "Giao hàng nhanh", Modifier.weight(1f))
        FeatureCard("⭐", "Chất lượng", Modifier.weight(1f))
        FeatureCard("🏪", "Nhiều chi nhánh", Modifier.weight(1f))
    }
}

@Composable
fun FeatureCard(icon: String, title: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CategorySection(
    state: UiState<List<String>>,
    selectedCategory: String,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            "Danh mục",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        when (state) {
            is UiState.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { category ->
                        CategoryChip(category, selectedCategory == category) { onSelect(category) }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun CategoryChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFFF9800) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else Color.LightGray),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                name,
                color = if (isSelected) Color.White else Color.DarkGray,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun MenuItemSection(
    state: UiState<List<MenuItem>>,
    onNavigate: (String) -> Unit,
    onAddToCart: (MenuItem) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Món ăn dành cho bạn",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "MenuAnimation"
        ) { targetState ->
            when (targetState) {
                is UiState.Success -> {
                    val items = targetState.data
                    if (items.isEmpty()) {
                        EmptyMenuState()
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.animateContentSize()
                        ) {
                            for (i in items.indices step 2) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    MenuItemCard(
                                        item = items[i], 
                                        modifier = Modifier.weight(1f),
                                        onCardClick = { onNavigate(items[i].id) },
                                        onAddClick = { onAddToCart(items[i]) }
                                    )
                                    if (i + 1 < items.size) {
                                        MenuItemCard(
                                            item = items[i + 1], 
                                            modifier = Modifier.weight(1f),
                                            onCardClick = { onNavigate(items[i+1].id) },
                                            onAddClick = { onAddToCart(items[i+1]) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFFF9800))
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun EmptyMenuState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Không có món ăn nào trong danh mục này",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MenuItemCard(item: MenuItem, modifier: Modifier, onCardClick: () -> Unit, onAddClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = Constants.getImageUrl(item.image, "menu"),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.description,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${item.price}đ",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFFFF9800), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
