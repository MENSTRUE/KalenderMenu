package com.plenger.kalendermenu.ui.screens.aiRecommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.plenger.kalendermenu.data.OrderRepository
import com.plenger.kalendermenu.data.OrderSummary
import com.plenger.kalendermenu.ui.components.*
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AiRecommendationScreen(
    navController: NavController,
    orderId: Long,
    viewModel: AiRecommendationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = NeutralBackground,
        topBar = { KmTopBar(title = "Rekomendasi AI", onBackClick = { navController.popBackStack() }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { KmAiBadge() } }
            item {
                BudgetInputCard(
                    budget = uiState.budgetInput,
                    selectedTheme = uiState.selectedTheme,
                    onBudgetChange = viewModel::onBudgetChange,
                    onThemeChange = viewModel::onThemeChange,
                    onSearch = viewModel::findBestMenus,
                    isLoading = uiState.isSearching
                )
            }
            if (uiState.errorMessage != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = StatusRedLight)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = StatusRed)
                            Spacer(Modifier.width(10.dp))
                            Text(uiState.errorMessage!!, color = StatusRed, fontSize = 14.sp)
                        }
                    }
                }
            }
            items(uiState.recommendations) { rec ->
                RecommendationCard(rec, onSelect = {
                    val repo = OrderRepository.getInstance(context)
                    val d = try { LocalDate.parse(uiState.date) } catch (_: Exception) { LocalDate.now() }

                    repo.saveOrder(OrderSummary(
                        orderId, uiState.customerName, rec.menuName, uiState.portions,
                        rec.totalHpp, d.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID"))),
                        d.format(DateTimeFormatter.ofPattern("EEE", Locale("id", "ID"))).uppercase(),
                        d.format(DateTimeFormatter.ofPattern("dd", Locale("id", "ID"))),
                        "draft",
                        rec.ingredients
                    ))

                    navController.navigate(Screen.CalendarReminder.createRoute(orderId, rec.menuName))
                })
            }
        }
    }
}

@Composable
private fun BudgetInputCard(budget: String, selectedTheme: String, onBudgetChange: (String) -> Unit, onThemeChange: (String) -> Unit, onSearch: () -> Unit, isLoading: Boolean) {
    val disp = if (budget.isNotEmpty()) String.format("%,d", budget.toLongOrNull() ?: 0L).replace(',', '.') else ""
    KmCard {
        Text("Budget Maksimal", fontWeight = FontWeight.SemiBold, color = NeutralBlack)
        OutlinedTextField(
            value = disp,
            onValueChange = { onBudgetChange(it.filter { c -> c.isDigit() }) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Text("Rp", color = TealPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NeutralBlack, unfocusedTextColor = NeutralBlack)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Bebas", "Ayam", "Sapi", "Kambing", "Ikan")) { theme ->
                FilterChip(
                    selected = selectedTheme == theme,
                    onClick = { onThemeChange(theme) },
                    label = { Text(theme) }
                )
            }
        }
        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = Color.White)
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            else Text("Cari Menu Terbaik", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecommendationCard(rec: MenuRecommendation, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(rec.menuName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
            Text("Total HPP: Rp ${String.format("%,d", rec.totalHpp).replace(',', '.')}", color = TealPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = Color.White)
            ) {
                Text("Pilih Menu Ini", fontWeight = FontWeight.Bold)
            }
        }
    }
}