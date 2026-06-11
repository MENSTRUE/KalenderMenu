package com.plenger.kalendermenu.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plenger.kalendermenu.data.OrderRepository
import com.plenger.kalendermenu.data.OrderSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val recentOrders: List<OrderSummary> = emptyList(),
    val isFilterWeekly: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val repository = OrderRepository.getInstance(context)

    init {
        viewModelScope.launch {
            repository.ordersFlow.collect { orders ->
                refreshOrders(orders)
            }
        }
    }

    fun toggleFilter() {
        val currentFilter = _uiState.value.isFilterWeekly
        _uiState.update { it.copy(isFilterWeekly = !currentFilter) }
        refreshOrders(repository.ordersFlow.value)
    }

    private fun refreshOrders(allOrders: List<OrderSummary>) {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID"))

        val sortedOrders = allOrders.filter {
            it.status.lowercase() != "selesai" && it.status.lowercase() != "batal"
        }.sortedBy {
            try {
                LocalDate.parse(it.dateFormatted, formatter)
            } catch (_: Exception) {
                LocalDate.MAX
            }
        }

        val filtered = if (_uiState.value.isFilterWeekly) {
            sortedOrders.take(3)
        } else {
            sortedOrders
        }
        _uiState.update { it.copy(recentOrders = filtered) }
    }
}