package com.aman.keyswithkotlin.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Chats : BottomBarScreen(
        route = "chats",
        title = "Chats",
        icon = Icons.Default.Person
    )

    object Notes : BottomBarScreen(
        route = "notes",
        title = "Notes",
        icon = Icons.Default.Settings
    )

    object Settings : BottomBarScreen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}
