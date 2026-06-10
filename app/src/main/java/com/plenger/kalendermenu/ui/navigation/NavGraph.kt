package com.plenger.kalendermenu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.plenger.kalendermenu.ui.screens.aiRecommendation.AiRecommendationScreen
import com.plenger.kalendermenu.ui.screens.calendar.CalendarReminderScreen
import com.plenger.kalendermenu.ui.screens.dashboard.DashboardScreen
import com.plenger.kalendermenu.ui.screens.ingredientprice.IngredientPriceListScreen
import com.plenger.kalendermenu.ui.screens.ingredientprice.UpdateIngredientPriceScreen
import com.plenger.kalendermenu.ui.screens.login.LoginScreen
import com.plenger.kalendermenu.ui.screens.neworder.NewOrderScreen
import com.plenger.kalendermenu.ui.screens.orders.AllOrdersScreen
import com.plenger.kalendermenu.ui.screens.orders.OrderDetailScreen
import com.plenger.kalendermenu.ui.screens.profile.ProfileScreen
import com.plenger.kalendermenu.ui.screens.specificmenu.SpecificMenuScreen
import com.plenger.kalendermenu.ui.screens.splash.SplashScreen
import com.plenger.kalendermenu.ui.screens.supplier.SendToSupplierScreen

@Composable
fun KalenderMenuNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route   // ← mulai dari Splash
    ) {

        // ── Entry ─────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // ── Main ──────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Screen.AllOrders.route) {
            AllOrdersScreen(navController = navController)
        }

        composable(Screen.NewOrder.route) {
            NewOrderScreen(navController = navController)
        }

        composable(
            route = Screen.SpecificMenu.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            val orderId = backStack.arguments?.getLong("orderId") ?: 0L
            SpecificMenuScreen(navController = navController, orderId = orderId)
        }

        composable(
            route = Screen.AiRecommendation.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            val orderId = backStack.arguments?.getLong("orderId") ?: 0L
            AiRecommendationScreen(navController = navController, orderId = orderId)
        }

        composable(
            route = Screen.CalendarReminder.route,
            arguments = listOf(
                navArgument("orderId")  { type = NavType.LongType },
                navArgument("menuName") { type = NavType.StringType }
            )
        ) { backStack ->
            val orderId  = backStack.arguments?.getLong("orderId") ?: 0L
            val menuName = backStack.arguments?.getString("menuName") ?: ""
            CalendarReminderScreen(navController = navController, orderId = orderId, menuName = menuName)
        }

        composable(
            route = Screen.SendToSupplier.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            val orderId = backStack.arguments?.getLong("orderId") ?: 0L
            SendToSupplierScreen(navController = navController, orderId = orderId)
        }

        composable(
            route = Screen.UpdateIngredientPrice.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStack ->
            val recipeId = backStack.arguments?.getLong("recipeId") ?: 0L
            UpdateIngredientPriceScreen(navController = navController, recipeId = recipeId)
        }

        composable(Screen.IngredientPriceList.route) {
            IngredientPriceListScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            val orderId = backStack.arguments?.getLong("orderId") ?: 0L
            OrderDetailScreen(navController = navController, orderId = orderId)
        }
    }
}
