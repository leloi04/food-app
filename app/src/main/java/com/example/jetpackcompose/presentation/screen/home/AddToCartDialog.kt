package com.example.jetpackcompose.presentation.screen.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.model.Topping
import com.example.jetpackcompose.data.model.Variant
import com.example.jetpackcompose.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCartDialog(
    menuItem: MenuItem,
    initialQuantity: Int = 1,
    initialVariant: Variant? = null,
    initialToppings: List<Topping> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (Int, Variant?, List<Topping>) -> Unit
) {
    val isAvailable = menuItem.status == "available"
    
    var quantity by remember { mutableIntStateOf(initialQuantity) }
    var selectedVariant by remember { mutableStateOf(initialVariant ?: menuItem.variants.firstOrNull()) }
    val selectedToppings = remember { mutableStateListOf<Topping>().apply { addAll(initialToppings) } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.fillMaxHeight(0.9f)) {
            // Header with Image
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                AsyncImage(
                    model = Constants.getImageUrl(menuItem.image, "menu"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = if (!isAvailable) ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                        0.33f, 0.33f, 0.33f, 0f, 0f,
                        0.33f, 0.33f, 0.33f, 0f, 0f,
                        0.33f, 0.33f, 0.33f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    ))) else null
                )
                
                if (!isAvailable) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                        Surface(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp)) {
                            Text("HẾT HÀNG", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(20.dp)) {
                item {
                    Text(
                        menuItem.name, 
                        style = MaterialTheme.typography.headlineSmall, 
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isAvailable) Color.Black else Color.Gray
                    )
                    if (menuItem.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(menuItem.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF1F1F1))
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (menuItem.variants.isNotEmpty()) {
                    item {
                        Text("Chọn Size", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    items(menuItem.variants) { variant ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = isAvailable) { selectedVariant = variant }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = selectedVariant?.id == variant.id, 
                                onClick = { if (isAvailable) selectedVariant = variant },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF5722)),
                                enabled = isAvailable
                            )
                            Text(
                                variant.label, 
                                modifier = Modifier.weight(1f),
                                color = if (isAvailable) Color.Black else Color.Gray,
                                fontWeight = if (selectedVariant?.id == variant.id) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                "${variant.price}đ", 
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) Color.Black else Color.Gray
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                if (menuItem.toppings.isNotEmpty()) {
                    item {
                        Text("Toppings thêm", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    items(menuItem.toppings) { topping ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = isAvailable) { 
                                    if (selectedToppings.any { it.id == topping.id }) selectedToppings.removeIf { it.id == topping.id }
                                    else selectedToppings.add(topping)
                                }.padding(vertical = 10.dp)
                        ) {
                            Checkbox(
                                checked = selectedToppings.any { it.id == topping.id },
                                onCheckedChange = { 
                                    if (isAvailable) {
                                        if (it) selectedToppings.add(topping) else selectedToppings.removeIf { t -> t.id == topping.id } 
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF5722)),
                                enabled = isAvailable
                            )
                            Text(
                                topping.name, 
                                modifier = Modifier.weight(1f),
                                color = if (isAvailable) Color.Black else Color.Gray
                            )
                            Text("+${topping.price}đ", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Footer
            Surface(
                tonalElevation = 8.dp, 
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(if (isAvailable) Color(0xFFF5F5F5) else Color(0xFFEEEEEE), CircleShape)
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp),
                            enabled = isAvailable
                        ) {
                            Icon(Icons.Default.Remove, null, tint = if (isAvailable) Color(0xFFFF5722) else Color.Gray)
                        }
                        Text(
                            quantity.toString(), 
                            modifier = Modifier.widthIn(min = 30.dp), 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = if (isAvailable) Color.Black else Color.Gray
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(36.dp),
                            enabled = isAvailable
                        ) {
                            Icon(Icons.Default.Add, null, tint = if (isAvailable) Color(0xFFFF5722) else Color.Gray)
                        }
                    }
                    
                    val basePrice = selectedVariant?.price ?: menuItem.price
                    val toppingsPrice = selectedToppings.sumOf { it.price }
                    val totalPrice = (basePrice + toppingsPrice) * quantity
                    
                    Button(
                        onClick = { onConfirm(quantity, selectedVariant, selectedToppings.toList()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .weight(1f)
                            .padding(start = 16.dp),
                        contentPadding = PaddingValues(),
                        enabled = isAvailable
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (isAvailable) {
                                        Modifier.background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isAvailable) "Thêm vào giỏ - ${totalPrice}đ" else "Hết hàng", 
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) Color.White else Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
