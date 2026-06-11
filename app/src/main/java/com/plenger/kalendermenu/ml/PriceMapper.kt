package com.plenger.kalendermenu.ml

import android.content.Context
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceMapper @Inject constructor(private val context: Context) {

    private val masterData: JSONObject by lazy {
        val resId = context.resources.getIdentifier("master_harga", "raw", context.packageName)
        val text = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
        JSONObject(text)
    }

    private val hargaCache = mutableMapOf<String, Pair<Long, String>>()

    fun hitungHargaBahan(namaBahan: String, jumlahTeks: String): Long {
        val keyBahan = resolveKey(namaBahan)
        val (hargaDasar, satuanDasar) = getHargaDasar(keyBahan)
        if (hargaDasar == 0L) return 0L

        val (angka, satuan) = parseJumlah(jumlahTeks)
        if (angka <= 0.0) return 0L

        val kuantitas = konversiKeSatuanDasar(angka, satuan, satuanDasar)
        return (kuantitas * hargaDasar).toLong()
    }

    fun hitungHargaBahanRaw(rawBaris: String): Long {
        val regex = Regex("""^([\d.,/]+)\s*([a-zA-Z]+)\s+(.+)$""", RegexOption.IGNORE_CASE)
        val match = regex.find(rawBaris.trim()) ?: return 0L

        val angka = parseAngka(match.groupValues[1])
        val satuan = match.groupValues[2].lowercase()
        val nama = match.groupValues[3].trim()

        val keyBahan = resolveKey(nama)
        val (harga, satuanDasar) = getHargaDasar(keyBahan)
        if (harga == 0L) return 0L

        val mult = konversiKeSatuanDasar(1.0, satuan, satuanDasar)
        return (angka * mult * harga).toLong()
    }

    private fun parseAngka(s: String): Double {
        return if (s.contains("/")) {
            val p = s.split("/")
            p[0].toDouble() / p[1].toDouble()
        } else {
            s.replace(",", ".").toDoubleOrNull() ?: 0.0
        }
    }

    private fun resolveKey(namaBahan: String): String {
        val alias = masterData.getJSONObject("alias_nama_bahan")
        val namaLower = namaBahan.lowercase().trim()

        if (alias.has(namaLower)) return alias.getString(namaLower)

        val namaKey = namaLower.replace(" ", "_")
        if (alias.has(namaKey)) return alias.getString(namaKey)

        val aliasKeys = alias.keys().asSequence().toList()
        val partialMatch = aliasKeys.firstOrNull { key ->
            namaLower.contains(key) || key.contains(namaLower)
        }
        if (partialMatch != null) return alias.getString(partialMatch)

        return namaKey
    }

    private fun getHargaDasar(keyBahan: String): Pair<Long, String> {
        hargaCache[keyBahan]?.let { return it }
        val komoditas = masterData.getJSONObject("komoditas")
        komoditas.keys().forEach { kategori ->
            val items = komoditas.getJSONObject(kategori)
            if (items.has(keyBahan)) {
                val item = items.getJSONObject(keyBahan)
                val result = when {
                    item.has("harga_per_kg")    -> Pair(item.getLong("harga_per_kg"),    "kg")
                    item.has("harga_per_liter") -> Pair(item.getLong("harga_per_liter"), "liter")
                    else -> Pair(0L, "kg")
                }
                hargaCache[keyBahan] = result
                return result
            }
        }
        return Pair(0L, "kg")
    }

    private data class JumlahParsed(val angka: Double, val satuan: String)

    private fun parseJumlah(teks: String): JumlahParsed {
        val t = teks.lowercase().trim()
        if (t.isEmpty() || t.contains("secukupnya") || t.contains("sesuai selera")) {
            return JumlahParsed(0.0, "")
        }

        val fracMatch = Regex("""(\d+)/(\d+)""").find(t)
        val angka = if (fracMatch != null) {
            val num = fracMatch.groupValues[1].toDouble()
            val den = fracMatch.groupValues[2].toDouble()
            if (den != 0.0) num / den else 0.0
        } else {
            Regex("""([\d.,]+)""").find(t)?.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        }

        val satuan = when {
            t.contains("liter") || t.contains("ltr") -> "liter"
            t.contains("ml")    -> "ml"
            t.contains("kg")    -> "kg"
            t.contains("gram")  -> "gr"
            t.contains(" gr")   -> "gr"
            t.contains("garam") -> "gr"
            t.endsWith("gr")    -> "gr"
            t.contains("ons")   -> "ons"
            t.contains("sdm")   -> "sdm"
            t.contains("sdt")   -> "sdt"
            t.contains("siung") -> "siung"
            t.contains("butir") -> "butir"
            t.contains("buah")  -> "buah"
            t.contains("ekor")  -> "ekor"
            t.contains("lembar")-> "lembar"
            t.contains("ikat")  -> "ikat"
            t.contains("batang")|| t.contains("btg") -> "batang"
            t.contains("sachet")|| t.contains("bks") || t.contains("bungkus") -> "sachet"
            t.contains("cup")   -> "cup"
            t.contains("potong")-> "potong"
            t.contains("ruas")  -> "ruas"
            else -> ""
        }

        return JumlahParsed(angka, satuan)
    }

    private fun konversiKeSatuanDasar(angka: Double, satuan: String, satuanDasar: String): Double {
        return when (satuan) {
            "kg"    -> angka
            "gr", "g", "gram" -> angka * 0.001
            "ons"   -> angka * 0.1
            "oz"    -> angka * 0.02835
            "liter", "ltr" -> angka
            "ml"    -> angka * 0.001
            "cup"   -> angka * 0.24
            "sdm"   -> angka * 0.015
            "sdt"   -> angka * 0.005
            "siung" -> angka * 0.005
            "butir" -> angka * 0.06
            "ekor"  -> angka * 1.0
            "buah"  -> angka * 0.1
            "potong"-> angka * 0.1
            "lembar"-> angka * 0.005
            "batang"-> angka * 0.02
            "ruas"  -> angka * 0.01
            "ikat"  -> angka * 0.05
            "sachet"-> angka * 0.005
            ""      -> 0.0
            else    -> angka * 0.001
        }
    }
}