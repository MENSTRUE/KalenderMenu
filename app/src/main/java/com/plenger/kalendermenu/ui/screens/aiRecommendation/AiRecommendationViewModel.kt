package com.plenger.kalendermenu.ui.screens.aiRecommendation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plenger.kalendermenu.ml.PriceMapper
import com.plenger.kalendermenu.ml.RecipeSearch
import com.plenger.kalendermenu.ml.TfliteHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class MenuRecommendation(
    val menuName: String,
    val hppPerPortion: Long,
    val totalHpp: Long,
    val ingredients: List<String>
)

data class AiRecommendationUiState(
    val budgetInput: String = "",
    val portions: Int = 50,
    val date: String = "",
    val customerName: String = "",
    val selectedTheme: String = "Bebas",
    val isSearching: Boolean = false,
    val recommendations: List<MenuRecommendation> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AiRecommendationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recipeSearch: RecipeSearch,
    private val tfliteHelper: TfliteHelper,
    private val priceMapper: PriceMapper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val MIN_HPP_PER_PORSI = 1_000L
    }

    private val _uiState = MutableStateFlow(
        AiRecommendationUiState(
            portions = savedStateHandle.get<Int>("portions") ?: 50,
            date = savedStateHandle.get<String>("date") ?: "",
            customerName = savedStateHandle.get<String>("customerName") ?: "Pelanggan",
            budgetInput = savedStateHandle.get<String>("budget") ?: "",
            selectedTheme = savedStateHandle.get<String>("theme") ?: "Bebas"
        )
    )
    val uiState: StateFlow<AiRecommendationUiState> = _uiState.asStateFlow()

    fun onBudgetChange(value: String) {
        _uiState.update { it.copy(budgetInput = value, errorMessage = null) }
        savedStateHandle["budget"] = value
    }

    fun onThemeChange(theme: String) {
        _uiState.update { it.copy(selectedTheme = theme, errorMessage = null) }
        savedStateHandle["theme"] = theme
    }

    fun findBestMenus() {
        val budget = _uiState.value.budgetInput.toLongOrNull() ?: 0L
        if (budget <= 0L) {
            _uiState.update { it.copy(errorMessage = "Masukkan budget yang valid.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, recommendations = emptyList(), errorMessage = null) }

            val results = withContext(Dispatchers.IO) {
                val porsi = _uiState.value.portions
                val tema = _uiState.value.selectedTheme

                val candidates = try {
                    recipeSearch.getRecipesByTheme(tema, porsi)
                } catch (_: Exception) {
                    emptyList()
                }

                candidates
                    .mapNotNull { recipe ->
                        val rawTotalHpp = recipe.bahan.sumOf { bahan ->
                            val basePrice = if (bahan.jumlah.isNotEmpty()) {
                                priceMapper.hitungHargaBahan(bahan.nama, bahan.jumlah)
                            } else {
                                priceMapper.hitungHargaBahanRaw(bahan.nama)
                            }
                            basePrice * porsi
                        }

                        val totalHpp = if (rawTotalHpp < (porsi * 2000L)) (rawTotalHpp * 1.15).toLong() else rawTotalHpp

                        if (totalHpp <= 0L) return@mapNotNull null

                        val hppPerPorsi = totalHpp / porsi

                        if (hppPerPorsi < MIN_HPP_PER_PORSI) return@mapNotNull null
                        if (totalHpp !in 1..budget) return@mapNotNull null

                        MenuRecommendation(
                            menuName = recipe.namaMenu,
                            hppPerPortion = hppPerPorsi,
                            totalHpp = totalHpp,
                            ingredients = recipe.bahan.map { "${it.nama}: ${it.jumlah}" }
                        )
                    }
                    .distinctBy { rec ->
                        rec.menuName.lowercase()
                            .replace(Regex("\\b(dan|dengan|serta|plus)\\b"), "")
                            .replace(Regex("\\s+"), " ")
                            .trim()
                    }
                    .sortedByDescending { it.totalHpp }
                    .take(10)
            }

            _uiState.update {
                it.copy(
                    isSearching = false,
                    recommendations = results,
                    errorMessage = if (results.isEmpty())
                        "Tidak ada menu yang cocok untuk budget Rp ${String.format("%,d", budget).replace(',', '.')} dengan ${_uiState.value.portions} porsi."
                    else null
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try { tfliteHelper.close() } catch (_: Exception) {}
    }
}