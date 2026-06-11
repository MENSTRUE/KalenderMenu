package com.plenger.kalendermenu.ui.screens.neworder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var customerName: String
        get() = savedStateHandle.get<String>("customerName") ?: ""
        set(value) { savedStateHandle["customerName"] = value }

    var portions: Int
        get() = savedStateHandle.get<Int>("portions") ?: 50
        set(value) { savedStateHandle["portions"] = value }

    var selectedDate: String
        get() = savedStateHandle.get<String>("date") ?: java.time.LocalDate.now().toString()
        set(value) { savedStateHandle["date"] = value }
}