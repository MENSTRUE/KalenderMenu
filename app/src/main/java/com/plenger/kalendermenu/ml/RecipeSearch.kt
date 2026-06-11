package com.plenger.kalendermenu.ml

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeSearch @Inject constructor(
    private val context: Context
) {

    data class BahanItem(
        val nama: String,
        val jumlah: String
    )

    data class RecipeResult(
        val namaMenu: String,
        val bahan: List<BahanItem>,
        val teks: String
    )

    private val allRecipes: Map<String, String> by lazy { loadJson() }

    fun cariMenu(query: String, porsi: Int = 50): RecipeResult? {
        val q = query.lowercase().trim()

        val exactKey = allRecipes.keys.firstOrNull { it.lowercase() == q }
        val containsKey = allRecipes.keys.firstOrNull { it.lowercase().contains(q) || q.contains(it.lowercase()) }
        val keywords = q.split(" ").filter { it.length > 2 }
        val partialKey = allRecipes.keys.firstOrNull { key ->
            val kLower = key.lowercase()
            keywords.any { kw -> kLower.contains(kw) }
        }

        val matchedKey = exactKey ?: containsKey ?: partialKey ?: return null
        val rawText = allRecipes[matchedKey] ?: return null
        return parseRawToResult(matchedKey, rawText, porsi)
    }

    fun getRecipesByTheme(tema: String, targetPorsi: Int): List<RecipeResult> {
        val filteredEntries = if (tema.equals("Bebas", ignoreCase = true)) {
            allRecipes.entries.toList()
        } else {
            allRecipes.entries.filter { it.key.contains(tema, ignoreCase = true) }
        }

        return filteredEntries
            .take(100)
            .map { (name, raw) -> parseRawToResult(name, raw, targetPorsi) }
    }

    fun getAllMenuNames(): List<String> = allRecipes.keys.toList()

    private fun loadJson(): Map<String, String> {
        return try {
            val resId = context.resources.getIdentifier("database_resep_bersih", "raw", context.packageName)
            val text = if (resId != 0) {
                context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
            } else {
                context.assets.open("database_resep_bersih.json").bufferedReader().use { it.readText() }
            }
            parseJsonToMap(text)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun parseJsonToMap(text: String): Map<String, String> {
        val trimmed = text.trim()
        return when {
            trimmed.startsWith("{") -> parseObjectFormat(trimmed)
            trimmed.startsWith("[") -> parseArrayFormat(trimmed)
            else -> emptyMap()
        }
    }

    private fun parseObjectFormat(text: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val obj = JSONObject(text)
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = obj.optString(key, "")
        }
        return result
    }

    private fun parseArrayFormat(text: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val arr = JSONArray(text)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val title = obj.optString("title", obj.optString("nama", obj.optString("name", "Menu $i")))
            val ingArr = obj.optJSONArray("ingredients") ?: obj.optJSONArray("bahan")
            val rawText = if (ingArr != null) {
                (0 until ingArr.length()).joinToString(", ") { j ->
                    val ing = ingArr.getJSONObject(j)
                    "${ing.optString("name", ing.optString("nama", ""))} ${ing.optString("quantity", ing.optString("jumlah", ""))}"
                }
            } else {
                obj.optString("steps", obj.optString("description", ""))
            }
            result[title] = rawText
        }
        return result
    }

    private fun parseRawToResult(namaMenu: String, rawText: String, porsi: Int): RecipeResult {
        val parts = rawText.split(",", "•", "\n", ";").map { it.trim() }.filter { it.isNotBlank() }.take(10)

        val satuanRegex = "(?:kg|gr|g|ml|liter|ltr|sdm|sdt|siung|butir|lembar|ikat|batang|btg|bks|sachet|bungkus|ons|oz|cup|bh|buah|potong|iris|helai|tangkai|ruas|genggam|sendok)"

        val bahan = parts.map { part ->
            val qtyFirstRegex = Regex("""^([\d.,/]+\s*$satuanRegex)\s+(.*)$""", RegexOption.IGNORE_CASE)
            val matchQtyFirst = qtyFirstRegex.find(part.trim())

            val nameFirstRegex = Regex("""(.*?)\s+([\d.,/]+\s*$satuanRegex[\s\S]*)""", RegexOption.IGNORE_CASE)
            val matchNameFirst = nameFirstRegex.find(part.trim())

            when {
                matchQtyFirst != null -> {
                    BahanItem(
                        nama = matchQtyFirst.groupValues[2].trim().replaceFirstChar { it.uppercase() },
                        jumlah = scaleQty(matchQtyFirst.groupValues[1].trim(), porsi)
                    )
                }
                matchNameFirst != null -> {
                    BahanItem(
                        nama = matchNameFirst.groupValues[1].trim().replaceFirstChar { it.uppercase() },
                        jumlah = scaleQty(matchNameFirst.groupValues[2].trim(), porsi)
                    )
                }
                else -> {
                    BahanItem(
                        nama = part.replaceFirstChar { it.uppercase() },
                        jumlah = ""
                    )
                }
            }
        }

        return RecipeResult(
            namaMenu = namaMenu.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
            bahan = bahan,
            teks = rawText
        )
    }

    private fun scaleQty(qty: String, porsi: Int): String {
        val base = 4
        val factor = porsi.toDouble() / base
        val numRegex = Regex("""([\d.,/]+)""")
        val match = numRegex.find(qty) ?: return qty

        val numStr = match.value
        val num = if (numStr.contains("/")) {
            val fractionParts = numStr.split("/")
            if (fractionParts.size == 2 && fractionParts[1].toDoubleOrNull() != 0.0) {
                (fractionParts[0].toDoubleOrNull() ?: 0.0) / (fractionParts[1].toDoubleOrNull() ?: 1.0)
            } else 0.0
        } else {
            numStr.replace(",", ".").toDoubleOrNull() ?: return qty
        }

        val scaled = num * factor
        val unit = qty.substring(match.range.last + 1).trim()
        val fmt = if (scaled % 1.0 == 0.0) scaled.toInt().toString() else "%.1f".format(scaled)
        return "$fmt $unit".trim()
    }
}