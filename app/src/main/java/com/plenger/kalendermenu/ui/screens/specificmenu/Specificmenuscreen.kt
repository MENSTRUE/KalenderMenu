package com.plenger.kalendermenu.ui.screens.specificmenu

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.screens.dashboard.formatRupiah
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.StatusRed
import com.plenger.kalendermenu.ui.theme.StatusRedLight
import com.plenger.kalendermenu.ui.theme.TealContainer
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun SpecificMenuScreen(
    navController: NavController,
    orderId: Long,
    viewModel: SpecificMenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(title = "Menu Spesifik", onBackClick = { navController.popBackStack() })
        },
        bottomBar = {
            if (uiState.result != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeutralWhite)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tombol Pasang Pengingat Kalender (Sudah Putih)
                    Button(
                        onClick = {
                            navController.navigate(
                                Screen.CalendarReminder.createRoute(
                                    orderId,
                                    uiState.result!!.menuName
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = NeutralWhite
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Pasang Pengingat Kalender",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeutralWhite
                        )
                    }

                    // Tombol Kirim ke Supplier (Sudah Dirapikan)
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.SendToSupplier.createRoute(orderId)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TealPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.ChatBubble,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = TealPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Kirim ke Supplier",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealPrimary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SearchCard(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onQueryChange,
                    onSearch = viewModel::searchMenu,
                    isLoading = uiState.isSearching
                )
            }

            if (uiState.result != null) {
                val result = uiState.result!!
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Daftar Bahan Baku",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeutralBlack
                        )
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TealContainer
                        ) {
                            Text(
                                "${result.portions} Porsi",
                                color = TealPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                result.menuName,
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Dari dataset 14.000 resep Indonesia",
                                color = NeutralMidGray,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = NeutralDivider)
                        }
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            result.ingredients.forEach { ing ->
                                IngredientRow(name = ing.name, quantity = ing.quantity)
                                HorizontalDivider(color = NeutralDivider.copy(alpha = 0.5f))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                item {
                    HppEstimateCard(hpp = result.estimatedHpp)
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    ErrorCard(message = uiState.errorMessage!!)
                }
            }
        }
    }
}

@Composable
private fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isLoading: Boolean
) {
    KmCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cari Nama Menu", fontSize = 14.sp, color = NeutralMidGray)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("contoh: Rendang Sapi", color = NeutralLightGray, fontSize = 17.sp) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = NeutralBlack),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = NeutralDivider
            )
        )
        Spacer(Modifier.height(12.dp))

        // PERBAIKAN: Tombol Cari Menu dirubah ke standar agar bisa dikunci ke warna putih
        Button(
            onClick = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary // Warna hijau tosca
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading // Cegah klik berulang saat loading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = NeutralWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = NeutralWhite // Memaksa Ikon jadi putih
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Cari Menu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralWhite // Memaksa Teks jadi putih
                )
            }
        }
    }
}

@Composable
private fun IngredientRow(name: String, quantity: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 16.sp, color = NeutralBlack, fontWeight = FontWeight.Normal)
        Text(quantity, fontSize = 16.sp, color = TealPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HppEstimateCard(hpp: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, TealPrimary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .background(TealPrimary, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.height(4.dp))
            Text("Estimasi Modal Awal", fontSize = 13.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Rp ${formatRupiah(hpp)}",
                style = MaterialTheme.typography.displayLarge,
                color = NeutralBlack,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StatusRedLight)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Error, contentDescription = null, tint = StatusRed)
            Spacer(Modifier.width(10.dp))
            Text(message, color = StatusRed, fontSize = 15.sp)
        }
    }
}