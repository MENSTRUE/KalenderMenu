package com.plenger.kalendermenu.ui.screens.ingredientprice

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmPriceTrendBadge
import com.plenger.kalendermenu.ui.components.KmPrimaryButton
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.screens.dashboard.formatRupiah
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDarkGray
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.OrangeAI
import com.plenger.kalendermenu.ui.theme.OrangeAILight
import com.plenger.kalendermenu.ui.theme.StatusGreen
import com.plenger.kalendermenu.ui.theme.StatusOrange
import com.plenger.kalendermenu.ui.theme.TealPrimary

data class IngredientPriceEntry(
    val name: String,
    val unit: String,
    val currentPrice: Long,
    val priceDelta: Long
)

@Composable
fun UpdateIngredientPriceScreen(
    navController: NavController,
    recipeId: Long
) {
    val ingredients = remember {
        mutableStateListOf(
            IngredientPriceEntry("Daging Sapi", "5 kg", 130000, 5000),
            IngredientPriceEntry("Santan", "3 ltr", 18000, 0),
            IngredientPriceEntry("Cabai Merah", "500gr", 45000, 8000),
            IngredientPriceEntry("Serai", "10 btg", 5000, 0),
            IngredientPriceEntry("Lengkuas", "200gr", 12000, 0),
            IngredientPriceEntry("Bawang Merah", "300gr", 22000, 2000)
        )
    }
    val prices = remember { ingredients.map { it.currentPrice.toString() }.toMutableStateList() }
    val oldHpp = 875000L
    val newHpp by remember(prices) {
        derivedStateOf {
            val total = prices.mapIndexed { i, p ->
                val base = p.toLongOrNull() ?: ingredients[i].currentPrice
                when (i) {
                    0 -> base * 5
                    1 -> base * 3
                    2 -> (base * 0.5).toLong()
                    3 -> base * 10
                    4 -> (base * 0.2).toLong()
                    5 -> (base * 0.3).toLong()
                    else -> base
                }
            }.sum()
            total
        }
    }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Update Harga Bahan",
                onBackClick = { navController.popBackStack() },
                actionIcon = Icons.Outlined.Info,
                onActionClick = {}
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeutralWhite)
                    .padding(16.dp)
            ) {
                KmPrimaryButton(
                    text = "Simpan & Hitung Ulang HPP",
                    onClick = { navController.popBackStack() },
                    icon = Icons.Default.Save
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeAILight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangeAI.copy(alpha = 0.4f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = OrangeAI, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Masukkan harga terbaru dari supplier. HPP dihitung ulang otomatis.",
                            fontSize = 14.sp,
                            color = StatusOrange,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Bahan Baku — Rendang Sapi (50 porsi)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralBlack
                )
                Spacer(Modifier.height(12.dp))
            }

            items(ingredients.indices.toList()) { i ->
                val ing = ingredients[i]
                PriceInputRow(
                    name = ing.name,
                    unit = ing.unit,
                    price = prices[i],
                    delta = ing.priceDelta,
                    onPriceChange = { prices[i] = it }
                )
                Spacer(Modifier.height(10.dp))
            }

            item {
                Spacer(Modifier.height(8.dp))
                HppComparisonCard(oldHpp = oldHpp, newHpp = newHpp)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PriceInputRow(
    name: String,
    unit: String,
    price: String,
    delta: Long,
    onPriceChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = NeutralBlack)
                Text(unit, fontSize = 13.sp, color = NeutralMidGray)
            }
            Spacer(Modifier.width(10.dp))
            KmPriceTrendBadge(delta = delta)
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 8) onPriceChange(it) },
                modifier = Modifier.width(110.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeutralBlack,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = NeutralDivider
                )
            )
        }
    }
}

@Composable
private fun HppComparisonCard(oldHpp: Long, newHpp: Long) {
    val diff = newHpp - oldHpp
    val isUp = diff > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Perbandingan HPP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack)
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("HPP Lama", fontSize = 15.sp, color = NeutralMidGray)
                Text(
                    "Rp ${formatRupiah(oldHpp)}",
                    fontSize = 15.sp,
                    color = NeutralLightGray,
                    textDecoration = TextDecoration.LineThrough
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("HPP Baru", fontSize = 16.sp, color = TealPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    "Rp ${formatRupiah(newHpp)}",
                    fontSize = 22.sp,
                    color = NeutralBlack,
                    fontWeight = FontWeight.Bold
                )
            }
            if (diff != 0L) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "${if (isUp) "Selisih +Rp" else "Hemat Rp"} ${formatRupiah(Math.abs(diff))} dari estimasi awal",
                    fontSize = 13.sp,
                    color = if (isUp) StatusOrange else StatusGreen
                )
            }
        }
    }
}

@Composable
fun IngredientPriceListScreen(navController: NavController) {
    val ingredients = listOf(
        Triple("Daging Sapi", "per kg", 130000L to 5000L),
        Triple("Ayam Potong", "per kg", 35000L to 0L),
        Triple("Beras", "per kg", 14000L to 0L),
        Triple("Cabai Merah", "per kg", 45000L to 8000L),
        Triple("Santan", "per liter", 18000L to 0L),
        Triple("Minyak Goreng", "per liter", 16000L to 0L),
        Triple("Bawang Merah", "per kg", 32000L to 2000L),
    )
    val selectedCategory = remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Daging", "Sayur", "Bumbu")

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Harga Bahan Pasar",
                onMenuClick = {},
                actionIcon = Icons.Default.Refresh,
                onActionClick = {}
            )
        },
        bottomBar = {
            KmBottomNavBar(
                selectedRoute = "ingredient_price_list",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Diperbarui: Hari ini · 08:30 WIB", fontSize = 12.sp, color = NeutralMidGray)
                        Text("Sumber: Pasar Tradisional", fontSize = 12.sp, color = NeutralMidGray)
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari nama bahan...", color = NeutralLightGray) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NeutralMidGray)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = NeutralDivider,
                            unfocusedContainerColor = NeutralWhite,
                            focusedContainerColor = NeutralWhite
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory.value == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory.value = cat },
                                label = { Text(cat, fontSize = 14.sp) },
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
                    Spacer(Modifier.height(12.dp))
                    Text("Harga Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack)
                    Spacer(Modifier.height(8.dp))
                }
            }

            items(ingredients) { (name, unit, priceData) ->
                val (price, delta) = priceData
                PriceListItem(name = name, unit = unit, price = price, delta = delta)
                Spacer(Modifier.height(8.dp))
            }

            item {
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    KmPrimaryButton(
                        text = "Update Harga dari Supplier",
                        onClick = { navController.navigate("update_price/1") },
                        icon = Icons.Default.Edit
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceListItem(name: String, unit: String, price: Long, delta: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(TealPrimary, shape = RoundedCornerShape(50))
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = NeutralBlack)
                Text(unit, fontSize = 13.sp, color = NeutralMidGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Rp ${formatRupiah(price)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack)
                Spacer(Modifier.height(2.dp))
                KmPriceTrendBadge(delta = delta)
            }
        }
    }
}