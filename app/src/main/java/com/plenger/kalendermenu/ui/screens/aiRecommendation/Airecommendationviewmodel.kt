package com.plenger.kalendermenu.ui.screens.aiRecommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuRecommendation(
    val menuName: String,
    val hppPerPortion: Long,
    val totalHpp: Long,
    val keyIngredients: List<String>
)

data class AiRecommendationUiState(
    val budgetInput: String = "900000",
    val portions: Int = 50,
    val isSearching: Boolean = false,
    val recommendations: List<MenuRecommendation> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AiRecommendationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AiRecommendationUiState())
    val uiState: StateFlow<AiRecommendationUiState> = _uiState.asStateFlow()

    fun onBudgetChange(value: String) {
        _uiState.update { it.copy(budgetInput = value, errorMessage = null) }
    }

    fun findBestMenus() {
        val budget = _uiState.value.budgetInput.toLongOrNull() ?: 0L
        val portions = _uiState.value.portions
        if (budget <= 0L) {
            _uiState.update { it.copy(errorMessage = "Masukkan budget yang valid terlebih dahulu.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, recommendations = emptyList(), errorMessage = null) }
            delay(800)
            val results = batchScoringMock(budget, portions)
            if (results.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = "Tidak ada menu yang sesuai budget Rp ${budget}. Coba naikkan budget."
                    )
                }
            } else {
                _uiState.update { it.copy(isSearching = false, recommendations = results) }
            }
        }
    }

    private fun batchScoringMock(budget: Long, portions: Int): List<MenuRecommendation> {
        val allMenus = listOf(
            MenuRecommendation("Ayam Bakar Kecap", 14800L, 14800L * portions, listOf("Ayam", "Kecap Manis", "Bawang Putih")),
            MenuRecommendation("Nasi Gudeg Komplit", 15200L, 15200L * portions, listOf("Nangka", "Santan", "Telur")),
            MenuRecommendation("Soto Ayam Lamongan", 17600L, 17600L * portions, listOf("Ayam", "Kunyit", "Lontong")),
            MenuRecommendation("Nasi Kotak Ayam", 15000L, 15000L * portions, listOf("Ayam", "Nasi", "Tempe")),
            MenuRecommendation("Rendang Sapi", 17500L, 17500L * portions, listOf("Daging Sapi", "Santan", "Cabai")),
            MenuRecommendation("Opor Ayam", 13500L, 13500L * portions, listOf("Ayam", "Santan", "Serai")),
        )
        return allMenus
            .filter { it.totalHpp <= budget }
            .sortedBy { it.totalHpp }
            .take(3)
    }
}