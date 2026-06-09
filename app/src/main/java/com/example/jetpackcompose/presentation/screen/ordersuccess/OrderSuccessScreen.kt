package com.example.jetpackcompose.presentation.screen.ordersuccess

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderSuccessScreen(onGoHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Đặt hàng thành công!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Đơn hàng của bạn đang được xử lý.", color = Color.Gray)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("VỀ TRANG CHỦ")
        }
    }
}
