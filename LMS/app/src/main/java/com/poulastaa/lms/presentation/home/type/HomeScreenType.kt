package com.poulastaa.lms.presentation.home.type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.navigation.Screens

@Composable
fun HomeRootScreenType(
    viewModel: HomeScreenTypeViewModel = hiltViewModel(),
    navigate: (Screens) -> Unit
) {
    val context = LocalContext.current

//    ObserveAsEvent(flow = viewModel.uiEvent) {
//        when (it) {
//            HomeScreenTypeUiAction.Err -> {
//                navigate(Screens.Auth)
//
//                Toast.makeText(
//                    context,
//                    UiText.StringResource(R.string.error_something_went_wrong).asString(context),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }


    HomeScreenType(
        type = viewModel.userType
    )
}

@Composable
private fun HomeScreenType(
    type: UserType
) {
    when (type) {
        UserType.SACT -> {

        }

        UserType.PERMANENT -> {

        }

        UserType.PRINCIPLE -> {

        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}