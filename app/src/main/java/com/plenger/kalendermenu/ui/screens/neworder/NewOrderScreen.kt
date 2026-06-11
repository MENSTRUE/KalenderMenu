package com.plenger.kalendermenu.ui.screens.neworder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.*
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(navController: NavController, viewModel: NewOrderViewModel = hiltViewModel()) {
    var customerName by remember { mutableStateOf(viewModel.customerName) }
    var portions by remember { mutableStateOf(viewModel.portions) }
    var portionsText by remember { mutableStateOf(viewModel.portions.toString()) }
    var selectedDate by remember { mutableStateOf(LocalDate.parse(viewModel.selectedDate)) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID"))
    val orderId = System.currentTimeMillis()

    fun navigateAndSave(isAi: Boolean) {
        viewModel.customerName = customerName
        viewModel.portions = portions
        viewModel.selectedDate = selectedDate.toString()

        val nameParam = customerName.trim().ifBlank { "Pelanggan" }
        val route = if (isAi) Screen.AiRecommendation.createRoute(orderId, portions, selectedDate.toString(), nameParam)
        else Screen.SpecificMenu.createRoute(orderId, portions, selectedDate.toString(), nameParam)
        navController.navigate(route)
    }

    Scaffold(containerColor = NeutralBackground, topBar = { KmTopBar(title = "Pesanan Baru", onBackClick = { navController.popBackStack() }) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Nama Pelanggan", fontSize = 14.sp, color = NeutralMidGray, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = customerName, onValueChange = { customerName = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Masukkan nama pelanggan...", color = NeutralLightGray) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = NeutralDivider, focusedContainerColor = NeutralWhite, unfocusedContainerColor = NeutralWhite)
                )
            }

            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tanggal Pesanan", fontSize = 14.sp, color = NeutralMidGray, fontWeight = FontWeight.Medium)
                }
                Text(text = selectedDate.format(formatter), style = MaterialTheme.typography.headlineMedium, color = TealPrimary, fontWeight = FontWeight.Bold)
                KmSecondaryButton(text = "Ubah Tanggal", onClick = { showDatePicker = true }, icon = Icons.Default.DateRange)
            }

            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Jumlah Porsi", fontSize = 14.sp, color = NeutralMidGray, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    IconButton(onClick = { if (portions > 1) { portions--; portionsText = portions.toString() } }, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(TealContainer)) { Icon(Icons.Default.Remove, null, tint = TealPrimary) }
                    Spacer(Modifier.width(16.dp))
                    OutlinedTextField(
                        value = portionsText,
                        onValueChange = { input -> val filtered = input.filter { it.isDigit() }.take(4); portionsText = filtered; val num = filtered.toIntOrNull(); if (num != null && num > 0) portions = num },
                        modifier = Modifier.width(120.dp), textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center, color = NeutralBlack, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    IconButton(onClick = { if (portions < 9999) { portions++; portionsText = portions.toString() } }, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(TealContainer)) { Icon(Icons.Default.Add, null, tint = TealPrimary) }
                }
            }

            RouteOptionCard(Icons.Default.Restaurant, TealContainer, TealPrimary, "Pelanggan Minta Menu Sendiri", "Tentukan menu spesifik", TealPrimary, null) { navigateAndSave(false) }
            RouteOptionCard(Icons.Default.SmartToy, OrangeAILight, OrangeAI, "Sesuaikan dengan Budget", "AI rekomendasikan menu", OrangeAI, { KmAiBadge() }) { navigateAndSave(true) }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { selectedDate = LocalDate.ofEpochDay(it / 86400000) }; showDatePicker = false }) { Text("Pilih", color = TealPrimary) } }) { DatePicker(state = datePickerState) }
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
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NeutralWhite), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().border(2.dp, accentColor, RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(iconBg), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconTint, modifier = Modifier.size(26.dp)) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = NeutralBlack)
                Text(subtitle, fontSize = 13.sp, color = NeutralMidGray, lineHeight = 18.sp)
            }
            if (badge != null) badge() else Icon(Icons.Default.ChevronRight, null, tint = accentColor)
        }
    }
}