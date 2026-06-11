package com.plenger.kalendermenu.ui.screens.specificmenu

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plenger.kalendermenu.ml.PriceHelper
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
import org.json.JSONObject
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
class SpecificMenuViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recipeSearch: RecipeSearch,
    private val tfliteHelper: TfliteHelper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpecificMenuUiState(
        searchQuery = savedStateHandle.get<String>("query") ?: ""
    ))
    val uiState: StateFlow<SpecificMenuUiState> = _uiState.asStateFlow()

    val portions: Int = savedStateHandle.get<Int>("portions") ?: 10
    val date: String = savedStateHandle.get<String>("date") ?: ""
    val customerName: String = savedStateHandle.get<String>("customerName") ?: "Pelanggan"

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, errorMessage = null) }
        savedStateHandle["query"] = query
    }

    fun searchMenu() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, result = null) }

            val result = withContext(Dispatchers.IO) {
                val recipeResult = recipeSearch.cariMenu(query, porsi = portions) ?: return@withContext null
                val resId = context.resources.getIdentifier("master_harga", "raw", context.packageName)
                val jsonString = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
                val root = JSONObject(jsonString)
                val konversi = root.getJSONObject("konversi_satuan")
                val masterPrices = PriceHelper.loadMasterIngredients(context)

                var totalHpp = 0L
                recipeResult.bahan.forEach { b ->
                    val nameLower = b.nama.lowercase()
                    val pricePerUnit = masterPrices.entries.firstOrNull { (key, _) ->
                        nameLower.contains(key) || key.contains(nameLower)
                    }?.value ?: 0L

                    val qty = Regex("""(\d+[\d.,]*)""").find(b.jumlah.lowercase())
                        ?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

                    val faktor = when {
                        nameLower.contains("bawang putih") -> konversi.getDouble("siung_bawang_putih_ke_kg")
                        nameLower.contains("bawang merah") -> konversi.getDouble("siung_bawang_merah_ke_kg")
                        nameLower.contains("telur") -> konversi.getDouble("butir_telur_ke_kg")
                        nameLower.contains("beras") || nameLower.contains("nasi") -> 1.0
                        pricePerUnit == 0L -> 0.0
                        else -> 0.05
                    }

                    totalHpp += (pricePerUnit * qty * faktor * portions).toLong()
                }

                val finalHpp = if (totalHpp < (portions * 2000)) (totalHpp * 1.15).toLong() else totalHpp

                MenuSearchResult(
                    menuName = recipeResult.namaMenu,
                    portions = portions,
                    ingredients = recipeResult.bahan.map { IngredientItem(it.nama, it.jumlah) },
                    estimatedHpp = finalHpp
                )
            }
            _uiState.update { it.copy(isSearching = false, result = result) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try { tfliteHelper.close() } catch (_: Exception) {}
    }
}
