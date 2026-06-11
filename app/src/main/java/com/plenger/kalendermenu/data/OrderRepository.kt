package com.plenger.kalendermenu.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class OrderSummary(
    val id: Long,
    val customerName: String,
    val menuName: String,
    val portions: Int,
    val hpp: Long,
    val dateFormatted: String,
    val dayShort: String,
    val dateNumber: String,
    val status: String,
    val ingredients: List<String> = emptyList()
) {
    companion object {
        fun fromJson(jsonStr: String): OrderSummary {
            val obj = JSONObject(jsonStr)
            val ingredientsList = mutableListOf<String>()
            if (obj.has("ingredients")) {
                val arr = obj.getJSONArray("ingredients")
                for (i in 0 until arr.length()) {
                    ingredientsList.add(arr.getString(i))
                }
            }
            return OrderSummary(
                id = obj.getLong("id"),
                customerName = obj.getString("customerName"),
                menuName = obj.getString("menuName"),
                portions = obj.getInt("portions"),
                hpp = obj.getLong("hpp"),
                dateFormatted = obj.getString("dateFormatted"),
                dayShort = obj.getString("dayShort"),
                dateNumber = obj.getString("dateNumber"),
                status = obj.getString("status"),
                ingredients = ingredientsList
            )
        }
    }

    fun toJsonString(): String {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("customerName", customerName)
        obj.put("menuName", menuName)
        obj.put("portions", portions)
        obj.put("hpp", hpp)
        obj.put("dateFormatted", dateFormatted)
        obj.put("dayShort", dayShort)
        obj.put("dateNumber", dateNumber)
        obj.put("status", status)
        val arr = JSONArray()
        ingredients.forEach { arr.put(it) }
        obj.put("ingredients", arr)
        return obj.toString()
    }
}

class OrderRepository private constructor(context: Context) {

    private val file = File(context.filesDir, "orders.json")

    private val _ordersFlow = MutableStateFlow<List<OrderSummary>>(emptyList())
    val ordersFlow: StateFlow<List<OrderSummary>> = _ordersFlow.asStateFlow()

    init {
        loadData()
    }

    companion object {
        @Volatile
        private var INSTANCE: OrderRepository? = null

        fun getInstance(context: Context): OrderRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = OrderRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private fun loadData() {
        if (!file.exists()) {
            _ordersFlow.value = emptyList()
            return
        }

        val jsonStr = file.readText()
        if (jsonStr.isBlank()) {
            _ordersFlow.value = emptyList()
            return
        }

        val jsonArray = JSONArray(jsonStr)
        val orders = mutableListOf<OrderSummary>()
        for (i in 0 until jsonArray.length()) {
            try {
                orders.add(OrderSummary.fromJson(jsonArray.getString(i)))
            } catch (_: Exception) {}
        }
        _ordersFlow.value = orders
    }

    private fun saveToFile(orders: List<OrderSummary>) {
        val jsonArray = JSONArray()
        orders.forEach { jsonArray.put(it.toJsonString()) }
        file.writeText(jsonArray.toString())
    }

    fun saveOrder(order: OrderSummary) {
        val currentOrders = _ordersFlow.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == order.id }

        if (index != -1) {
            currentOrders[index] = order
        } else {
            currentOrders.add(0, order)
        }

        saveToFile(currentOrders)
        _ordersFlow.value = currentOrders
    }

    fun updateOrderStatus(orderId: Long, newStatus: String) {
        val currentOrders = _ordersFlow.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }

        if (index != -1) {
            currentOrders[index] = currentOrders[index].copy(status = newStatus)
            saveToFile(currentOrders)
            _ordersFlow.value = currentOrders
        }
    }

    fun getOrderById(orderId: Long): OrderSummary? {
        return _ordersFlow.value.find { it.id == orderId }
    }
}