package com.plenger.kalendermenu.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object AllOrders : Screen("all_orders")
    data object NewOrder : Screen("new_order")
    data object SpecificMenu : Screen("specific_menu/{orderId}") {
        fun createRoute(orderId: Long) = "specific_menu/$orderId"
    }
    data object AiRecommendation : Screen("ai_recommendation/{orderId}") {
        fun createRoute(orderId: Long) = "ai_recommendation/$orderId"
    }
    data object CalendarReminder : Screen("calendar_reminder/{orderId}/{menuName}") {
        fun createRoute(orderId: Long, menuName: String) = "calendar_reminder/$orderId/$menuName"
    }
    data object SendToSupplier : Screen("send_to_supplier/{orderId}") {
        fun createRoute(orderId: Long) = "send_to_supplier/$orderId"
    }
    data object UpdateIngredientPrice : Screen("update_price/{recipeId}") {
        fun createRoute(recipeId: Long) = "update_price/$recipeId"
    }
    data object IngredientPriceList : Screen("ingredient_price_list")
    data object Profile : Screen("profile")
    data object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Long) = "order_detail/$orderId"
    }
}