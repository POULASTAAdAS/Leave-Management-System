package com.poulastaa.lms.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.poulastaa.lms.presentation.auth.AuthRootScreen
import com.poulastaa.lms.presentation.home.type.HomeRootScreenType
import com.poulastaa.lms.presentation.store_details.StoreDetailsRootScreen

@Composable
fun Navigation(
    navController: NavHostController,
    startDestination: String = Screens.Auth.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screens.Auth.route) {
            AuthRootScreen {
                navController.popBackStack()
                navController.navigate(it.route)
            }
        }

        composable(route = Screens.StoreDetails.route) {
            StoreDetailsRootScreen {
                navController.popBackStack()
                navController.navigate(Screens.Home.route)
            }
        }


        composable(route = Screens.Home.route) {
            HomeRootScreenType {
                if (it == Screens.Auth) {
                    navController.popBackStack()
                    navController.navigate(it.route)
                } else navController.navigate(it.route)
            }
        }
    }
}