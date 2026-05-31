package com.plenger.kalendermenu.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmSectionHeader
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.OrangeAI
import com.plenger.kalendermenu.ui.theme.OrangeAILight
import com.plenger.kalendermenu.ui.theme.StatusGreen
import com.plenger.kalendermenu.ui.theme.StatusOrange
import com.plenger.kalendermenu.ui.theme.TealContainer
import com.plenger.kalendermenu.ui.theme.TealDark
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentRoute = Screen.Dashboard.route

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "KalenderMenu",
                onMenuClick = {},
                onNotificationClick = {}
            )
        },
        bottomBar = {
            KmBottomNavBar(
                selectedRoute = currentRoute,
                onItemSelected = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                UpcomingOrderCard(
                    state = uiState,
                    onDetailClick = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                QuickActionRow(
                    onAddOrderClick = { navController.navigate(Screen.NewOrder.route) },
                    onInvoiceHistoryClick = { navController.navigate(Screen.AllOrders.route) }
                )
            }

            item {
                Spacer(Modifier.height(20.dp))
                KmSectionHeader(
                    title = "Jadwal Minggu Ini",
                    actionText = "Lihat Semua ",
                    onActionClick = { navController.navigate(Screen.AllOrders.route) }
                )
                Spacer(Modifier.height(8.dp))
            }

            items(uiState.weeklyOrders) { order ->
                WeeklyScheduleItem(
                    order = order,
                    onClick = { navController.navigate(Screen.OrderDetail.createRoute(order.id)) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun UpcomingOrderCard(
    state: DashboardUiState,
    onDetailClick: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(TealPrimary)
            .clickable { state.upcomingOrder?.let { onDetailClick(it.id) } }
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = TealContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Pesanan Terdekat",
                    color = TealContainer,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            if (state.upcomingOrder != null) {
                Text(
                    text = state.upcomingOrder.dateFormatted,
                    color = NeutralWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    lineHeight = 32.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${state.upcomingOrder.customerName} · ${state.upcomingOrder.portions} Porsi",
                    color = NeutralWhite.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
                Text(
                    text = state.upcomingOrder.menuName,
                    color = NeutralWhite.copy(alpha = 0.85f),
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TealDark
                    ) {
                        Text(
                            text = "HPP: Rp ${formatRupiah(state.upcomingOrder.hpp)}",
                            color = NeutralWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                    Text(
                        text = "Lihat Detail ",
                        color = TealContainer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    text = "Tidak ada pesanan mendatang",
                    color = NeutralWhite.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Tambah pesanan baru →",
                    color = TealContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun QuickActionRow(
    onAddOrderClick: () -> Unit,
    onInvoiceHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Default.Add,
            iconBg = TealContainer,
            iconTint = TealPrimary,
            title = "Tambah Pesanan",
            subtitle = "Buat jadwal baru",
            onClick = onAddOrderClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            icon = Icons.Outlined.Receipt,
            iconBg = OrangeAILight,
            iconTint = OrangeAI,
            title = "Riwayat Invoice",
            subtitle = "Lihat semua nota",
            onClick = onInvoiceHistoryClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    iconBg: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NeutralBlack)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 13.sp, color = NeutralMidGray)
        }
    }
}

@Composable
private fun WeeklyScheduleItem(
    order: OrderSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Memanggil CustomDateBadge yang dibuat khusus di file ini
            CustomDateBadge(dayLabel = order.dayLabel, dayNumber = order.dayNumber)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NeutralBlack)
                Spacer(Modifier.height(2.dp))
                Text(order.menuName, fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                Text("${order.portions} Porsi", fontSize = 13.sp, color = NeutralMidGray)
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        when (order.status) {
                            "konfirmasi" -> StatusGreen
                            "menunggu" -> StatusOrange
                            else -> NeutralLightGray
                        }
                    )
            )
        }
    }
}

// Ini adalah komponen custom pengganti KmDateBadge khusus untuk mengatasi masalah UI kamu
@Composable
private fun CustomDateBadge(dayLabel: String, dayNumber: String) {
    Column(
        modifier = Modifier
            .width(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(TealPrimary) // Menggunakan warna hijau tosca yang sama dengan desain
            .padding(vertical = 10.dp), // Ini yang bikin kotak melebar simetris ke atas dan bawah
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayLabel,
            color = NeutralWhite, // Teks hari (SEL, RAB) diubah paksa jadi putih
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = dayNumber,
            color = NeutralWhite, // Teks angka diubah jadi putih
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatRupiah(amount: Long): String {
    return String.format("%,d", amount).replace(',', '.')
}