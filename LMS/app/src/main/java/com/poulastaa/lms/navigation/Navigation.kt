package com.poulastaa.lms.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.poulastaa.lms.presentation.auth.AuthRootScreen
import com.poulastaa.lms.presentation.home.type.HomeRootScreenType
import com.poulastaa.lms.presentation.profile.ProfileRootScreen
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsEditUiEvent
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsEditViewModel
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsRootScreen
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

        composable(route = Screens.Profile.route) {
            ProfileRootScreen(
                navigate = {
                    val name =
                        it.args[Screens.EditDetails.Args.NAME.title] ?: return@ProfileRootScreen
                    val email =
                        it.args[Screens.EditDetails.Args.EMAIL.title] ?: return@ProfileRootScreen
                    val phoneOne = it.args[Screens.EditDetails.Args.PHONE_ONE.title]
                        ?: return@ProfileRootScreen
                    val phoneTwo =
                        it.args[Screens.EditDetails.Args.PHONE_TWO.title].let { phone ->
                            if (phone.isNullOrEmpty()) "0"
                            else phone
                        }
                    val qualification = it.args[Screens.EditDetails.Args.QUALIFICATION.title]
                        ?: return@ProfileRootScreen

                    when {
                        it.screen == Screens.EditDetails -> {
                            navController.navigate(
                                route = it.screen.route + "$name/$email/$phoneOne/$phoneTwo/$qualification"
                            )
                        }
                    }
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screens.EditDetails.route + Screens.EditDetails.PARAMS,
            arguments = listOf(
                navArgument(Screens.EditDetails.Args.NAME.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditDetails.Args.EMAIL.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditDetails.Args.PHONE_ONE.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditDetails.Args.PHONE_TWO.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditDetails.Args.QUALIFICATION.title) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel: DetailsEditViewModel = hiltViewModel()

            val name =
                it.arguments?.getString(Screens.EditDetails.Args.NAME.title)
            val email =
                it.arguments?.getString(Screens.EditDetails.Args.EMAIL.title)
            val phoneOne = it.arguments?.getString(Screens.EditDetails.Args.PHONE_ONE.title)
            val phoneTwo = it.arguments?.getString(Screens.EditDetails.Args.PHONE_TWO.title) ?: ""
            val qualification =
                it.arguments?.getString(Screens.EditDetails.Args.QUALIFICATION.title)


            if (name == null ||
                email == null ||
                phoneOne == null ||
                qualification == null
            ) {
                viewModel.onEvent(DetailsEditUiEvent.SomethingWentWrong)

                navController.popBackStack()
                return@composable
            }

            DetailsRootScreen(
                name = name,
                email = email,
                phoneOne = phoneOne,
                phoneTwo = phoneTwo,
                qualification = qualification,
                viewModel = viewModel
            ) {
                navController.popBackStack()
            }
        }

        composable(route = Screens.ApplyLeave.route) {

        }

        composable(route = Screens.LeaveHistory.route) {

        }

        composable(route = Screens.LeaveStatus.route) {

        }

        composable(route = Screens.ApproveLeave.route) {

        }

        composable(route = Screens.ViewLeave.route) {

        }

        composable(route = Screens.ViewReport.route) {

        }

        composable(route = Screens.DefineDepartmentInCharge.route) {

        }

        composable(route = Screens.Add.route) {

        }
    }
}