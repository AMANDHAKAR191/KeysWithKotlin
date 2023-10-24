package com.aman.keyswithkotlin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val index:Int,
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Password : BottomBarScreen(
        index = 0,
        route = "Password",
        title = "Password",
        icon = Icons.Default.Home
    )

    object Chats : BottomBarScreen(
        index = 1,
        route = "Chat",
        title = "Chats",
        icon = Icons.Default.ChatBubble
    )

    object Notes : BottomBarScreen(
        index = 2,
        route = "Note",
        title = "Notes",
        icon = Icons.Default.StickyNote2
    )

    object Settings : BottomBarScreen(
        index = 3,
        route = "Setting",
        title = "Setting",
        icon = Icons.Default.Settings
    )
}
