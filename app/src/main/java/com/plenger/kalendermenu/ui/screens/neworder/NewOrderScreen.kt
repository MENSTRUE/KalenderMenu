package com.plenger.kalendermenu.ui.screens.neworder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmAiBadge
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmSecondaryButton
import com.plenger.kalendermenu.ui.components.KmStepIndicator
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.OrangeAI
import com.plenger.kalendermenu.ui.theme.OrangeAILight
import com.plenger.kalendermenu.ui.theme.TealContainer
import com.plenger.kalendermenu.ui.theme.TealPrimary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(navController: NavController) {

    var selectedDate    by remember { mutableStateOf(LocalDate.now()) }
    var portions        by remember { mutableStateOf(50) }   // ✅ Int, bukan String
    var portionsText    by remember { mutableStateOf("50") } // teks input sementara
    var showDatePicker  by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val scrollState     = rememberScrollState()

    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID"))
    val orderId   = 1L

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(title = "Pesanan Baru", onBackClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KmStepIndicator(
                currentStep = 1,
                totalSteps  = 3,
                labels      = listOf("Detail", "Menu", "Konfirmasi")
            )

            // ── Tanggal Pesanan ───────────────────────────────────
            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tanggal Pesanan", fontSize = 14.sp, color = NeutralMidGray, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = selectedDate.format(formatter),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                KmSecondaryButton(
                    text  = "Ubah Tanggal",
                    onClick = { showDatePicker = true },
                    icon  = Icons.Default.DateRange
                )
            }

            // ── Jumlah Porsi ──────────────────────────────────────
            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Jumlah Porsi", fontSize = 14.sp, color = NeutralMidGray, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(14.dp))

                // ✅ FIX: Row dengan tombol - dan + serta text input di tengah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Tombol kurangi
                    IconButton(
                        onClick = {
                            if (portions > 1) {
                                portions--
                                portionsText = portions.toString()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TealContainer)
                    ) {
                        Icon(Icons.Default.Remove, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    }

                    Spacer(Modifier.width(16.dp))

                    // ✅ FIX: OutlinedTextField yang benar-benar bisa diedit
                    OutlinedTextField(
                        value = portionsText,
                        onValueChange = { input ->
                            // Filter hanya angka, max 4 digit
                            val filtered = input.filter { it.isDigit() }.take(4)
                            portionsText = filtered
                            // Update Int portions
                            val num = filtered.toIntOrNull()
                            if (num != null && num > 0) portions = num
                        },
                        modifier = Modifier.width(120.dp),
                        textStyle = MaterialTheme.typography.displaySmall.copy(
                            textAlign = TextAlign.Center,
                            color = NeutralBlack,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = NeutralDivider,
                            focusedContainerColor = NeutralWhite,
                            unfocusedContainerColor = NeutralWhite
                        )
                    )

                    Spacer(Modifier.width(16.dp))

                    // Tombol tambah
                    IconButton(
                        onClick = {
                            if (portions < 9999) {
                                portions++
                                portionsText = portions.toString()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TealContainer)
                    ) {
                        Icon(Icons.Default.Add, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Ketuk angka untuk ubah, atau gunakan tombol ±",
                    fontSize = 13.sp,
                    color = NeutralMidGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // ── Pilih Jenis Pesanan ────────────────────────────────
            Text(
                "Pilih Jenis Pesanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeutralBlack
            )

            // ✅ FIX: bawa portionsText ke screen berikutnya via state
            RouteOptionCard(
                icon        = Icons.Default.Restaurant,
                iconBg      = TealContainer,
                iconTint    = TealPrimary,
                title       = "Pelanggan Minta Menu Sendiri",
                subtitle    = "Tentukan menu spesifik & ekstrak bahan otomatis",
                accentColor = TealPrimary,
                badge       = null,
                onClick     = {
                    if (portions > 0)
                        navController.navigate(Screen.SpecificMenu.createRoute(orderId))
                }
            )

            RouteOptionCard(
                icon        = Icons.Default.SmartToy,
                iconBg      = OrangeAILight,
                iconTint    = OrangeAI,
                title       = "Sesuaikan dengan Budget",
                subtitle    = "AI rekomendasikan menu terbaik sesuai anggaran",
                accentColor = OrangeAI,
                badge       = { KmAiBadge() },
                onClick     = {
                    if (portions > 0)
                        navController.navigate(Screen.AiRecommendation.createRoute(orderId))
                }
            )

            Spacer(Modifier.height(16.dp))
        }
    }

    // ── Date Picker Dialog ─────────────────────────────────────────
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = LocalDate.ofEpochDay(millis / 86400000)
                    }
                    showDatePicker = false
                }) {
                    Text("Pilih", color = TealPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = NeutralMidGray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = TealPrimary,
                    todayDateBorderColor      = TealPrimary
                )
            )
        }
    }
}

@Composable
private fun RouteOptionCard(
    icon: ImageVector,
    iconBg: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    accentColor: androidx.compose.ui.graphics.Color,
    badge: (@Composable () -> Unit)?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = NeutralBlack)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, fontSize = 13.sp, color = NeutralMidGray, lineHeight = 18.sp)
            }
            Spacer(Modifier.width(8.dp))
            if (badge != null) badge()
            else Icon(Icons.Default.ChevronRight, null, tint = accentColor)
        }
    }
}
