package com.tchibo.plantbuddy.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.tchibo.plantbuddy.ui.theme.ubuntuFontFamily
import com.tchibo.plantbuddy.utils.ScreenInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(screenInfo: ScreenInfo) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            Text(
                text = screenInfo.title.orEmpty(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = ubuntuFontFamily
            )
        },
        navigationIcon = {
            screenInfo.onNavigationIconClick?.let { it ->
                IconButton(onClick = it) {
                    screenInfo.navigationIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screenInfo.navigationIconContentDescription,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}