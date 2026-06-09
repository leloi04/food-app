package com.example.jetpackcompose.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    var quantity by remember { mutableIntStateOf(initialQuantity) }
    var selectedVariant by remember { mutableStateOf(initialVariant ?: menuItem.variants.firstOrNull()) }
    val selectedToppings = remember { mutableStateListOf<Topping>().apply { addAll(initialToppings) } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight(0.9f)) {
            // Header with Image
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                AsyncImage(
                    model = Constants.getImageUrl(menuItem.image, "menu"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
                item {
                    Text(menuItem.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    if (menuItem.description.isNotEmpty()) {
                        Text(menuItem.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (menuItem.variants.isNotEmpty()) {
                    item {
                        Text("Chọn Size", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(menuItem.variants) { variant ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { selectedVariant = variant }.padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedVariant?.id == variant.id, 
                                onClick = { selectedVariant = variant },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF9800))
                            )
                            Text(variant.label, modifier = Modifier.weight(1f))
                            Text("${variant.price}đ", fontWeight = FontWeight.Bold)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                if (menuItem.toppings.isNotEmpty()) {
                    item {
                        Text("Toppings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(menuItem.toppings) { topping ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { 
                                if (selectedToppings.any { it.id == topping.id }) selectedToppings.removeIf { it.id == topping.id }
                                else selectedToppings.add(topping)
                            }.padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = selectedToppings.any { it.id == topping.id },
                                onCheckedChange = { 
                                    if (it) selectedToppings.add(topping) else selectedToppings.removeIf { t -> t.id == topping.id } 
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF9800))
                            )
                            Text(topping.name, modifier = Modifier.weight(1f))
                            Text("+${topping.price}đ", color = Color.Gray)
                        }
                    }
                }
            }

            // Footer
            Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.border(1.dp, Color.LightGray, CircleShape)) {
                            Icon(Icons.Default.Remove, null, tint = Color.DarkGray)
                        }
                        Text(quantity.toString(), modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        IconButton(onClick = { quantity++ }, modifier = Modifier.background(Color(0xFFFF9800), CircleShape)) {
                            Icon(Icons.Default.Add, null, tint = Color.White)
                        }
                    }
                    
                    val basePrice = selectedVariant?.price ?: menuItem.price
                    val toppingsPrice = selectedToppings.sumOf { it.price }
                    val totalPrice = (basePrice + toppingsPrice) * quantity
                    
                    Button(
                        onClick = { onConfirm(quantity, selectedVariant, selectedToppings.toList()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        // Check if we are in "Edit" mode by checking if initialQuantity > 0
                        // Since new items always default to initialQuantity = 1 in the signature, 
                        // we might need a better flag or just keep it simple.
                        // Actually, I set initialQuantity = item.quantity in CartScreen.
                        Text("Xác nhận - ${totalPrice}đ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
