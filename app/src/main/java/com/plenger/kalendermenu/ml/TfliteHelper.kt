package com.plenger.kalendermenu.ml

import android.content.Context
import org.json.JSONArray
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TfliteHelper
 * Load: assets/kalendermenu_hpp_model.tflite
 *       assets/kalendermenu_vocab.json
 *
 * Input  : teks bahan baku (Indonesian)
 * Output : prediksi HPP (Rupiah)
 */
@Singleton
class TfliteHelper @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val MODEL_FILE  = "kalendermenu_hpp_model.tflite"
        private const val VOCAB_FILE  = "kalendermenu_vocab.json"
        private const val MAX_SEQ_LEN = 100
        private const val PAD_INDEX   = 0
    }

    // ── Lazy init — tidak pakai by lazy untuk hindari delegate error ──
    private var _interpreter: Interpreter? = null
    private val interpreter: Interpreter
        get() {
            if (_interpreter == null) {
                _interpreter = Interpreter(loadModel())
            }
            return _interpreter!!
        }

    private var _vocab: Map<String, Int>? = null
    private val vocab: Map<String, Int>
        get() {
            if (_vocab == null) {
                _vocab = loadVocab()
            }
            return _vocab!!
        }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────

    /**
     * Prediksi HPP total dari teks bahan.
     * @param text  contoh: "daging sapi 5kg santan 3liter cabai 500gr"
     * @param porsi jumlah porsi
     * @return      total HPP dalam Rupiah
     */
    fun predictHpp(text: String, porsi: Int = 50): Float {
        val input  = tokenize(text)
        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)
        val raw = output[0][0]

        // De-normalisasi: sesuaikan dengan skala training kamu
        // Kalau model output 0-1, multiply ke range Rupiah
        // Kalau model sudah output Rupiah langsung, hapus multiply
        return raw * 50_000f * porsi
    }

    /**
     * Batch prediction untuk AI recommendation.
     */
    fun predictHppBatch(texts: List<String>, porsi: Int = 50): List<Float> =
        texts.map { predictHpp(it, porsi) }

    fun close() {
        _interpreter?.close()
        _interpreter = null
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    private fun loadModel(): MappedByteBuffer {
        val fd      = context.assets.openFd(MODEL_FILE)
        val stream  = FileInputStream(fd.fileDescriptor)
        val channel = stream.channel
        return channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
    }

    private fun loadVocab(): Map<String, Int> {
        val text  = context.assets.open(VOCAB_FILE).bufferedReader().use { it.readText() }
        val arr   = JSONArray(text)
        val map   = mutableMapOf<String, Int>()
        for (i in 0 until arr.length()) {
            map[arr.getString(i)] = i + 1   // 0 = PAD
        }
        return map
    }

    private fun tokenize(text: String): Array<IntArray> {
        val tokens = text
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { vocab[it] ?: PAD_INDEX }
            .take(MAX_SEQ_LEN)

        val padded = IntArray(MAX_SEQ_LEN) { PAD_INDEX }
        tokens.forEachIndexed { i, v -> padded[i] = v }
        return arrayOf(padded)
    }
}
