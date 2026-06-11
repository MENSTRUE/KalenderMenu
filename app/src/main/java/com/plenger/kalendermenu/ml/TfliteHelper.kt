package com.plenger.kalendermenu.ml

import android.content.Context
import org.json.JSONArray
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TfliteHelper @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val MODEL_FILE = "kalendermenu_hpp_model.tflite"
        private const val VOCAB_FILE = "kalendermenu_vocab.json"
        private const val MAX_SEQ_LEN = 100
        private const val PAD_INDEX = 0
    }

    private var _interpreter: Interpreter? = null
    private val interpreter: Interpreter
        get() {
            if (_interpreter == null) {
                _interpreter = Interpreter(loadModel())
                try {
                    _interpreter!!.resizeInput(0, intArrayOf(1, MAX_SEQ_LEN))
                    _interpreter!!.allocateTensors()
                } catch (_: Exception) {}
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

    fun predictHpp(text: String, porsi: Int = 50): Float {
        val input = tokenize(text)
        var hppBase = 0f

        try {
            val output = Array(1) { FloatArray(1) }
            interpreter.run(input, output)
            hppBase = output[0][0]
        } catch (e1: IllegalArgumentException) {
            try {
                val output1D = FloatArray(1)
                interpreter.run(input, output1D)
                hppBase = output1D[0]
            } catch (e2: Exception) {
                hppBase = 0f
            }
        } catch (e: Exception) {
            hppBase = 0f
        }

        return (hppBase / 4f) * porsi.toFloat()
    }

    fun predictHppBatch(texts: List<String>, porsi: Int = 50): List<Float> =
        texts.map { predictHpp(it, porsi) }

    fun close() {
        _interpreter?.close()
        _interpreter = null
    }

    private fun loadModel(): MappedByteBuffer {
        val fd = context.assets.openFd(MODEL_FILE)
        val stream = FileInputStream(fd.fileDescriptor)
        val channel = stream.channel
        return channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
    }

    private fun loadVocab(): Map<String, Int> {
        val text = context.assets.open(VOCAB_FILE).bufferedReader().use { it.readText() }
        val arr = JSONArray(text)
        val map = mutableMapOf<String, Int>()
        for (i in 0 until arr.length()) {
            map[arr.getString(i)] = i + 1
        }
        return map
    }

    private fun tokenize(text: String): Array<FloatArray> {
        val tokens = text
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { (vocab[it] ?: PAD_INDEX).toFloat() }
            .take(MAX_SEQ_LEN)

        val padded = FloatArray(MAX_SEQ_LEN) { PAD_INDEX.toFloat() }
        tokens.forEachIndexed { i, v -> padded[i] = v }
        return arrayOf(padded)
    }
}