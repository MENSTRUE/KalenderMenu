package com.plenger.kalendermenu.ml

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RecipeSearch
 * Load: res/raw/database_resep_bersih.json
 *
 * Format JSON yang didukung (flexible):
 * {
 *   "nama menu 1": "bahan1, bahan2, ...",
 *   "nama menu 2": "bahan1, bahan2, ..."
 * }
 * atau format array:
 * [{"title":"...", "ingredients":[...]}, ...]
 */
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
        val teks: String          // raw text → input ke TFLite
    )

    // ── Lazy load — parse sekali, reuse ───────────────────────────
    private val allRecipes: Map<String, String> by lazy { loadJson() }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────

    /**
     * Cari resep berdasarkan nama menu.
     * Return null kalau tidak ketemu.
     */
    fun cariMenu(query: String, porsi: Int = 50): RecipeResult? {
        val q = query.lowercase().trim()

        // 1. Exact match
        val exactKey = allRecipes.keys.firstOrNull {
            it.lowercase() == q
        }
        // 2. Contains match
        val containsKey = allRecipes.keys.firstOrNull {
            it.lowercase().contains(q) || q.contains(it.lowercase())
        }
        // 3. Partial keyword match
        val keywords = q.split(" ").filter { it.length > 2 }
        val partialKey = allRecipes.keys.firstOrNull { key ->
            val kLower = key.lowercase()
            keywords.any { kw -> kLower.contains(kw) }
        }

        val matchedKey = exactKey ?: containsKey ?: partialKey
            ?: return null

        val rawText = allRecipes[matchedKey] ?: return null
        return parseRawToResult(matchedKey, rawText, porsi)
    }

    /**
     * Ambil top N resep untuk AI recommendation batch scoring.
     */
    fun getTopRecipes(n: Int = 30): List<RecipeResult> {
        return allRecipes.entries
            .take(n)
            .map { (name, raw) -> parseRawToResult(name, raw, 50) }
    }

    /**
     * Semua nama menu (untuk autocomplete).
     */
    fun getAllMenuNames(): List<String> = allRecipes.keys.toList()

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    /**
     * Load database_resep_bersih.json dari res/raw/.
     * File format: {"nama resep": "teks bahan baku...", ...}
     */
    private fun loadJson(): Map<String, String> {
        return try {
            val resId = context.resources.getIdentifier(
                "database_resep_bersih", "raw", context.packageName
            )

            val text = if (resId != 0) {
                context.resources.openRawResource(resId)
                    .bufferedReader().use { it.readText() }
            } else {
                // fallback ke assets/
                context.assets.open("database_resep_bersih.json")
                    .bufferedReader().use { it.readText() }
            }

            parseJsonToMap(text)
        } catch (e: Exception) {
            // Kalau file tidak ada, return empty map
            // ViewModel akan fallback ke mock data
            emptyMap()
        }
    }

    /**
     * Parse JSON ke Map<namaMenu, rawBahanText>.
     * Support 2 format:
     * 1. Object: {"rendang sapi": "daging sapi 500gr, santan..."}
     * 2. Array: [{"title": "...", "ingredients": [...]}, ...]
     */
    private fun parseJsonToMap(text: String): Map<String, String> {
        val trimmed = text.trim()
        return when {
            trimmed.startsWith("{") -> parseObjectFormat(trimmed)
            trimmed.startsWith("[") -> parseArrayFormat(trimmed)
            else -> emptyMap()
        }
    }

    /** Format 1: JSON Object — key = nama menu, value = teks bahan */
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

    /** Format 2: JSON Array — [{"title":..., "ingredients":...}] */
    private fun parseArrayFormat(text: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val arr = JSONArray(text)
        for (i in 0 until arr.length()) {
            val obj   = arr.getJSONObject(i)
            val title = obj.optString("title",
                        obj.optString("nama",
                        obj.optString("name", "Menu $i")))
            val ingArr = obj.optJSONArray("ingredients")
                ?: obj.optJSONArray("bahan")
            val rawText = if (ingArr != null) {
                (0 until ingArr.length()).joinToString(", ") { j ->
                    val ing = ingArr.getJSONObject(j)
                    "${ing.optString("name", ing.optString("nama", ""))} " +
                    "${ing.optString("quantity", ing.optString("jumlah", ""))}"
                }
            } else {
                obj.optString("steps", obj.optString("description", ""))
            }
            result[title] = rawText
        }
        return result
    }

    /**
     * Konversi raw text bahan → RecipeResult.
     * Contoh raw: "daging sapi 500gr, santan 200ml, cabai 100gr"
     */
    private fun parseRawToResult(
        namaMenu: String,
        rawText: String,
        porsi: Int
    ): RecipeResult {
        // Split per koma atau bullet
        val parts = rawText
            .split(",", "•", "\n", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(10) // max 10 bahan

        val bahan = parts.map { part ->
            // Coba pisah nama vs angka+satuan
            // Contoh: "daging sapi 500 gr" → nama="Daging Sapi", jumlah="500 gr"
            val qtyRegex = Regex("""(.*?)\s+(\d[\d.,]*\s*(?:kg|gr|g|ml|liter|ltr|sdm|sdt|siung|butir|lembar|ikat|batang|btg|bks|sachet|bungkus|ons|oz|cup|bh|buah|potong|iris|helai|tangkai|ruas|genggam|sendok)[\s\S]*)""",
                RegexOption.IGNORE_CASE)
            val match = qtyRegex.find(part.trim())

            if (match != null) {
                BahanItem(
                    nama   = match.groupValues[1].trim()
                        .replaceFirstChar { it.uppercase() },
                    jumlah = scaleQty(match.groupValues[2].trim(), porsi)
                )
            } else {
                // Tidak bisa parse → tampilkan apa adanya
                BahanItem(
                    nama   = part.replaceFirstChar { it.uppercase() },
                    jumlah = ""
                )
            }
        }

        return RecipeResult(
            namaMenu = namaMenu.split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
            bahan    = bahan,
            teks     = rawText
        )
    }

    /**
     * Scale jumlah bahan sesuai porsi.
     * Asumsi base = 10 porsi (sesuaikan kalau dataset berbeda).
     */
    private fun scaleQty(qty: String, porsi: Int): String {
        val base   = 10
        val factor = porsi.toDouble() / base
        val numRegex = Regex("""([\d.,]+)""")
        val match  = numRegex.find(qty) ?: return qty
        val num    = match.value.replace(",", ".").toDoubleOrNull() ?: return qty
        val scaled = num * factor
        val unit   = qty.substring(match.range.last + 1).trim()
        val fmt    = if (scaled % 1.0 == 0.0) scaled.toInt().toString()
                     else "%.1f".format(scaled)
        return "$fmt $unit".trim()
    }
}
