package com.example.bottomnavigationcomposeui.datamodel

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.bottomnavigationcomposeui.utils.Screens

data class NavigationBarItem(
    val title: String,
    val screenName: Any,
    val icon: ImageVector,
    val badgeCount: Int? = null,
    var selected: Boolean = false,
    val hasContent: Boolean = false
)
