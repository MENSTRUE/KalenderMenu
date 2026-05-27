package com.plenger.kalendermenu.ui.screens.aiRecommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmAiBadge
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmIngredientChip
import com.plenger.kalendermenu.ui.components.KmOrangeButton
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.screens.dashboard.formatRupiah
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.OrangeAI
import com.plenger.kalendermenu.ui.theme.OrangeAILight
import com.plenger.kalendermenu.ui.theme.StatusOrange
import com.plenger.kalendermenu.ui.theme.StatusOrangeLight
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun AiRecommendationScreen(
    navController: NavController,
    orderId: Long,
    viewModel: AiRecommendationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Rekomendasi AI",
                onBackClick = { navController.popBackStack() },
                actionIcon = null,
                onActionClick = null
            )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    KmAiBadge()
                }
            }

            item {
                BudgetInputCard(
                    budget = uiState.budgetInput,
                    portions = uiState.portions,
                    onBudgetChange = viewModel::onBudgetChange,
                    onSearch = viewModel::findBestMenus,
                    isLoading = uiState.isSearching
                )
            }

            if (uiState.recommendations.isNotEmpty()) {
                item {
                    Text(
                        "3 Menu Terbaik untuk Budget Anda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeutralBlack
                    )
                }

                items(uiState.recommendations.size) { index ->
                    val rec = uiState.recommendations[index]
                    RecommendationCard(
                        rank = index + 1,
                        recommendation = rec,
                        isBest = index == 0,
                        onSelect = {
                            navController.navigate(Screen.SendToSupplier.createRoute(orderId))
                        }
                    )
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusOrangeLight)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusOrange)
                            Spacer(Modifier.width(10.dp))
                            Text(uiState.errorMessage!!, color = StatusOrange, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetInputCard(
    budget: String,
    portions: Int,
    onBudgetChange: (String) -> Unit,
    onSearch: () -> Unit,
    isLoading: Boolean
) {
    KmCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SmartToy, contentDescription = null, tint = OrangeAI, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("Masukkan Budget Maksimal", fontSize = 15.sp, color = NeutralMidGray, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = budget,
            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 10) onBudgetChange(it) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Text("Rp", color = NeutralMidGray, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 14.dp))
            },
            textStyle = MaterialTheme.typography.displayMedium.copy(
                textAlign = TextAlign.End,
                color = NeutralBlack
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangeAI,
                unfocusedBorderColor = NeutralDivider
            ),
            placeholder = {
                Text(
                    "900000",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 28.sp,
                    color = NeutralLightGray
                )
            }
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Untuk $portions porsi · Ketuk untuk ubah",
            fontSize = 13.sp,
            color = NeutralMidGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        KmOrangeButton(
            text = "Cari Menu Terbaik",
            onClick = onSearch,
            icon = Icons.Default.AutoAwesome,
            isLoading = isLoading
        )
    }
}

@Composable
private fun RecommendationCard(
    rank: Int,
    recommendation: MenuRecommendation,
    isBest: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isBest) TealPrimary else NeutralDivider
    val rankBg = if (isBest) TealPrimary else NeutralLightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isBest) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(if (isBest) 2.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(rankBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(rank.toString(), color = NeutralWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(recommendation.menuName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                }
                if (isBest) {
                    Surface(shape = RoundedCornerShape(20.dp), color = OrangeAILight) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = OrangeAI, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Terbaik", color = OrangeAI, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("HPP per porsi: Rp ${formatRupiah(recommendation.hppPerPortion)}", fontSize = 14.sp, color = NeutralMidGray)
            Spacer(Modifier.height(2.dp))
            Text("Total: Rp ${formatRupiah(recommendation.totalHpp)}", fontSize = 18.sp, color = TealPrimary, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recommendation.keyIngredients.take(3).forEach { ing ->
                    KmIngredientChip(name = ing)
                }
            }

            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary)
            ) {
                Text("Pilih Menu Ini →", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}