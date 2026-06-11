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
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.AllOrders.route) { AllOrdersScreen(navController) }
        composable(Screen.NewOrder.route) { NewOrderScreen(navController) }

        composable(
            route = Screen.SpecificMenu.route + "/{orderId}/{portions}/{date}/{customerName}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.LongType },
                navArgument("portions") { type = NavType.IntType },
                navArgument("date") { type = NavType.StringType },
                navArgument("customerName") { type = NavType.StringType }
            )
        ) { backStack ->
            SpecificMenuScreen(navController, backStack.arguments?.getLong("orderId") ?: 0L)
        }

        composable(
            route = Screen.AiRecommendation.route + "/{orderId}/{portions}/{date}/{customerName}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.LongType },
                navArgument("portions") { type = NavType.IntType },
                navArgument("date") { type = NavType.StringType },
                navArgument("customerName") { type = NavType.StringType }
            )
        ) { backStack ->
            AiRecommendationScreen(
                navController = navController,
                orderId = backStack.arguments?.getLong("orderId") ?: 0L
            )
        }

        composable(
            route = Screen.CalendarReminder.route + "/{orderId}/{menuName}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.LongType },
                navArgument("menuName") { type = NavType.StringType }
            )
        ) { backStack ->
            CalendarReminderScreen(
                navController = navController,
                orderId = backStack.arguments?.getLong("orderId") ?: 0L,
                menuName = backStack.arguments?.getString("menuName") ?: ""
            )
        }

        composable(
            route = Screen.SendToSupplier.route + "/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            SendToSupplierScreen(navController, backStack.arguments?.getLong("orderId") ?: 0L)
        }

        composable(
            route = Screen.UpdateIngredientPrice.route + "/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStack ->
            UpdateIngredientPriceScreen(navController, backStack.arguments?.getLong("recipeId") ?: 0L)
        }

        composable(Screen.IngredientPriceList.route) { IngredientPriceListScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }

        composable(
            route = Screen.OrderDetail.route + "/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStack ->
            OrderDetailScreen(navController, backStack.arguments?.getLong("orderId") ?: 0L)
        }

    }
}