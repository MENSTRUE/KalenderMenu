package com.plenger.kalendermenu.ml

// ═══════════════════════════════════════════════════════════════════
// CONTOH PENGGUNAAN DI VIEWMODEL
// Copy bagian yang kamu butuhkan ke ViewModel yang relevan
// ═══════════════════════════════════════════════════════════════════

/*
─────────────────────────────────────────────────────────────────────
1. MenuSpesifikViewModel  (Jalur A — user ketik nama menu)
─────────────────────────────────────────────────────────────────────
*/
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// ── State class ───────────────────────────────────────────────────
data class MenuSpesifikUiState(
    val isLoading: Boolean = false,
    val namaMenu: String = "",
    val bahan: List<RecipeSearch.BahanItem> = emptyList(),
    val estimasiHpp: Long = 0L,       // total Rupiah
    val hppPerPorsi: Long = 0L,
    val error: String? = null
)

// ── ViewModel ─────────────────────────────────────────────────────
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
                    // 1. Search recipe in JSON database
                    val result = recipeSearch.cariMenu(namaMenu, porsi)

                    if (result == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Menu '$namaMenu' tidak ditemukan di database"
                        )
                        return@withContext
                    }

                    // 2. Run TFLite prediction on ingredient text
                    val totalHpp    = tfliteHelper.predictHpp(result.teks, porsi).toLong()
                    val hppPerPorsi = if (porsi > 0) totalHpp / porsi else 0L

                    _uiState.value = _uiState.value.copy(
                        isLoading   = false,
                        namaMenu    = result.namaMenu,
                        bahan       = result.bahan,
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

/*
─────────────────────────────────────────────────────────────────────
2. RekomendasiAiViewModel  (Jalur B — user input budget)
─────────────────────────────────────────────────────────────────────
*/

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
                    // 1. Get top 20 recipes from database
                    val candidates = recipeSearch.getTopRecipes(20)

                    // 2. Predict HPP for each using TFLite
                    val predictions = candidates.map { recipe ->
                        val hpp = tfliteHelper.predictHpp(recipe.teks, porsi).toLong()
                        Pair(recipe, hpp)
                    }

                    // 3. Filter by budget, sort by HPP ascending
                    val filtered = predictions
                        .filter { (_, hpp) -> hpp <= budget }
                        .sortedBy { (_, hpp) -> hpp }
                        .take(3)

                    // 4. Build UI state
                    val rekomendasi = filtered.mapIndexed { idx, (recipe, totalHpp) ->
                        val hppPerPorsi = if (porsi > 0) totalHpp / porsi else 0L
                        // Extract ingredient names as tags (first 3)
                        val tags = recipe.bahan.take(3).map { it.nama }

                        RekomendasiItem(
                            rank        = idx + 1,
                            namaMenu    = recipe.namaMenu,
                            hppPerPorsi = hppPerPorsi,
                            totalHpp    = totalHpp,
                            tags        = tags,
                            isTerbaik   = idx == 0
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading       = false,
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

/*
─────────────────────────────────────────────────────────────────────
3. Cara collect di Fragment (Jetpack Compose style or View style)
─────────────────────────────────────────────────────────────────────

// Di MenuSpesifikFragment.kt:

@AndroidEntryPoint
class MenuSpesifikFragment : Fragment() {

    private val viewModel: MenuSpesifikViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Trigger search
        binding.btnCariMenu.setOnClickListener {
            val nama  = binding.etNamaMenu.text.toString()
            val porsi = binding.etPorsi.text.toString().toIntOrNull() ?: 50
            viewModel.cariMenu(nama, porsi)
        }

        // Observe state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.tvTotalHpp.text = "Rp ${state.estimasiHpp.formatRupiah()}"
                bahanAdapter.submitList(state.bahan)
                state.error?.let { showSnackbar(it) }
            }
        }
    }
}
─────────────────────────────────────────────────────────────────────
*/
