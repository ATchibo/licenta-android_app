package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.ProgressIndicator
import com.tchibo.plantbuddy.ui.components.logspage.LogCard
import com.tchibo.plantbuddy.ui.viewmodels.LogsPageViewModel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogsPage(raspberryId: String) {

    val navigator = LocalNavController.current

    val viewModel = viewModel<LogsPageViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LogsPageViewModel(navigator, raspberryId) as T
            }
        }
    )
    val state = viewModel.state.value

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = viewModel::loadLogs
    )

    Scaffold (
        topBar = { Appbar(screenInfo = state.screenInfo) },
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(paddingValues = paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.logs_page),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 30.dp, 0.dp),
                    fontSize = TEXT_SIZE_UGE,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )

                Text(
                    text = stringResource(id = R.string.logs_page_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 30.dp, 30.dp),
                    fontSize = TEXT_SIZE_SMALL,
                    color = Color.White,
                )

                if (state.isRefreshing) {
                    ProgressIndicator()
                } else {
                    LazyColumn (
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                    ) {
                        items(state.logs.size) { index ->
                            LogCard(log = state.logs[index])
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                Button(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.back_button_text),
                            fontSize = TEXT_SIZE_SMALL,
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}