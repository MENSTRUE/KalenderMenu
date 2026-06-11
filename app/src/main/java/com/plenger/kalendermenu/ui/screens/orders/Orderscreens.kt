package com.plenger.kalendermenu.ui.screens.orders

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.data.OrderRepository
import com.plenger.kalendermenu.data.OrderSummary
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmOrderStatusChip
import com.plenger.kalendermenu.ui.components.KmSecondaryButton
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDarkGray
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.StatusGreen
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun AllOrdersScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { OrderRepository.getInstance(context) }

    val ordersList by repository.ordersFlow.collectAsState()

    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Minggu Ini", "Bulan Ini", "Selesai")
    var searchQuery by remember { mutableStateOf("") }

    var sortBy by remember { mutableStateOf("Terbaru") }
    var showSortMenu by remember { mutableStateOf(false) }
    val sortOptions = listOf("Terbaru", "Terlama", "HPP Tertinggi")

    val filtered = ordersList
        .filter { order ->
            (searchQuery.isBlank() || order.customerName.contains(searchQuery, ignoreCase = true)) &&
                    when (selectedFilter) {
                        "Semua"      -> true
                        "Selesai"    -> order.status.lowercase() == "selesai"
                        "Minggu Ini" -> true
                        "Bulan Ini"  -> true
                        else         -> true
                    }
        }
        .let { list ->
            when (sortBy) {
                "Terlama"      -> list.sortedBy { it.id }
                "HPP Tertinggi"-> list.sortedByDescending { it.hpp }
                else           -> list.sortedByDescending { it.id }
            }
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
                selectedRoute = Screen.AllOrders.route,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari nama pelanggan...", color = NeutralLightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = NeutralMidGray) },
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
                                label = { Text(f, fontSize = 14.sp, color = if (isSelected) NeutralWhite else NeutralDarkGray) },
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

                        Row(
                            modifier = Modifier.clickable { showSortMenu = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Urutkan: $sortBy", fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp) },
                                    onClick = {
                                        sortBy = option
                                        showSortMenu = false
                                    }
                                )
                            }
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                KmOrderStatusChip(order.status)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DateRange, null, tint = NeutralMidGray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text("${order.dateFormatted} · ${order.portions} Porsi", fontSize = 14.sp, color = NeutralMidGray)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Restaurant, null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text(order.menuName, fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = NeutralDivider)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("HPP: Rp ${localFormatRupiah(order.hpp)}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NeutralBlack)
                Text("Detail →", fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun OrderDetailScreen(navController: NavController, orderId: Long) {
    val context = LocalContext.current
    val repository = remember { OrderRepository.getInstance(context) }

    val ordersList by repository.ordersFlow.collectAsState()
    val order = ordersList.find { it.id == orderId }

    var showStatusDialog by remember { mutableStateOf(false) }

    if (order == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(title = "Detail Pesanan", onBackClick = { navController.popBackStack() })
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeutralWhite)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (order.status.lowercase()) {
                    "menunggu" -> {
                        Button(
                            onClick = { navController.navigate(Screen.SendToSupplier.createRoute(orderId)) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.ChatBubble, null, modifier = Modifier.size(20.dp), tint = NeutralWhite)
                            Spacer(Modifier.width(8.dp))
                            Text("Kirim ke Supplier", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = NeutralWhite)
                        }

                        Button(
                            onClick = { repository.updateOrderStatus(orderId, "konfirmasi") },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp), tint = NeutralWhite)
                            Spacer(Modifier.width(8.dp))
                            Text("Konfirmasi Pesanan", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = NeutralWhite)
                        }
                    }
                    "konfirmasi" -> {
                        KmSecondaryButton(
                            text = "Pasang Pengingat Kalender",
                            onClick = { navController.navigate(Screen.CalendarReminder.createRoute(orderId, order.menuName)) },
                            icon = Icons.Default.DateRange
                        )

                        Button(
                            onClick = { repository.updateOrderStatus(orderId, "selesai") },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp), tint = NeutralWhite)
                            Spacer(Modifier.width(8.dp))
                            Text("Tandai Pesanan Selesai", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = NeutralWhite)
                        }
                    }
                }

                Button(
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeutralLightGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Home, null, modifier = Modifier.size(18.dp), tint = NeutralBlack)
                    Spacer(Modifier.width(6.dp))
                    Text("Kembali ke Beranda", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = NeutralBlack)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                KmCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Status Pesanan", fontSize = 14.sp, color = NeutralMidGray)
                            Spacer(Modifier.height(2.dp))
                            Text("Ketuk untuk mengubah manual", fontSize = 11.sp, color = TealPrimary)
                        }
                        Surface(
                            modifier = Modifier.clickable { showStatusDialog = true },
                            color = androidx.compose.ui.graphics.Color.Transparent
                        ) {
                            KmOrderStatusChip(order.status)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    DetailRow("Pelanggan",     order.customerName)
                    DetailRow("Tanggal",       order.dateFormatted)
                    DetailRow("Jumlah Porsi",  "${order.portions} Porsi")
                    DetailRow("Menu",          order.menuName)
                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total HPP", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NeutralBlack)
                        Text("Rp ${localFormatRupiah(order.hpp)}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TealPrimary)
                    }
                }
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Ubah Status Pesanan", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val statuses = listOf("menunggu", "konfirmasi", "selesai", "batal")
                    statuses.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    repository.updateOrderStatus(orderId, status)
                                    showStatusDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            KmOrderStatusChip(status)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Tutup", color = NeutralMidGray)
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 15.sp, color = NeutralMidGray)
        Text(value, fontSize = 15.sp, color = NeutralBlack, fontWeight = FontWeight.Medium)
    }
}

private fun localFormatRupiah(amount: Long): String {
    return String.format("%,d", amount).replace(',', '.')
}