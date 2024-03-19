package com.tchibo.plantbuddy.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tchibo.plantbuddy.LocalNavController
import com.tchibo.plantbuddy.R
import com.tchibo.plantbuddy.ui.components.Appbar
import com.tchibo.plantbuddy.ui.components.ProgressIndicator
import com.tchibo.plantbuddy.ui.components.raspberrysettingspage.NotifiableMessageComponent
import com.tchibo.plantbuddy.ui.components.raspberrysettingspage.PropertyEditingComponent
import com.tchibo.plantbuddy.ui.viewmodels.RaspberrySettingsViewModel
import com.tchibo.plantbuddy.ui.viewmodels.RaspberrySettingsViewModelFactory
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_SMALL
import com.tchibo.plantbuddy.utils.TEXT_SIZE_UGE

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RaspberrySettingsPage(raspberryId: String) {

    val navigator = LocalNavController.current
    val viewModel = viewModel<RaspberrySettingsViewModel>(
        factory = RaspberrySettingsViewModelFactory(navigator, raspberryId)
    )
    val state = viewModel.state.value

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.loading,
        onRefresh = viewModel::loadRaspberryInfo
    )

    Scaffold (
        topBar = { Appbar(screenInfo = state.screenInfo) }
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
                    text = stringResource(id = R.string.raspberry_settings),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 30.dp, 0.dp),
                    fontSize = TEXT_SIZE_UGE,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )

                Text(
                    text = stringResource(id = R.string.raspberry_settings_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp, 10.dp, 30.dp, 30.dp),
                    fontSize = TEXT_SIZE_SMALL,
                    color = Color.White,
                )

                if (state.loading) {
                    ProgressIndicator()
                } else {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .verticalScroll(rememberScrollState())
                    ) {

                        PropertyEditingComponent(
                            labelText = stringResource(id = R.string.raspberry_name),
                            textFieldValue = state.raspberryName,
                            editingValue = state.editingName,
                            onEditClick = viewModel::onNameEditClick,
                            onCancelEditClick = viewModel::onNameCancelEditClick,
                            onConfirmEditClick = viewModel::onNameConfirmEditClick,
                            confirmIcon = state.confirmIcon,
                            cancelIcon = state.cancelIcon,
                            editIcon = state.editIcon,
                        )

                        PropertyEditingComponent(
                            labelText = stringResource(id = R.string.raspberry_description),
                            textFieldValue = state.raspberryDescription,
                            editingValue = state.editingDescription,
                            onEditClick = viewModel::onDescriptionEditClick,
                            onCancelEditClick = viewModel::onDescriptionCancelEditClick,
                            onConfirmEditClick = viewModel::onDescriptionConfirmEditClick,
                            confirmIcon = state.confirmIcon,
                            cancelIcon = state.cancelIcon,
                            editIcon = state.editIcon,
                        )

                        PropertyEditingComponent(
                            labelText = stringResource(id = R.string.raspberry_location),
                            textFieldValue = state.raspberryLocation,
                            editingValue = state.editingLocation,
                            onEditClick = viewModel::onLocationEditClick,
                            onCancelEditClick = viewModel::onLocationCancelEditClick,
                            onConfirmEditClick = viewModel::onLocationConfirmEditClick,
                            confirmIcon = state.confirmIcon,
                            cancelIcon = state.cancelIcon,
                            editIcon = state.editIcon,
                        )

                        Text(
                            text = stringResource(id = R.string.notification_settings),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 30.dp, 0.dp, 10.dp),
                            fontSize = TEXT_SIZE_NORMAL,
                            color = Color.White,
                        )

                        state.notifiableMessages.forEach { notifiableMessage ->
                            NotifiableMessageComponent(
                                notifiableMessageName = notifiableMessage.key,
                                notifiableMessageValue = notifiableMessage.value
                            ) { newValue, onSuccess ->
                                viewModel.onNotifiableMessageValueChange(
                                    notifiableMessage.key,
                                    newValue,
                                    onSuccess
                                )
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}