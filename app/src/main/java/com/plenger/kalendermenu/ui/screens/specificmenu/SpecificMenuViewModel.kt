package com.plenger.kalendermenu.ui.screens.specificmenu

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

// ─── UI State ─────────────────────────────────────────────────────
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
    val errorMessage: String? = null,
    val isModelReady: Boolean = false
)

// ─── ViewModel ────────────────────────────────────────────────────
@HiltViewModel
class SpecificMenuViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recipeSearch: RecipeSearch,
    private val tfliteHelper: TfliteHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpecificMenuUiState())
    val uiState: StateFlow<SpecificMenuUiState> = _uiState.asStateFlow()

    init {
        // Cek apakah model siap
        viewModelScope.launch(Dispatchers.IO) {
            val ready = tryInitModel()
            _uiState.update { it.copy(isModelReady = ready) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, errorMessage = null) }
    }

    // ─── SEARCH MENU — pakai RecipeSearch (data real dari JSON) ──
    fun searchMenu() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ketik nama menu terlebih dahulu.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, result = null, errorMessage = null) }

            val result = withContext(Dispatchers.IO) {
                // 1. Cari resep dari database_resep_bersih.json
                val recipeResult = recipeSearch.cariMenu(query, porsi = 50)

                if (recipeResult == null) {
                    // Fallback ke mock data kalau tidak ketemu di JSON
                    return@withContext fallbackSearch(query)
                }

                // 2. Prediksi HPP pakai TFLite model
                val hpp = try {
                    if (_uiState.value.isModelReady) {
                        tfliteHelper.predictHpp(recipeResult.teks, porsi = 50).toLong()
                            .coerceAtLeast(100_000L)   // floor: minimum Rp 100rb
                    } else {
                        estimateHppFallback(recipeResult.bahan)
                    }
                } catch (e: Exception) {
                    estimateHppFallback(recipeResult.bahan)
                }

                // 3. Convert ke UI model
                MenuSearchResult(
                    menuName    = recipeResult.namaMenu,
                    portions    = 50,
                    ingredients = recipeResult.bahan.map {
                        IngredientItem(it.nama, it.jumlah)
                    },
                    estimatedHpp = hpp
                )
            }

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

    // ─── Estimasi HPP manual kalau model belum siap ───────────────
    private fun estimateHppFallback(bahan: List<RecipeSearch.BahanItem>): Long {
        // Harga rata-rata per bahan dikali 50 porsi
        val hargaPerBahan = mapOf(
            "daging sapi"   to 130000L,
            "ayam"          to  35000L,
            "santan"        to  18000L,
            "beras"         to  14000L,
            "cabai"         to  45000L,
            "bawang merah"  to  32000L,
            "bawang putih"  to  28000L,
            "tepung"        to  12000L,
            "telur"         to  28000L,
            "tempe"         to   8000L
        )
        var total = 0L
        bahan.forEach { b ->
            val namaLower = b.nama.lowercase()
            val harga = hargaPerBahan.entries
                .firstOrNull { (k, _) -> namaLower.contains(k) }?.value ?: 10000L
            // Extract angka dari jumlah, scale sederhana
            val angka = Regex("""(\d+)""").find(b.jumlah)
                ?.groupValues?.get(1)?.toLongOrNull() ?: 1L
            total += harga * angka / 10L
        }
        return total.coerceAtLeast(300_000L)
    }

    // ─── Fallback ke mock data ────────────────────────────────────
    private fun fallbackSearch(query: String): MenuSearchResult? {
        val normalized = query.lowercase().trim()
        val mockDb = mapOf(
            "rendang sapi" to MenuSearchResult(
                "Rendang Sapi", 50,
                listOf(
                    IngredientItem("Daging Sapi", "5 kg"),
                    IngredientItem("Santan", "3 liter"),
                    IngredientItem("Cabai Merah", "500 gr"),
                    IngredientItem("Serai", "10 btg"),
                    IngredientItem("Lengkuas", "200 gr"),
                    IngredientItem("Bawang Merah", "300 gr")
                ), 875000L
            ),
            "nasi gudeg" to MenuSearchResult(
                "Nasi Gudeg Komplit", 50,
                listOf(
                    IngredientItem("Nangka Muda", "3 kg"),
                    IngredientItem("Santan", "2 liter"),
                    IngredientItem("Telur", "25 butir"),
                    IngredientItem("Ayam", "2 kg")
                ), 760000L
            ),
            "ayam bakar" to MenuSearchResult(
                "Ayam Bakar Kecap", 50,
                listOf(
                    IngredientItem("Ayam Potong", "5 kg"),
                    IngredientItem("Kecap Manis", "500 ml"),
                    IngredientItem("Bawang Putih", "200 gr")
                ), 740000L
            ),
            "soto ayam" to MenuSearchResult(
                "Soto Ayam Lamongan", 50,
                listOf(
                    IngredientItem("Ayam Potong", "4 kg"),
                    IngredientItem("Kunyit", "100 gr"),
                    IngredientItem("Lontong", "3 kg")
                ), 880000L
            )
        )
        return mockDb.entries.firstOrNull { (key, _) ->
            normalized.contains(key) || key.contains(normalized)
        }?.value
    }

    // ─── Init model — return false kalau gagal ────────────────────
    private fun tryInitModel(): Boolean {
        return try {
            // Coba load model — kalau file tidak ada akan throw exception
            tfliteHelper.predictHpp("test ayam sapi", 1)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        tfliteHelper.close()
    }
}
