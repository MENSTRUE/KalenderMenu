package com.plenger.kalendermenu.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object AllOrders : Screen("all_orders")
    data object NewOrder : Screen("new_order")
    data object IngredientPriceList : Screen("ingredient_price_list")
    data object Profile : Screen("profile")

    data object SpecificMenu : Screen("specific_menu") {
        fun createRoute(orderId: Long, portions: Int, date: String, name: String) =
            "specific_menu/$orderId/$portions/$date/$name"
    }

    data object AiRecommendation : Screen("ai_recommendation") {
        fun createRoute(orderId: Long, portions: Int, date: String, name: String) =
            "ai_recommendation/$orderId/$portions/$date/$name"
    }

    data object CalendarReminder : Screen("calendar_reminder") {
        fun createRoute(orderId: Long, menuName: String) = "calendar_reminder/$orderId/$menuName"
    }

    data object SendToSupplier : Screen("send_to_supplier") {
        fun createRoute(orderId: Long) = "send_to_supplier/$orderId"
    }

    data object UpdateIngredientPrice : Screen("update_price") {
        fun createRoute(recipeId: Long) = "update_price/$recipeId"
    }

    data object OrderDetail : Screen("order_detail") {
        fun createRoute(orderId: Long) = "order_detail/$orderId"
    }
}