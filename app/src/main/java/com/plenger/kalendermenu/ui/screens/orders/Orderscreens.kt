package com.plenger.kalendermenu.ui.screens.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmOrderStatusChip
import com.plenger.kalendermenu.ui.components.KmPrimaryButton
import com.plenger.kalendermenu.ui.components.KmSecondaryButton
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.screens.dashboard.OrderSummary
import com.plenger.kalendermenu.ui.screens.dashboard.formatRupiah
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDarkGray
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.TealPrimary

private val mockOrders = listOf(
    OrderSummary(1L, "Ibu Hartini", "Nasi Gudeg + Ayam Bakar", 50, 875000, "Sel, 24 Juni 2026", "SEL", "24", "konfirmasi"),
    OrderSummary(2L, "Pak Ahmad", "Nasi Kotak Ayam", 80, 1200000, "Rab, 25 Juni 2026", "RAB", "25", "menunggu"),
    OrderSummary(3L, "Bu Siti", "Rendang Sapi", 30, 620000, "Jum, 27 Juni 2026", "JUM", "27", "konfirmasi"),
)

@Composable
fun AllOrdersScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Minggu Ini", "Bulan Ini", "Selesai")
    var searchQuery by remember { mutableStateOf("") }

    val filtered = mockOrders.filter { order ->
        (searchQuery.isBlank() || order.customerName.contains(searchQuery, ignoreCase = true)) &&
                (selectedFilter == "Semua" ||
                        (selectedFilter == "Selesai" && order.status == "selesai") ||
                        (selectedFilter == "Minggu Ini"))
    }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Semua Pesanan",
                onMenuClick = {},
                actionIcon = Icons.Default.Add,
                onActionClick = { navController.navigate(Screen.NewOrder.route) }
            )
        },
        bottomBar = {
            KmBottomNavBar(
                selectedRoute = "all_orders",
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari nama pelanggan...", color = NeutralLightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeutralMidGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = NeutralDivider,
                            unfocusedContainerColor = NeutralWhite,
                            focusedContainerColor = NeutralWhite
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { f ->
                            val isSelected = selectedFilter == f
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = f },
                                label = { Text(f, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = NeutralWhite,
                                    containerColor = NeutralWhite,
                                    labelColor = NeutralDarkGray
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = NeutralDivider,
                                    selectedBorderColor = TealPrimary,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${filtered.size} pesanan", fontSize = 14.sp, color = NeutralMidGray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Urutkan: Terbaru", fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            items(filtered) { order ->
                OrderCard(order = order, onClick = {
                    navController.navigate(Screen.OrderDetail.createRoute(order.id))
                })
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                KmOrderStatusChip(order.status)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = NeutralMidGray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text("${order.dateFormatted} · ${order.portions} Porsi", fontSize = 14.sp, color = NeutralMidGray)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text(order.menuName, fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = NeutralDivider)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("HPP: Rp ${formatRupiah(order.hpp)}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NeutralBlack)
                Text("Detail →", fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun OrderDetailScreen(navController: NavController, orderId: Long) {
    val order = mockOrders.find { it.id == orderId } ?: return

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(title = "Detail Pesanan", onBackClick = { navController.popBackStack() })
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KmPrimaryButton(
                    text = "Kirim ke Supplier",
                    onClick = { navController.navigate(Screen.SendToSupplier.createRoute(orderId)) },
                    icon = Icons.Default.ChatBubble
                )
                KmSecondaryButton(
                    text = "Pasang Pengingat Kalender",
                    onClick = { navController.navigate(Screen.CalendarReminder.createRoute(orderId, order.menuName)) },
                    icon = Icons.Default.DateRange
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                KmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status Pesanan", fontSize = 14.sp, color = NeutralMidGray)
                        KmOrderStatusChip(order.status)
                    }
                    Spacer(Modifier.height(16.dp))
                    DetailRow(label = "Pelanggan", value = order.customerName)
                    DetailRow(label = "Tanggal", value = order.dateFormatted)
                    DetailRow(label = "Jumlah Porsi", value = "${order.portions} Porsi")
                    DetailRow(label = "Menu", value = order.menuName)
                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total HPP", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NeutralBlack)
                        Text("Rp ${formatRupiah(order.hpp)}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TealPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 15.sp, color = NeutralMidGray)
        Text(value, fontSize = 15.sp, color = NeutralBlack, fontWeight = FontWeight.Medium)
    }
}