package com.plenger.kalendermenu.ui.screens.aiRecommendation

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
    val isModelReady: Boolean = false,
    val recommendations: List<MenuRecommendation> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AiRecommendationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recipeSearch: RecipeSearch,
    private val tfliteHelper: TfliteHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiRecommendationUiState())
    val uiState: StateFlow<AiRecommendationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val ready = tryInitModel()
            _uiState.update { it.copy(isModelReady = ready) }
        }
    }

    fun onBudgetChange(value: String) {
        _uiState.update { it.copy(budgetInput = value, errorMessage = null) }
    }

    fun findBestMenus() {
        val budget  = _uiState.value.budgetInput.toLongOrNull() ?: 0L
        val portions = _uiState.value.portions

        if (budget <= 0L) {
            _uiState.update { it.copy(errorMessage = "Masukkan budget yang valid terlebih dahulu.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, recommendations = emptyList(), errorMessage = null) }

            val results = withContext(Dispatchers.IO) {
                runRecommendation(budget, portions)
            }

            if (results.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isSearching  = false,
                        errorMessage = "Tidak ada menu yang sesuai budget Rp ${formatBudget(budget)}. Coba naikkan budget."
                    )
                }
            } else {
                _uiState.update { it.copy(isSearching = false, recommendations = results) }
            }
        }
    }

    private fun runRecommendation(budget: Long, portions: Int): List<MenuRecommendation> {
        // 1. Ambil kandidat dari database JSON
        val candidates = try {
            recipeSearch.getTopRecipes(50)   // ambil 50 kandidat
        } catch (e: Exception) {
            emptyList()
        }

        if (candidates.isNotEmpty()) {
            val scored = candidates.mapNotNull { recipe ->
                // ── Bersihkan nama menu dari karakter aneh ─────────
                val cleanName = recipe.namaMenu
                    .replace(Regex("""[^\w\s\-+&()]"""), "")   // hapus karakter aneh
                    .replace(Regex("""\\u[0-9a-fA-F]{4}"""), "") // hapus unicode escape
                    .trim()
                    .replaceFirstChar { it.uppercase() }

                if (cleanName.isBlank()) return@mapNotNull null

                // ── Prediksi HPP ───────────────────────────────────
                val totalHpp = try {
                    if (_uiState.value.isModelReady) {
                        tfliteHelper.predictHpp(recipe.teks, portions).toLong()
                            .coerceIn(100_000L, 5_000_000L)
                    } else {
                        estimateHpp(recipe.bahan, portions)
                    }
                } catch (e: Exception) {
                    estimateHpp(recipe.bahan, portions)
                }

                if (totalHpp > budget) return@mapNotNull null

                MenuRecommendation(
                    menuName       = cleanName,
                    hppPerPortion  = if (portions > 0) totalHpp / portions else 0L,
                    totalHpp       = totalHpp,
                    keyIngredients = recipe.bahan.take(3).map { b ->
                        b.nama.replaceFirstChar { it.uppercase() }
                    }
                )
            }
            .filter { it.menuName.length >= 3 }      // filter nama terlalu pendek
            .sortedBy { it.totalHpp }
            .take(3)

            if (scored.isNotEmpty()) return scored
        }

        // 2. Fallback ke data hardcode yang benar (bukan kambing semua)
        return fallbackRecommendation(budget, portions)
    }

    private fun estimateHpp(bahan: List<RecipeSearch.BahanItem>, portions: Int): Long {
        val hargaRef = mapOf(
            "daging sapi"   to 130000L,
            "daging kambing" to 110000L,
            "ayam"          to  35000L,
            "udang"         to  80000L,
            "ikan"          to  45000L,
            "santan"        to  18000L,
            "beras"         to  14000L,
            "nasi"          to  14000L,
            "cabai"         to  45000L,
            "bawang merah"  to  32000L,
            "bawang putih"  to  28000L,
            "telur"         to  28000L,
            "tempe"         to   8000L,
            "tahu"          to   6000L,
            "kentang"       to  15000L,
            "wortel"        to  10000L,
            "tepung"        to  12000L,
            "minyak"        to  16000L
        )
        var total = 0L
        bahan.forEach { b ->
            val key   = b.nama.lowercase()
            val harga = hargaRef.entries
                .firstOrNull { (k, _) -> key.contains(k) }?.value ?: 8000L
            val angka = Regex("""(\d+(?:\.\d+)?)""")
                .find(b.jumlah)?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
            total += (harga * angka / 10.0).toLong()
        }
        return (total * portions / 10L).coerceIn(200_000L, 5_000_000L)
    }

    private fun fallbackRecommendation(budget: Long, portions: Int): List<MenuRecommendation> {
        val allMenus = listOf(
            MenuRecommendation("Ayam Bakar Kecap",   14800L, 14800L * portions, listOf("Ayam", "Kecap Manis", "Bawang Putih")),
            MenuRecommendation("Nasi Gudeg Komplit",  15200L, 15200L * portions, listOf("Nangka", "Santan", "Telur")),
            MenuRecommendation("Soto Ayam Lamongan",  17600L, 17600L * portions, listOf("Ayam", "Kunyit", "Lontong")),
            MenuRecommendation("Opor Ayam",           13500L, 13500L * portions, listOf("Ayam", "Santan", "Serai")),
            MenuRecommendation("Rendang Sapi",        17500L, 17500L * portions, listOf("Daging Sapi", "Santan", "Cabai")),
            MenuRecommendation("Nasi Kotak Ayam",     15000L, 15000L * portions, listOf("Ayam", "Nasi", "Tempe")),
            MenuRecommendation("Capcay Sayuran",       8500L,  8500L * portions, listOf("Wortel", "Kol", "Bakso")),
            MenuRecommendation("Nasi Uduk Komplit",   12000L, 12000L * portions, listOf("Beras", "Santan", "Serai")),
        )
        return allMenus
            .filter { it.totalHpp <= budget }
            .sortedBy { it.totalHpp }
            .take(3)
    }

    private fun tryInitModel(): Boolean = try {
        tfliteHelper.predictHpp("ayam sapi santan cabai", 1)
        true
    } catch (e: Exception) { false }

    private fun formatBudget(amount: Long): String =
        String.format("%,d", amount).replace(',', '.')

    override fun onCleared() {
        super.onCleared()
        tfliteHelper.close()
    }
}
