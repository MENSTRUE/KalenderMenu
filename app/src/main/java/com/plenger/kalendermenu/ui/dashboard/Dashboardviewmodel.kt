package com.plenger.kalendermenu.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderSummary(
    val id: Long,
    val customerName: String,
    val menuName: String,
    val portions: Int,
    val hpp: Long,
    val dateFormatted: String,
    val dayLabel: String,
    val dayNumber: String,
    val status: String
)

data class DashboardUiState(
    val upcomingOrder: OrderSummary? = null,
    val weeklyOrders: List<OrderSummary> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val mockOrders = listOf(
                OrderSummary(
                    id = 1L,
                    customerName = "Ibu Hartini",
                    menuName = "Nasi Gudeg + Ayam Bakar",
                    portions = 50,
                    hpp = 875000,
                    dateFormatted = "Selasa, 24 Juni 2026",
                    dayLabel = "SEL",
                    dayNumber = "24",
                    status = "konfirmasi"
                ),
                OrderSummary(
                    id = 2L,
                    customerName = "Pak Ahmad",
                    menuName = "Nasi Kotak Ayam",
                    portions = 80,
                    hpp = 1200000,
                    dateFormatted = "Rabu, 25 Juni 2026",
                    dayLabel = "RAB",
                    dayNumber = "25",
                    status = "menunggu"
                ),
                OrderSummary(
                    id = 3L,
                    customerName = "Bu Siti",
                    menuName = "Rendang Sapi",
                    portions = 30,
                    hpp = 620000,
                    dateFormatted = "Jumat, 27 Juni 2026",
                    dayLabel = "JUM",
                    dayNumber = "27",
                    status = "konfirmasi"
                )
            )
            _uiState.update {
                it.copy(
                    upcomingOrder = mockOrders.first(),
                    weeklyOrders = mockOrders,
                    isLoading = false
                )
            }
        }
    }
}