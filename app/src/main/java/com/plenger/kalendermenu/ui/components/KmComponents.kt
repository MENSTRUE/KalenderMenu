package com.plenger.kalendermenu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.plenger.kalendermenu.ui.theme.BottomNavSelected
import com.plenger.kalendermenu.ui.theme.BottomNavUnselected
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralDarkGray
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralSurface
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.OrangeAI
import com.plenger.kalendermenu.ui.theme.StatusGreen
import com.plenger.kalendermenu.ui.theme.StatusGreenLight
import com.plenger.kalendermenu.ui.theme.StatusOrange
import com.plenger.kalendermenu.ui.theme.StatusOrangeLight
import com.plenger.kalendermenu.ui.theme.StatusRed
import com.plenger.kalendermenu.ui.theme.StatusRedLight
import com.plenger.kalendermenu.ui.theme.TealContainer
import com.plenger.kalendermenu.ui.theme.TealLight
import com.plenger.kalendermenu.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KmTopBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null
) {
    var showDrawer by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (onBackClick == null) TealPrimary else NeutralBlack
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = NeutralBlack)
                }
            } else if (onMenuClick != null) {
                IconButton(onClick = { showDrawer = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = NeutralBlack)
                }
            }
        },
        actions = {
            if (onNotificationClick != null) {
                IconButton(onClick = { showNotifDialog = true }) {
                    BadgedBox(badge = { Badge() }) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifikasi",
                            tint = NeutralBlack
                        )
                    }
                }
            } else if (actionIcon != null && onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    Icon(actionIcon, contentDescription = null, tint = TealPrimary)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NeutralWhite
        )
    )

    if (showDrawer) {
        KmDrawerDialog(onDismiss = { showDrawer = false })
    }

    if (showNotifDialog) {
        KmNotificationDialog(onDismiss = { showNotifDialog = false })
    }
}

@Composable
private fun KmDrawerDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NeutralWhite)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SW", color = NeutralWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Ibu Sari Wahyuni", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack)
                        Text("Katering Sari Rasa", fontSize = 13.sp, color = NeutralMidGray)
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = NeutralDivider)
                Spacer(Modifier.height(12.dp))

                val items = listOf(
                    Pair(Icons.Outlined.Home, "Beranda"),
                    Pair(Icons.Outlined.DateRange, "Semua Pesanan"),
                    Pair(Icons.Outlined.Sell, "Harga Bahan"),
                    Pair(Icons.Outlined.Person, "Profil Saya")
                )
                items.forEach { (icon, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDismiss() }
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(14.dp))
                        Text(label, fontSize = 16.sp, color = NeutralBlack)
                    }
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = NeutralDivider)
                Spacer(Modifier.height(12.dp))
                Text(
                    "KalenderMenu v1.0 · GCW 2026",
                    fontSize = 12.sp,
                    color = NeutralLightGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun KmNotificationDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NeutralWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Notifikasi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                Spacer(Modifier.height(16.dp))

                val notifs = listOf(
                    Pair("Besok: Ibu Hartini 50 Porsi", "Nasi Gudeg + Ayam Bakar · 24 Jun"),
                    Pair("Bahan belum diperbarui", "Harga bahan Rendang Sapi perlu update"),
                    Pair("Rabu: Pak Ahmad 80 Porsi", "Nasi Kotak Ayam · 25 Jun")
                )
                notifs.forEach { (title, subtitle) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDismiss() }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralBlack)
                        Text(subtitle, fontSize = 13.sp, color = NeutralMidGray)
                    }
                    HorizontalDivider(color = NeutralDivider)
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup", color = NeutralWhite, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun KmBottomNavBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Beranda", Icons.Outlined.Home, Icons.Outlined.Home, "dashboard"),
        BottomNavItem("Pesanan", Icons.Outlined.DateRange, Icons.Outlined.DateRange, "all_orders"),
        BottomNavItem("Harga Bahan", Icons.Outlined.Sell, Icons.Outlined.Sell, "ingredient_price_list"),
        BottomNavItem("Akun", Icons.Outlined.Person, Icons.Outlined.Person, "profile")
    )

    NavigationBar(containerColor = NeutralWhite) {
        items.forEach { item ->
            val isSelected = selectedRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BottomNavSelected,
                    selectedTextColor = BottomNavSelected,
                    unselectedIconColor = BottomNavUnselected,
                    unselectedTextColor = BottomNavUnselected,
                    indicatorColor = TealContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String
)

@Composable
fun KmPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary,
            contentColor = NeutralWhite,
            disabledContainerColor = NeutralLightGray,
            disabledContentColor = NeutralWhite
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NeutralWhite, strokeWidth = 2.dp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                icon?.let {
                    Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun KmSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
fun KmWhatsAppButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = NeutralWhite)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NeutralWhite)
    }
}

@Composable
fun KmOrangeButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null, isLoading: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OrangeAI, contentColor = NeutralWhite)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NeutralWhite, strokeWidth = 2.dp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                icon?.let { Icon(it, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)) }
                Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun KmOrderStatusChip(status: String) {
    val (bg, textColor, label) = when (status.lowercase()) {
        "konfirmasi" -> Triple(StatusGreenLight, StatusGreen, "Konfirmasi")
        "menunggu"   -> Triple(StatusOrangeLight, StatusOrange, "Menunggu")
        "selesai"    -> Triple(TealContainer, TealPrimary, "Selesai")
        "batal"      -> Triple(StatusRedLight, StatusRed, "Batal")
        else         -> Triple(NeutralSurface, NeutralMidGray, status)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
    }
}

@Composable
fun KmAiBadge() {
    Surface(shape = RoundedCornerShape(20.dp), color = OrangeAI) {
        Text("+ AI", color = NeutralWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
fun KmIngredientChip(name: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = NeutralSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NeutralDivider)) {
        Text(name, color = NeutralDarkGray, fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun KmSectionHeader(title: String, actionText: String? = null, onActionClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NeutralBlack)
        if (actionText != null && onActionClick != null) {
            Text(actionText, fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onActionClick() })
        }
    }
}

@Composable
fun KmDateBadge(dayLabel: String, dayNumber: String) {
    Box(
        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(TealPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dayLabel, color = TealLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(dayNumber, color = NeutralWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KmPriceTrendBadge(delta: Long) {
    val isStable = delta == 0L
    val bg    = if (isStable) StatusGreenLight else StatusOrangeLight
    val color = if (isStable) StatusGreen else StatusOrange
    val text  = if (isStable) "Stabil" else "+${String.format("%,.0f", delta.toDouble()).replace(',', '.')}"

    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!isStable) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
            }
            Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun KmCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun KmStepIndicator(currentStep: Int, totalSteps: Int, labels: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        labels.forEachIndexed { index, label ->
            val step = index + 1
            val isActive = step == currentStep
            val isDone   = step < currentStep
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(when { isActive -> TealPrimary; isDone -> TealLight; else -> NeutralDivider }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step.toString(), color = if (isActive || isDone) NeutralWhite else NeutralMidGray,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(label, fontSize = 11.sp, color = if (isActive) TealPrimary else NeutralMidGray,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal)
            }
            if (index < labels.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp).padding(bottom = 20.dp),
                    color = if (isDone) TealLight else NeutralDivider, thickness = 2.dp
                )
            }
        }
    }
}