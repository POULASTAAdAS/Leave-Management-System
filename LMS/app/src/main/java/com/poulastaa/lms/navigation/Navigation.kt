package com.poulastaa.lms.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.poulastaa.lms.presentation.auth.AuthRootScreen
import com.poulastaa.lms.presentation.home.type.HomeRootScreenType
import com.poulastaa.lms.presentation.leave_apply.ApplyLeaveRootScreen
import com.poulastaa.lms.presentation.leave_approval.ApproveLeaveRootScreen
import com.poulastaa.lms.presentation.leave_history.LeaveHistoryRootScreen
import com.poulastaa.lms.presentation.leave_status.LeaveStatusRootScreen
import com.poulastaa.lms.presentation.leave_view.LeaveViewRootScreen
import com.poulastaa.lms.presentation.profile.ProfileRootScreen
import com.poulastaa.lms.presentation.profile.edit.address_edit.AddressEditRootScreen
import com.poulastaa.lms.presentation.profile.edit.address_edit.AddressEditUiEvent
import com.poulastaa.lms.presentation.profile.edit.address_edit.AddressEditViewModel
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsEditUiEvent
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsEditViewModel
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsRootScreen
import com.poulastaa.lms.presentation.profile.edit.details_edit.head.HeadDetailsEditRootScreen
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
                    when (it.screen) {
                        Screens.EditDetails -> {
                            val name =
                                it.args[Screens.EditDetails.Args.NAME.title]
                                    ?: return@ProfileRootScreen
                            val email =
                                it.args[Screens.EditDetails.Args.EMAIL.title]
                                    ?: return@ProfileRootScreen
                            val phoneOne = it.args[Screens.EditDetails.Args.PHONE_ONE.title]
                                ?: return@ProfileRootScreen
                            val phoneTwo =
                                it.args[Screens.EditDetails.Args.PHONE_TWO.title].let { phone ->
                                    if (phone.isNullOrEmpty()) "0"
                                    else phone
                                }
                            val qualification =
                                it.args[Screens.EditDetails.Args.QUALIFICATION.title]
                                    ?: return@ProfileRootScreen

                            val route =
                                Screens.EditDetails.route + "$name/$email/$phoneOne/$phoneTwo/$qualification"

                            navController.navigate(
                                route = route
                            )
                        }

                        Screens.EditAddress -> {
                            val city =
                                it.args[Screens.EditAddress.Args.CITY.title]
                                    ?: return@ProfileRootScreen
                            val type =
                                it.args[Screens.EditAddress.Args.TYPE.title]
                                    ?: return@ProfileRootScreen
                            val house = it.args[Screens.EditAddress.Args.HOUSE_NUM.title]
                                ?: return@ProfileRootScreen
                            val zip =
                                it.args[Screens.EditAddress.Args.ZIP.title].let { phone ->
                                    if (phone.isNullOrEmpty()) "0"
                                    else phone
                                }
                            val street =
                                it.args[Screens.EditAddress.Args.STREET.title]
                                    ?: return@ProfileRootScreen

                            val state =
                                it.args[Screens.EditAddress.Args.STATE.title]
                                    ?: return@ProfileRootScreen


                            val route =
                                Screens.EditAddress.route + "$type/$house/$street/$city/$zip/$state"

                            navController.navigate(
                                route = route
                            )
                        }

                        Screens.EditHeadDetails -> {
                            navController.navigate(
                                route = it.screen.route
                            )
                        }

                        else -> Unit
                    }
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screens.EditHeadDetails.route) {
            HeadDetailsEditRootScreen {
                navController.popBackStack()
            }
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

        composable(
            route = Screens.EditAddress.route + Screens.EditAddress.PARAMS,
            arguments = listOf(
                navArgument(Screens.EditAddress.Args.HOUSE_NUM.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditAddress.Args.STREET.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditAddress.Args.CITY.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditAddress.Args.ZIP.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditAddress.Args.TYPE.title) {
                    type = NavType.StringType
                },
                navArgument(Screens.EditAddress.Args.STATE.title) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel = hiltViewModel<AddressEditViewModel>()

            val type =
                it.arguments?.getString(Screens.EditAddress.Args.TYPE.title)
            val house =
                it.arguments?.getString(Screens.EditAddress.Args.HOUSE_NUM.title)
            val street = it.arguments?.getString(Screens.EditAddress.Args.STREET.title)
            val city = it.arguments?.getString(Screens.EditAddress.Args.CITY.title)
            val zip =
                it.arguments?.getString(Screens.EditAddress.Args.ZIP.title)
            val state =
                it.arguments?.getString(Screens.EditAddress.Args.STATE.title)


            if (type == null ||
                house == null ||
                street == null ||
                city == null ||
                zip == null ||
                state == null
            ) {
                viewModel.onEvent(AddressEditUiEvent.SomethingWentWrong)

                navController.popBackStack()
                return@composable
            }

            LaunchedEffect(key1 = house) {
                viewModel.populate(
                    type = type,
                    houseNo = house,
                    street = street,
                    city = city,
                    zipCode = zip,
                    state = state
                )
            }


            AddressEditRootScreen(
                viewModel = viewModel,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screens.ApplyLeave.route) {
            ApplyLeaveRootScreen {
                navController.popBackStack()
            }
        }

        composable(route = Screens.LeaveHistory.route) {
            LeaveStatusRootScreen {
                navController.popBackStack()
            }
        }

        composable(route = Screens.LeaveStatus.route) {
            LeaveHistoryRootScreen {
                navController.popBackStack()
            }
        }

        composable(route = Screens.ApproveLeave.route) {
            ApproveLeaveRootScreen {
                navController.popBackStack()
            }
        }

        composable(route = Screens.ViewLeave.route) {
            LeaveViewRootScreen {
                navController.popBackStack()
            }
        }

        composable(route = Screens.ViewReport.route) {

        }

        composable(route = Screens.DefineDepartmentInCharge.route) {

        }

        composable(route = Screens.Add.route) {

        }
    }
}