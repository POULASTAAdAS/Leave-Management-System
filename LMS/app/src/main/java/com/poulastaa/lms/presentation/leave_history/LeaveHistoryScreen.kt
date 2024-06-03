package com.poulastaa.lms.presentation.leave_history

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.leave_history.components.LeaveHistoryHeaderCard
import com.poulastaa.lms.presentation.leave_history.components.LeaveHistoryItemCard
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun LeaveHistoryRootScreen(
    viewModel: LeaveHistoryViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.loadLeave()
    }

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is LeaveHistoryUiAction.OnErr -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LeaveHistoryScreen(
        state = viewModel.state,
        leave = viewModel.leave.collectAsLazyPagingItems(),
        navigateBack = navigateBack
    )
}

@Composable
private fun LeaveHistoryScreen(
    state: LeaveHistoryUiState,
    leave: LazyPagingItems<LeaveHistoryInfo>,
    navigateBack: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                )
                .fillMaxSize()
                .padding(it)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = MaterialTheme.dimens.medium1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = navigateBack, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = ArrowBackIcon,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

                Text(
                    text = stringResource(id = R.string.leave_history_heading),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            }

            Spacer(modifier = Modifier.heightIn(MaterialTheme.dimens.medium1))
            Spacer(modifier = Modifier.heightIn(MaterialTheme.dimens.large1))


            if (leave.itemCount > 0)
                LazyColumn(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    item {
                        LeaveHistoryHeaderCard(
                            header = state.header
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium3))
                    }

                    items(leave.itemCount) { index ->
                        leave[index]?.let { item ->
                            LeaveHistoryItemCard(leaveInfo = item)
                        }
                    }
                }
        }
    }
}