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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmPriceTrendBadge
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
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
import com.plenger.kalendermenu.ui.theme.StatusOrange
import com.plenger.kalendermenu.ui.theme.TealPrimary

// ─── Data model ───────────────────────────────────────────────────
data class IngredientPriceEntry(
    val name: String,
    val unit: String,
    val currentPrice: Long,
    val priceDelta: Long,
    val category: String = "Lainnya"
)

// ─── Master data dengan kategori ─────────────────────────────────
private val masterIngredients = listOf(
    IngredientPriceEntry("Daging Sapi",   "per kg",    130000L, 5000L, "Daging"),
    IngredientPriceEntry("Ayam Potong",   "per kg",     35000L,    0L, "Daging"),
    IngredientPriceEntry("Beras",         "per kg",     14000L,    0L, "Lainnya"),
    IngredientPriceEntry("Cabai Merah",   "per kg",     45000L, 8000L, "Bumbu"),
    IngredientPriceEntry("Santan",        "per liter",  18000L,    0L, "Lainnya"),
    IngredientPriceEntry("Minyak Goreng", "per liter",  16000L,    0L, "Lainnya"),
    IngredientPriceEntry("Bawang Merah",  "per kg",     32000L, 2000L, "Bumbu"),
    IngredientPriceEntry("Bawang Putih",  "per kg",     28000L,    0L, "Bumbu"),
    IngredientPriceEntry("Wortel",        "per kg",     10000L,    0L, "Sayur"),
    IngredientPriceEntry("Bayam",         "per ikat",    3000L,    0L, "Sayur"),
)

// ─────────────────────────────────────────────────────────────────
// SCREEN: Update Ingredient Price
// ─────────────────────────────────────────────────────────────────
@Composable
fun UpdateIngredientPriceScreen(
    navController: NavController,
    recipeId: Long = 1L
) {
    val ingredients = remember {
        mutableStateListOf(
            IngredientPriceEntry("Daging Sapi",  "5 kg",   130000, 5000),
            IngredientPriceEntry("Santan",       "3 ltr",   18000,    0),
            IngredientPriceEntry("Cabai Merah",  "500gr",   45000, 8000),
            IngredientPriceEntry("Serai",        "10 btg",   5000,    0),
            IngredientPriceEntry("Lengkuas",     "200gr",   12000,    0),
            IngredientPriceEntry("Bawang Merah", "300gr",   22000, 2000)
        )
    }
    val prices = remember { ingredients.map { it.currentPrice.toString() }.toMutableStateList() }
    val oldHpp = 875000L
    val newHpp by remember(prices) {
        derivedStateOf {
            prices.mapIndexed { i, p ->
                val base = p.toLongOrNull() ?: ingredients[i].currentPrice
                when (i) {
                    0    -> base * 5
                    1    -> base * 3
                    2    -> (base * 0.5).toLong()
                    3    -> base * 10
                    4    -> (base * 0.2).toLong()
                    5    -> (base * 0.3).toLong()
                    else -> base
                }
            }.sum()
        }
    }
    val diff = newHpp - oldHpp

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
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Save, null, tint = NeutralWhite, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Simpan & Hitung Ulang HPP",
                        color = NeutralWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Info banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeAILight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangeAI.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, null, tint = OrangeAI, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Masukkan harga terbaru dari supplier. HPP dihitung ulang otomatis.",
                            fontSize = 14.sp,
                            color = StatusOrange,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Section label
            item {
                Text(
                    "Bahan Baku — Rendang Sapi (50 porsi)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NeutralBlack,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Ingredient rows — pakai itemsIndexed dari LazyListScope
            itemsIndexed(ingredients) { i, ing ->
                IngredientPriceRow(
                    entry = ing,
                    priceInput = prices[i],
                    onPriceChange = { prices[i] = it }
                )
            }

            // HPP comparison card
            item {
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Perbandingan HPP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NeutralBlack
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("HPP Lama", fontSize = 14.sp, color = NeutralMidGray)
                            Text(
                                "Rp ${formatRupiah(oldHpp)}",
                                fontSize = 14.sp,
                                color = NeutralMidGray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "HPP Baru",
                                fontSize = 14.sp,
                                color = TealPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Rp ${formatRupiah(newHpp)}",
                                fontSize = 22.sp,
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (diff != 0L) {
                            Text(
                                text = if (diff > 0)
                                    "Selisih +Rp ${formatRupiah(diff)} dari estimasi awal"
                                else
                                    "Selisih -Rp ${formatRupiah(-diff)} dari estimasi awal",
                                fontSize = 13.sp,
                                color = if (diff > 0) StatusOrange else TealPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Ingredient price row composable
// ─────────────────────────────────────────────────────────────────
@Composable
private fun IngredientPriceRow(
    entry: IngredientPriceEntry,
    priceInput: String,
    onPriceChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = NeutralBlack
                )
                Text(entry.unit, fontSize = 12.sp, color = NeutralMidGray)
            }
            KmPriceTrendBadge(delta = entry.priceDelta)
            OutlinedTextField(
                value = priceInput,
                onValueChange = { v ->
                    if (v.all { c -> c.isDigit() } && v.length <= 9) onPriceChange(v)
                },
                modifier = Modifier.width(110.dp).height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralBlack,
                    textAlign = TextAlign.End
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = NeutralDivider
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// SCREEN: Ingredient Price List
// ─────────────────────────────────────────────────────────────────
@Composable
fun IngredientPriceListScreen(navController: NavController) {

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Daging", "Sayur", "Bumbu")

    var ingredients  by remember { mutableStateOf(masterIngredients) }
    var lastUpdated  by remember { mutableStateOf("Hari ini · 08:30 WIB") }
    var showReloadDialog by remember { mutableStateOf(false) }

    val filteredList by remember(searchQuery, selectedCategory, ingredients) {
        derivedStateOf {
            ingredients.filter { ing ->
                val matchSearch   = searchQuery.isBlank() ||
                    ing.name.contains(searchQuery, ignoreCase = true)
                val matchCategory = selectedCategory == "Semua" ||
                    ing.category == selectedCategory
                matchSearch && matchCategory
            }
        }
    }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Harga Bahan Pasar",
                onMenuClick = {},
                actionIcon = Icons.Default.Refresh,
                onActionClick = { showReloadDialog = true }
            )
        },
        bottomBar = {
            KmBottomNavBar(
                selectedRoute = Screen.IngredientPriceList.route,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Diperbarui: $lastUpdated", fontSize = 12.sp, color = NeutralMidGray)
                        Text("Sumber: Pasar Tradisional", fontSize = 12.sp, color = NeutralMidGray)
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari nama bahan...", color = NeutralLightGray) },
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

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = {
                                    Text(
                                        cat,
                                        fontSize = 14.sp,
                                        color = if (isSelected) NeutralWhite else NeutralDarkGray
                                    )
                                },
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
                    Text(
                        "Harga Hari Ini  (${filteredList.size} bahan)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NeutralBlack
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            items(filteredList) { ing ->
                PriceListItem(
                    name  = ing.name,
                    unit  = ing.unit,
                    price = ing.currentPrice,
                    delta = ing.priceDelta
                )
                Spacer(Modifier.height(8.dp))
            }

            if (filteredList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Tidak ditemukan",
                            fontSize = 16.sp,
                            color = NeutralMidGray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Coba kata kunci atau kategori lain",
                            fontSize = 14.sp,
                            color = NeutralLightGray
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = {
                            navController.navigate(Screen.UpdateIngredientPrice.createRoute(1L))
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = NeutralWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Update Harga Dari Supplier",
                            color = NeutralWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showReloadDialog) {
        AlertDialog(
            onDismissRequest = { showReloadDialog = false },
            title = { Text("Perbarui Harga", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Mengambil data harga terbaru dari server. Harga akan diperbarui ke data hari ini.",
                    fontSize = 15.sp,
                    color = NeutralMidGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        ingredients  = masterIngredients.map { it.copy() }
                        lastUpdated  = "Baru saja diperbarui"
                        showReloadDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Perbarui", color = NeutralWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReloadDialog = false }) {
                    Text("Batal", color = NeutralMidGray)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Price list item composable
// ─────────────────────────────────────────────────────────────────
@Composable
private fun PriceListItem(name: String, unit: String, price: Long, delta: Long) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                    .background(TealPrimary, RoundedCornerShape(50))
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = NeutralBlack)
                Text(unit, fontSize = 13.sp, color = NeutralMidGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Rp ${formatRupiah(price)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralBlack
                )
                Spacer(Modifier.height(2.dp))
                KmPriceTrendBadge(delta = delta)
            }
        }
    }
}
