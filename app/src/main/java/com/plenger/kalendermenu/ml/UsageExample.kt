package com.plenger.kalendermenu.ml

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class MenuSpesifikUiState(
    val isLoading: Boolean = false,
    val namaMenu: String = "",
    val bahan: List<RecipeSearch.BahanItem> = emptyList(),
    val estimasiHpp: Long = 0L,
    val hppPerPorsi: Long = 0L,
    val error: String? = null
)

@HiltViewModel
class MenuSpesifikViewModel @Inject constructor(
    private val recipeSearch: RecipeSearch,
    private val tfliteHelper: TfliteHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuSpesifikUiState())
    val uiState: StateFlow<MenuSpesifikUiState> = _uiState

    fun cariMenu(namaMenu: String, porsi: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            withContext(Dispatchers.IO) {
                try {
                    val result = recipeSearch.cariMenu(namaMenu, porsi)

                    if (result == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Menu '$namaMenu' tidak ditemukan di database"
                        )
                        return@withContext
                    }

                    val totalHpp = tfliteHelper.predictHpp(result.teks, porsi).toLong()
                    val hppPerPorsi = if (porsi > 0) totalHpp / porsi else 0L

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        namaMenu = result.namaMenu,
                        bahan = result.bahan,
                        estimasiHpp = totalHpp,
                        hppPerPorsi = hppPerPorsi
                    )

                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Gagal memuat prediksi: ${e.message}"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tfliteHelper.close()
    }
}

data class RekomendasiUiState(
    val isLoading: Boolean = false,
    val rekomendasiList: List<RekomendasiItem> = emptyList(),
    val error: String? = null
)

data class RekomendasiItem(
    val rank: Int,
    val namaMenu: String,
    val hppPerPorsi: Long,
    val totalHpp: Long,
    val tags: List<String>,
    val isTerbaik: Boolean = false
)

@HiltViewModel
class RekomendasiAiViewModel @Inject constructor(
    private val recipeSearch: RecipeSearch,
    private val tfliteHelper: TfliteHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(RekomendasiUiState())
    val uiState: StateFlow<RekomendasiUiState> = _uiState

    fun cariRekomendasiByBudget(budget: Long, porsi: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            withContext(Dispatchers.IO) {
                try {
                    val candidates = recipeSearch.getRecipesByTheme("Bebas", porsi).take(20)

                    val predictions = candidates.map { recipe ->
                        val hpp = tfliteHelper.predictHpp(recipe.teks, porsi).toLong()
                        Pair(recipe, hpp)
                    }

                    val filtered = predictions
                        .filter { (_, hpp) -> hpp <= budget }
                        .sortedBy { (_, hpp) -> hpp }
                        .take(3)

                    val rekomendasi = filtered.mapIndexed { idx, (recipe, totalHpp) ->
                        val hppPerPorsi = if (porsi > 0) totalHpp / porsi else 0L
                        val tags = recipe.bahan.take(3).map { it.nama }

                        RekomendasiItem(
                            rank = idx + 1,
                            namaMenu = recipe.namaMenu,
                            hppPerPorsi = hppPerPorsi,
                            totalHpp = totalHpp,
                            tags = tags,
                            isTerbaik = idx == 0
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        rekomendasiList = rekomendasi
                    )

                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Gagal memuat rekomendasi: ${e.message}"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tfliteHelper.close()
    }
}