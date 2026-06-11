package com.plenger.kalendermenu.ml

import android.content.Context
import org.json.JSONObject

object PriceHelper {
    fun loadMasterIngredients(context: Context): Map<String, Long> {
        return try {
            val resId = context.resources.getIdentifier("master_harga", "raw", context.packageName)
            val text = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
            val root = JSONObject(text)
            val komoditas = root.getJSONObject("komoditas")
            val map = mutableMapOf<String, Long>()

            komoditas.keys().forEach { kategori ->
                val items = komoditas.getJSONObject(kategori)
                items.keys().forEach { key ->
                    val item = items.getJSONObject(key)
                    if (item.has("nama_tampil")) {
                        val harga = item.optLong("harga_per_kg", item.optLong("harga_per_liter", 0L))
                        map[item.getString("nama_tampil").lowercase()] = harga
                    }
                }
            }
            map
        } catch (e: Exception) { emptyMap() }
    }
}