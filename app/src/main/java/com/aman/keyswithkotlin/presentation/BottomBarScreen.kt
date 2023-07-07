package com.aman.keyswithkotlin.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.aman.keyswithkotlin.navigation.Graph

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Password : BottomBarScreen(
        route = "Password",
        title = "Password",
        icon = Icons.Default.Home
    )

    object Chats : BottomBarScreen(
        route = "Chat",
        title = "Chats",
        icon = Icons.Default.Person
    )

    object Notes : BottomBarScreen(
        route = "Note",
        title = "Notes",
        icon = Icons.Default.Settings
    )

    object Settings : BottomBarScreen(
        route = "Setting",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}
