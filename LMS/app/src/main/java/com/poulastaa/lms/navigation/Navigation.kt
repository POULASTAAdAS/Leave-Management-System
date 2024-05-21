package com.poulastaa.lms.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.poulastaa.lms.presentation.auth.AuthRootScreen
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Home")
            }
        }
    }
}