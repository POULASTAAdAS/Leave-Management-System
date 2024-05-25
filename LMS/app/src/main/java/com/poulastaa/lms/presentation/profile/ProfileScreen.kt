package com.poulastaa.lms.presentation.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.profile.components.ProfileIconItemView
import com.poulastaa.lms.presentation.profile.components.ProfileItemEditableHeader
import com.poulastaa.lms.presentation.profile.components.ProfileItemHeader
import com.poulastaa.lms.presentation.profile.components.ProfileItemView
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.EditIcon
import com.poulastaa.lms.ui.theme.OutlineEmailIcon
import com.poulastaa.lms.ui.theme.OutlineHouseIcon
import com.poulastaa.lms.ui.theme.OutlinePhoneIcon
import com.poulastaa.lms.ui.theme.OutlineQualificationIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun ProfileRootScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigate: (ProfileUiAction.OnNavigate) -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) { event ->
        when (event) {
            is ProfileUiAction.OnNavigate -> navigate(event)

            is ProfileUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LaunchedEffect(
        key1 = true,
        viewModel.state.name.isEmpty()
    ) {
        viewModel.startJob()
    }

    if (!viewModel.state.isInternet) ProfileErr {
        navigateBack()
        viewModel.cancelJob()
    }
    else if (viewModel.state.isMakingApiCall) ProfileLoading()
    else ProfileScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        context = context,
        navigateBack = {
            navigateBack()
            viewModel.cancelJob()
        }
    )

    BackHandler {
        navigateBack()
        viewModel.cancelJob()
    }
}

@Composable
private fun ProfileErr(
    navigateBack: () -> Unit
) {
    ScreenWrapper(
        verticalArrangement = Arrangement.Top
    ) {
        Back {
            navigateBack()
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.error_internet),
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileLoading() {
    ScreenWrapper {
        CircularProgressIndicator(
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(70.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    context: Context,
    onEvent: (ProfileUiEvent) -> Unit,
    navigateBack: () -> Unit
) {
    ScreenWrapper(
        verticalArrangement = Arrangement.Top
    ) {
        Back {
            navigateBack()
        }
        ProfileCard(
            profilePicUrl = state.profilePicUrl,
            gender = state.gender,
            isProfilePicUpdating = state.isProfilePicUpdating,
            name = state.name,
            cookie = state.cookie,
            context = context,
            onClick = onEvent
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            PersonalDetails(
                personalDetails = state.personalDetails,
                onClick = onEvent
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))


            OtherDetails(otherDetails = state.otherDetails)

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

            Address(
                address = state.presentAddress,
                label = stringResource(id = R.string.present_address_header)
            ) {
                onEvent(ProfileUiEvent.OnPresentAddressEditClick)
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))


            Log.d("home Address ui", state.homeAddress.toString())

            Address(
                address = state.homeAddress,
                label = stringResource(id = R.string.home_address_header)
            ) {
                onEvent(ProfileUiEvent.OnHomeAddressEditClick)
            }


            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
        }
    }
}

@Composable
private fun Back(
    navigateBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = navigateBack,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
            Icon(
                imageVector = ArrowBackIcon,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ProfileCard(
    profilePicUrl: String,
    isProfilePicUpdating: Boolean,
    cookie: String,
    gender: String,
    context: Context,
    name: String,
    onClick: (ProfileUiEvent) -> Unit
) {
    context.imageLoader.memoryCache?.clear()

    val photoPicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {

            onClick(
                ProfileUiEvent.OnProfileEditClick(
                    context = context,
                    uri = it
                )
            )
        }



    Card(
        modifier = Modifier
            .padding(MaterialTheme.dimens.large2),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 20.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(profilePicUrl)
                        .addHeader(
                            name = "Cookie",
                            value = cookie
                        )
                        .crossfade(true)
                        .error(if (gender == "M") R.drawable.ic_profile_male else R.drawable.ic_profile_female)
                        .placeholder(if (gender == "M") R.drawable.ic_profile_male else R.drawable.ic_profile_female)
                        .build(),
                    contentDescription = null,
                    clipToBounds = true,
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.large1)
                        .clip(RoundedCornerShape(1000f))
                        .size(200.dp)
                        .align(Alignment.Center)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(1000f)
                        )
                        .background(color = MaterialTheme.colorScheme.primary)
                        .alpha(if (isProfilePicUpdating) .5f else 1f),
                    contentScale = ContentScale.Crop
                )

                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                        .alpha(if (isProfilePicUpdating) 1f else 0f),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 3.dp
                )

                IconButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = MaterialTheme.dimens.large1,
                            bottom = MaterialTheme.dimens.large1
                        )
                        .size(56.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.dimens.small3 + MaterialTheme.dimens.small2),
                        imageVector = EditIcon,
                        contentDescription = null,
                    )
                }
            }

            Text(
                text = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.dimens.medium1),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
        }
    }
}

@Composable
private fun PersonalDetails(
    personalDetails: PersonalDetails,
    onClick: (ProfileUiEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimens.small3)
        ) {
            ProfileItemEditableHeader(label = stringResource(id = R.string.personal_details)) {
                onClick(ProfileUiEvent.DetailsEditClick)
            }

            ProfileIconItemView(
                text = personalDetails.email,
                icon = OutlineEmailIcon
            )

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .padding(
                        start = 60.dp,
                        end = 30.dp
                    )
                    .background(MaterialTheme.colorScheme.background.copy(.1f))
            )

            ProfileIconItemView(
                text = personalDetails.phoneOne,
                icon = OutlinePhoneIcon
            )

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .padding(
                        start = 60.dp,
                        end = 30.dp
                    )
                    .background(MaterialTheme.colorScheme.background.copy(.1f))
            )

            ProfileIconItemView(
                text = personalDetails.phoneTwo.ifEmpty { "-" },
                icon = OutlinePhoneIcon
            )

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .padding(
                        start = 60.dp,
                        end = 30.dp
                    )
                    .background(MaterialTheme.colorScheme.background.copy(.1f))
            )

            ProfileIconItemView(
                text = personalDetails.qualification,
                icon = OutlineQualificationIcon
            )
        }
    }
}

@Composable
private fun OtherDetails(
    otherDetails: OtherDetails
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimens.small3)
        ) {
            ProfileItemHeader(label = stringResource(id = R.string.other_details))

            ProfileItemView(
                label = stringResource(id = R.string.department),
                text = otherDetails.department
            )

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .padding(start = MaterialTheme.dimens.small3, end = 30.dp)
                    .background(MaterialTheme.colorScheme.background.copy(.1f))
            )

            ProfileItemView(
                label = stringResource(id = R.string.experience),
                text = otherDetails.exp
            )

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .padding(start = MaterialTheme.dimens.small3, end = 30.dp)
                    .background(MaterialTheme.colorScheme.background.copy(.1f))
            )

            ProfileItemView(
                label = stringResource(id = R.string.date_of_joining),
                text = otherDetails.joiningDate
            )
        }
    }
}

@Composable
private fun Address(
    address: ProfileUiAddress,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimens.small3)
        ) {
            ProfileItemEditableHeader(label = label) {
                onClick()
            }

            ProfileIconItemView(
                text = listOf(
                    address.houseNumber,
                    address.street,
                    address.zipcode,
                    address.city,
                    address.state,
                    address.country
                ).joinToString(),
                icon = OutlineHouseIcon
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    TestThem {
        ProfileScreen(state = ProfileUiState(
            isProfilePicUpdating = false,
            gender = "M"
        ), context = LocalContext.current, onEvent = {}) {

        }
    }
}