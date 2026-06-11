package com.plenger.kalendermenu.ui.screens.ingredientprice

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.data.OrderRepository
import com.plenger.kalendermenu.ml.PriceHelper
import com.plenger.kalendermenu.ui.components.KmBottomNavBar
import com.plenger.kalendermenu.ui.components.KmPriceTrendBadge
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.screens.dashboard.formatRupiah
import com.plenger.kalendermenu.ui.theme.*
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.util.Locale

data class IngredientPriceEntry(
    val name: String,
    val unit: String,
    val currentPrice: Long,
    val priceDelta: Long = 0L,
    val category: String = "Lainnya",
    val masterKey: String = ""
)

@Composable
fun UpdateIngredientPriceScreen(
    navController: NavController,
    recipeId: Long = 1L
) {
    val context    = LocalContext.current
    val repository = remember { OrderRepository.getInstance(context) }
    val order      = remember(recipeId) { repository.getOrderById(recipeId) }
    val masterPrices = remember(context) { PriceHelper.loadMasterIngredients(context) }

    val ingredients = remember(order) {
        val list = mutableStateListOf<IngredientPriceEntry>()
        order?.ingredients?.forEach { ingStr ->
            val parts = ingStr.split(":")
            val name  = parts.firstOrNull()?.trim() ?: return@forEach
            val unit  = parts.drop(1).joinToString(":").trim().ifEmpty { "secukupnya" }

            val nameLower = name.lowercase()
            val price = masterPrices.entries.firstOrNull { (k, _) ->
                nameLower.contains(k) || k.contains(nameLower)
            }?.value ?: 15_000L

            list.add(IngredientPriceEntry(name, unit, price, 0L))
        }
        list
    }

    val prices = remember(ingredients) {
        ingredients.map { it.currentPrice.toString() }.toMutableStateList()
    }
    val oldHpp = order?.hpp ?: 875_000L

    val newHpp by remember(prices) {
        derivedStateOf {
            var total = 0L
            prices.forEachIndexed { i, p ->
                val base    = p.toLongOrNull() ?: ingredients[i].currentPrice
                val qtyStr  = ingredients[i].unit
                val angka   = Regex("""([\d.,]+)""").find(qtyStr)
                    ?.groupValues?.get(1)
                    ?.replace(",", ".")
                    ?.toDoubleOrNull() ?: 1.0
                total += (base * angka).toLong()
            }
            total.coerceAtLeast(1L)
        }
    }
    val diff = newHpp - oldHpp

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title       = "Update Harga Bahan",
                onBackClick = { navController.popBackStack() },
                actionIcon  = Icons.Outlined.Info,
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
                    onClick = {
                        if (order != null) repository.saveOrder(order.copy(hpp = newHpp))
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Save, null, tint = NeutralWhite, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Simpan & Hitung Ulang HPP",
                        color = NeutralWhite, fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape  = RoundedCornerShape(12.dp),
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
                            "Harga bawaan dari PIHPS Bank Indonesia (Juni 2026). " +
                                    "Edit jika harga supplier berbeda.",
                            fontSize  = 14.sp, color = StatusOrange, lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Text(
                    "Bahan Baku — ${order?.menuName ?: "Menu"} (${order?.portions ?: 0} porsi)",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NeutralBlack,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            itemsIndexed(ingredients) { i, ing ->
                IngredientPriceRow(
                    entry       = ing,
                    priceInput  = prices[i],
                    onPriceChange = { prices[i] = it }
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = NeutralWhite),
                    border    = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Perbandingan HPP", fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = NeutralBlack)
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("HPP Lama", fontSize = 14.sp, color = NeutralMidGray)
                            Text("Rp ${formatRupiah(oldHpp)}", fontSize = 14.sp,
                                color = NeutralMidGray,
                                textDecoration = TextDecoration.LineThrough)
                        }
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("HPP Baru", fontSize = 14.sp, color = TealPrimary,
                                fontWeight = FontWeight.SemiBold)
                            Text("Rp ${formatRupiah(newHpp)}", fontSize = 22.sp,
                                color = TealPrimary, fontWeight = FontWeight.Bold)
                        }
                        if (diff != 0L) {
                            Text(
                                text = if (diff > 0)
                                    "▲ Naik Rp ${formatRupiah(diff)} dari estimasi awal"
                                else
                                    "▼ Turun Rp ${formatRupiah(-diff)} dari estimasi awal",
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

@Composable
fun IngredientPriceListScreen(navController: NavController) {
    val context = LocalContext.current
    val allIngredients = remember(context) {
        // Integrasi PriceHelper langsung ke sini
        val rawMap = PriceHelper.loadMasterIngredients(context)
        rawMap.map { (name, price) -> IngredientPriceEntry(name, "per kg", price) }
    }

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var showReloadDialog by remember { mutableStateOf(false) }

    val filteredList by remember(searchQuery, selectedCategory, allIngredients) {
        derivedStateOf {
            allIngredients.filter { ing ->
                val matchSearch   = searchQuery.isBlank() ||
                        ing.name.contains(searchQuery, ignoreCase = true)
                matchSearch
            }
        }
    }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title         = "Harga Bahan Pasar",
                onMenuClick   = {},
                actionIcon    = Icons.Default.Refresh,
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
                        restoreState    = true
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari nama bahan...", color = NeutralLightGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = NeutralMidGray) },
                        singleLine  = true,
                        shape  = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = TealPrimary,
                            unfocusedBorderColor  = NeutralDivider,
                            unfocusedContainerColor = NeutralWhite,
                            focusedContainerColor   = NeutralWhite
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            items(filteredList) { ing ->
                PriceListItem(
                    name  = ing.name,
                    unit  = ing.unit,
                    price = ing.currentPrice,
                    delta = 0L
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun IngredientPriceRow(
    entry: IngredientPriceEntry,
    priceInput: String,
    onPriceChange: (String) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NeutralWhite),
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
                Text(entry.name, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = NeutralBlack)
                Text(entry.unit, fontSize = 12.sp, color = NeutralMidGray)
            }
            OutlinedTextField(
                value    = priceInput,
                onValueChange = { v ->
                    if (v.all { c -> c.isDigit() } && v.length <= 9) onPriceChange(v)
                },
                modifier = Modifier.width(110.dp).height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine  = true,
                textStyle   = TextStyle(
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = NeutralBlack,
                    textAlign  = TextAlign.End
                ),
                shape  = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TealPrimary,
                    unfocusedBorderColor = NeutralDivider
                )
            )
        }
    }
}

@Composable
private fun PriceListItem(name: String, unit: String, price: Long, delta: Long) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp)
                .background(TealPrimary, RoundedCornerShape(50)))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp, color = NeutralBlack)
                Text(unit, fontSize = 13.sp, color = NeutralMidGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Rp ${formatRupiah(price)}",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack
                )
            }
        }
    }
}