package com.plenger.kalendermenu.ui.screens.specificmenu

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

data class IngredientItem(val name: String, val quantity: String)

data class MenuSearchResult(
    val menuName: String,
    val portions: Int,
    val ingredients: List<IngredientItem>,
    val estimatedHpp: Long
)

data class SpecificMenuUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val result: MenuSearchResult? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SpecificMenuViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SpecificMenuUiState())
    val uiState: StateFlow<SpecificMenuUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, errorMessage = null) }
    }

    fun searchMenu() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ketik nama menu terlebih dahulu.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, result = null, errorMessage = null) }
            delay(600)
            val result = fuzzySearchLocal(query)
            if (result != null) {
                _uiState.update { it.copy(isSearching = false, result = result) }
            } else {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = "Menu \"$query\" tidak ditemukan di dataset. Coba nama lain."
                    )
                }
            }
        }
    }

    private fun fuzzySearchLocal(query: String): MenuSearchResult? {
        val normalized = query.lowercase().trim()
        val mockDatabase = mapOf(
            "rendang sapi" to MenuSearchResult(
                menuName = "Rendang Sapi",
                portions = 50,
                ingredients = listOf(
                    IngredientItem("Daging Sapi", "5 kg"),
                    IngredientItem("Santan", "3 liter"),
                    IngredientItem("Cabai Merah", "500 gr"),
                    IngredientItem("Serai", "10 btg"),
                    IngredientItem("Lengkuas", "200 gr"),
                    IngredientItem("Bawang Merah", "300 gr")
                ),
                estimatedHpp = 875000
            ),
            "nasi gudeg" to MenuSearchResult(
                menuName = "Nasi Gudeg Komplit",
                portions = 50,
                ingredients = listOf(
                    IngredientItem("Nangka Muda", "3 kg"),
                    IngredientItem("Santan", "2 liter"),
                    IngredientItem("Telur", "25 butir"),
                    IngredientItem("Ayam", "2 kg"),
                    IngredientItem("Krecek", "500 gr")
                ),
                estimatedHpp = 760000
            ),
            "ayam bakar" to MenuSearchResult(
                menuName = "Ayam Bakar Kecap",
                portions = 50,
                ingredients = listOf(
                    IngredientItem("Ayam Potong", "5 kg"),
                    IngredientItem("Kecap Manis", "500 ml"),
                    IngredientItem("Bawang Putih", "200 gr"),
                    IngredientItem("Kemiri", "100 gr"),
                    IngredientItem("Jahe", "150 gr")
                ),
                estimatedHpp = 740000
            ),
            "soto ayam" to MenuSearchResult(
                menuName = "Soto Ayam Lamongan",
                portions = 50,
                ingredients = listOf(
                    IngredientItem("Ayam Potong", "4 kg"),
                    IngredientItem("Kunyit", "100 gr"),
                    IngredientItem("Lontong", "3 kg"),
                    IngredientItem("Kol", "1 kg"),
                    IngredientItem("Telur", "20 butir")
                ),
                estimatedHpp = 880000
            )
        )
        return mockDatabase.entries.firstOrNull { (key, _) ->
            normalized.contains(key) || key.contains(normalized) ||
                    levenshteinSimilarity(normalized, key) >= 0.6
        }?.value
    }

    private fun levenshteinSimilarity(a: String, b: String): Double {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
                else minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
            }
        }
        val maxLen = maxOf(a.length, b.length).toDouble()
        return if (maxLen == 0.0) 1.0 else 1.0 - dp[a.length][b.length] / maxLen
    }
}