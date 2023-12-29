package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.domain.UserData
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.homepage.HomePageActionButton
import com.tchibo.plantbuddy.ui.components.homepage.RaspberryShortcutCard
import com.tchibo.plantbuddy.ui.theme.translucent_bg_tint
import com.tchibo.plantbuddy.ui.viewmodels.HomePageViewModel
import com.tchibo.plantbuddy.utils.TEXT_SIZE_BIG
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePage(
    userData: UserData
) {

    val navigator = LocalNavController.current

    val viewModel = viewModel<HomePageViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomePageViewModel(navigator) as T
            }
        }
    )
    val state = viewModel.state.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = viewModel::reloadRaspberryDtoList
    )

    Scaffold(
        topBar = { Appbar(screenInfo = state.screenInfo) },
        floatingActionButton = { HomePageActionButton { viewModel.onAddClick() } },
    ) { it ->
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState),
        ) {
            Image(
                painterResource(R.drawable.home_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillHeight,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = translucent_bg_tint)
                    .padding(it)
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Text(
                    text = stringResource(
                        id = R.string.main_screen_title,
                        userData.username // temp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    fontSize = TEXT_SIZE_BIG,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.main_screen_subtitle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 0.dp),
                    fontSize = TEXT_SIZE_NORMAL,
                    color = Color.White,
                )

                if (state.isRefreshing) {
                    Text(
                        text = stringResource(id = R.string.loading),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 0.dp),
                        fontSize = TEXT_SIZE_NORMAL,
                        color = Color.White,
                    )
                } else {
                    if (state.raspberryDtoList.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_devices_found),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 0.dp),
                            fontSize = TEXT_SIZE_NORMAL,
                            color = Color.White,
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            content = {
                                items(state.raspberryDtoList.size) { index ->
                                    RaspberryShortcutCard(state.raspberryDtoList[index])
                                }
                            }
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
