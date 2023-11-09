package com.tchibo.plantbuddy.utils

import androidx.compose.ui.graphics.vector.ImageVector

data class ScreenInfo(
    val navigationIcon: ImageVector? = null,
    val navigationIconContentDescription: String? = null,
    val onNavigationIconClick: (() -> Unit)? = null,
    val title: String? = null
)