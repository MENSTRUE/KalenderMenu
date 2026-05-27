package com.plenger.kalendermenu.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.OrangeAI
import com.plenger.kalendermenu.ui.theme.OrangeAILight
import com.plenger.kalendermenu.ui.theme.StatusGreen
import com.plenger.kalendermenu.ui.theme.StatusGreenLight
import com.plenger.kalendermenu.ui.theme.StatusRed
import com.plenger.kalendermenu.ui.theme.TealContainer
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun ProfileScreen(navController: NavController) {
    var notifEnabled by remember { mutableStateOf(true) }
    var calendarEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Profil Saya",
                onMenuClick = {},
                actionIcon = Icons.Default.Edit,
                onActionClick = {}
            )
        },
        bottomBar = {
            KmBottomNavBar(
                selectedRoute = "profile",
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
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(Modifier.height(2.dp)) }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(TealPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("SW", color = NeutralWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Ibu Sari Wahyuni", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NeutralBlack)
                                Text("Katering Sari Rasa", fontSize = 15.sp, color = NeutralMidGray)
                                Spacer(Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = NeutralMidGray, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text("Ponorogo, Jawa Timur", fontSize = 13.sp, color = NeutralMidGray)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatChip(label = "24 Pesanan", bg = TealContainer, textColor = TealPrimary, modifier = Modifier.weight(1f))
                            StatChip(label = "18 Lunas", bg = StatusGreenLight, textColor = StatusGreen, modifier = Modifier.weight(1f))
                            StatChip(label = "6 Pending", bg = OrangeAILight, textColor = OrangeAI, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                ProfileSection(title = "USAHA SAYA") {
                    ProfileMenuItem(icon = Icons.Outlined.Person, label = "Edit Profil Usaha", onClick = {})
                    ProfileMenuItem(icon = Icons.Outlined.Group, label = "Daftar Supplier", onClick = {})
                    ProfileMenuItem(icon = Icons.Outlined.Receipt, label = "Riwayat Invoice", onClick = {})
                    ProfileMenuItem(icon = Icons.Outlined.Sell, label = "Kelola Harga Bahan", onClick = {})
                }
            }

            item {
                ProfileSection(title = "PENGATURAN") {
                    ProfileToggleItem(
                        icon = Icons.Outlined.Notifications,
                        label = "Notifikasi & Pengingat",
                        checked = notifEnabled,
                        onCheckedChange = { notifEnabled = it }
                    )
                    ProfileToggleItem(
                        icon = Icons.Outlined.DateRange,
                        label = "Sinkronisasi Google Calendar",
                        checked = calendarEnabled,
                        onCheckedChange = { calendarEnabled = it }
                    )
                    ProfileMenuItem(icon = Icons.Outlined.Help, label = "Bantuan & FAQ", onClick = {})
                }
            }

            item {
                ProfileSection(title = "LAINNYA") {
                    ProfileMenuItem(icon = Icons.Outlined.Shield, label = "Kebijakan Privasi", onClick = {})
                    ProfileMenuItem(icon = Icons.Outlined.Info, label = "Tentang Aplikasi", onClick = {})
                    ProfileMenuItem(icon = Icons.Default.Logout, label = "Keluar", onClick = {}, isDestructive = true)
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, bg: androidx.compose.ui.graphics.Color, textColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(20.dp), color = bg, modifier = modifier) {
        Text(
            label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, fontSize = 11.sp, color = NeutralLightGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 2.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NeutralWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    val textColor = if (isDestructive) StatusRed else NeutralBlack
    val iconColor = if (isDestructive) StatusRed else NeutralMidGray
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 17.sp, color = textColor, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NeutralLightGray, modifier = Modifier.size(20.dp))
    }
    HorizontalDivider(color = NeutralDivider.copy(alpha = 0.6f), modifier = Modifier.padding(start = 52.dp))
}

@Composable
private fun ProfileToggleItem(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NeutralMidGray, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 17.sp, color = NeutralBlack, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeutralWhite,
                checkedTrackColor = TealPrimary,
                uncheckedThumbColor = NeutralWhite,
                uncheckedTrackColor = NeutralLightGray
            )
        )
    }
    HorizontalDivider(color = NeutralDivider.copy(alpha = 0.6f), modifier = Modifier.padding(start = 52.dp))
}