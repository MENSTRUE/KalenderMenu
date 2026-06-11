package com.plenger.kalendermenu.ui.screens.profile

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.plenger.kalendermenu.data.OrderRepository
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
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
    val context = LocalContext.current
    val repository = remember { OrderRepository.getInstance(context) }

    val ordersList by repository.ordersFlow.collectAsState()

    val totalPesanan = ordersList.size
    val totalLunas = ordersList.count { it.status.lowercase() == "konfirmasi" || it.status.lowercase() == "selesai" }
    val totalPending = ordersList.count { it.status.lowercase() == "menunggu" }

    var notifEnabled by remember { mutableStateOf(true) }
    var calendarEnabled by remember { mutableStateOf(true) }

    var showEditProfilDialog by remember { mutableStateOf(false) }
    var showSupplierDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var profileName by remember { mutableStateOf("Ibu Sari Wahyuni") }
    var profileBusiness by remember { mutableStateOf("Katering Sari Rasa") }
    var profileCity by remember { mutableStateOf("Ponorogo, Jawa Timur") }
    val profileInitial = profileName.split(" ")
        .take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Profil Saya",
                onMenuClick = {},
                actionIcon = Icons.Default.Edit,
                onActionClick = { showEditProfilDialog = true }
            )
        },
        bottomBar = {
            KmBottomNavBar(
                selectedRoute = Screen.Profile.route,
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
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(Modifier.height(2.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(64.dp).clip(CircleShape).background(TealPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(profileInitial, color = NeutralWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(profileName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NeutralBlack)
                                Text(profileBusiness, fontSize = 15.sp, color = NeutralMidGray)
                                Spacer(Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.LocationOn, null, tint = NeutralMidGray, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text(profileCity, fontSize = 13.sp, color = NeutralMidGray)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatChip("$totalPesanan Pesanan", TealContainer, TealPrimary, Modifier.weight(1f))
                            StatChip("$totalLunas Lunas", StatusGreenLight, StatusGreen, Modifier.weight(1f))
                            StatChip("$totalPending Pending", OrangeAILight, OrangeAI, Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                ProfileSection(title = "USAHA SAYA") {
                    ProfileMenuItem(Icons.Outlined.Person, "Edit Profil Usaha", onClick = { showEditProfilDialog = true })
                    ProfileMenuItem(Icons.Outlined.Group, "Daftar Supplier", onClick = { showSupplierDialog = true })
                    ProfileMenuItem(Icons.Outlined.Receipt, "Riwayat Invoice", onClick = {
                        navController.navigate(Screen.AllOrders.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                        }
                    })
                    ProfileMenuItem(Icons.Outlined.Sell, "Kelola Harga Bahan", onClick = {
                        navController.navigate(Screen.IngredientPriceList.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                        }
                    })
                }
            }

            item {
                ProfileSection(title = "PENGATURAN") {
                    ProfileToggleItem(Icons.Outlined.Notifications, "Notifikasi & Pengingat",
                        notifEnabled, { notifEnabled = it })
                    ProfileToggleItem(Icons.Outlined.DateRange, "Sinkronisasi Google Calendar",
                        calendarEnabled, { calendarEnabled = it })
                    ProfileMenuItem(Icons.AutoMirrored.Outlined.Help, "Bantuan & FAQ", onClick = { showHelpDialog = true })
                }
            }

            item {
                ProfileSection(title = "LAINNYA") {
                    ProfileMenuItem(Icons.Outlined.Shield, "Kebijakan Privasi", onClick = { showPrivacyDialog = true })
                    ProfileMenuItem(Icons.Outlined.Info, "Tentang Aplikasi", onClick = { showAboutDialog = true })
                    ProfileMenuItem(Icons.AutoMirrored.Filled.Logout, "Keluar", onClick = { showLogoutDialog = true }, isDestructive = true)
                }
            }

            item {
                Text(
                    "KalenderMenu v1.0",
                    fontSize = 11.sp, color = NeutralLightGray,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    if (showEditProfilDialog) {
        var editName by remember { mutableStateOf(profileName) }
        var editBusiness by remember { mutableStateOf(profileBusiness) }
        var editCity by remember { mutableStateOf(profileCity) }
        Dialog(onDismissRequest = { showEditProfilDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = NeutralWhite)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Edit Profil Usaha", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                    OutlinedTextField(editName, { editName = it }, label = { Text("Nama Pemilik") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(editBusiness, { editBusiness = it }, label = { Text("Nama Katering") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(editCity, { editCity = it }, label = { Text("Kota") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = { showEditProfilDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal", color = NeutralMidGray)
                        }
                        Button(
                            onClick = {
                                profileName = editName
                                profileBusiness = editBusiness
                                profileCity = editCity
                                showEditProfilDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Simpan", color = NeutralWhite) }
                    }
                }
            }
        }
    }

    if (showSupplierDialog) {
        val suppliers = listOf(
            Triple("Pak Muflihin", "Pasar Tradisional", "+62 856-4880-4502"),
            Triple("Pak Wowo",  "Pasar Modern",       "+62 881-0368-50480"),
            Triple("Pak Cesar","Distributor",        "+62 851-5646-1831")
        )
        Dialog(onDismissRequest = { showSupplierDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = NeutralWhite)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Daftar Supplier", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                    Spacer(Modifier.height(14.dp))
                    suppliers.forEach { (name, lokasi, telp) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(TealContainer), contentAlignment = Alignment.Center) {
                                Text(name.first().toString(), color = TealPrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NeutralBlack)
                                Text("$lokasi · $telp", fontSize = 13.sp, color = NeutralMidGray)
                            }
                        }
                        HorizontalDivider(color = NeutralDivider)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { showSupplierDialog = false }, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), shape = RoundedCornerShape(12.dp)) {
                        Text("Tutup", color = NeutralWhite)
                    }
                }
            }
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Bantuan & FAQ", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val faqs = listOf(
                        "Bagaimana cara tambah pesanan?" to "Klik tombol '+' di Beranda atau tab Pesanan.",
                        "Apa itu HPP?" to "Harga Pokok Produksi — estimasi biaya bahan baku per pesanan.",
                        "Bagaimana sync Google Calendar?" to "Aktifkan di Pengaturan, lalu klik 'Pasang Pengingat' di detail pesanan.",
                        "Cara kirim ke supplier?" to "Buka detail pesanan → Kirim ke Supplier → Buka WhatsApp & Kirim."
                    )
                    faqs.forEach { (q, a) ->
                        Text(q, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = NeutralBlack)
                        Text(a, fontSize = 13.sp, color = NeutralMidGray)
                        Spacer(Modifier.height(4.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Mengerti", color = NeutralWhite)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Kebijakan Privasi", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "KalenderMenu menghormati privasi Anda. Data pesanan dan kontak supplier " +
                            "tersimpan hanya di perangkat Anda. Kami tidak menjual atau membagikan " +
                            "data pribadi kepada pihak ketiga. Sinkronisasi Google Calendar memerlukan " +
                            "izin akses kalender yang dapat dicabut kapan saja di pengaturan perangkat.",
                    fontSize = 14.sp, color = NeutralMidGray, lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Mengerti", color = NeutralWhite)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Tentang Aplikasi", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("KalenderMenu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimary)
                    Text("Versi 1.0.0", fontSize = 13.sp, color = NeutralMidGray)
                    Spacer(Modifier.height(4.dp))
                    Text("Aplikasi manajemen katering pintar dengan prediksi HPP berbasis AI, integrasi WhatsApp, dan sinkronisasi Google Calendar.", fontSize = 14.sp, color = NeutralMidGray, lineHeight = 22.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Tim: Wafa, Zafran, Nero, Misbahul", fontSize = 13.sp, color = NeutralLightGray)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Tutup", color = NeutralWhite)
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari Aplikasi?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin keluar?", fontSize = 15.sp, color = NeutralMidGray) },
            confirmButton = {
                Button(
                    onClick = {
                        context.getSharedPreferences("kalendermenu_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_logged_in", false)
                            .apply()
                        showLogoutDialog = false
                        (context as? android.app.Activity)?.finishAffinity()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) { Text("Keluar", color = NeutralWhite) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = NeutralMidGray)
                }
            }
        )
    }
}

@Composable
private fun StatChip(label: String, bg: androidx.compose.ui.graphics.Color, textColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(20.dp), color = bg, modifier = modifier) {
        Text(label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, fontSize = 11.sp, color = NeutralLightGray, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 2.dp))
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NeutralWhite),
            elevation = CardDefaults.cardElevation(1.dp)) {
            content()
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    val textColor = if (isDestructive) StatusRed else NeutralBlack
    val iconColor = if (isDestructive) StatusRed else NeutralMidGray
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 16.dp),
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NeutralMidGray, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 17.sp, color = NeutralBlack, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeutralWhite, checkedTrackColor = TealPrimary,
                uncheckedThumbColor = NeutralWhite, uncheckedTrackColor = NeutralLightGray
            ))
    }
    HorizontalDivider(color = NeutralDivider.copy(alpha = 0.6f), modifier = Modifier.padding(start = 52.dp))
}