package com.aman.keyswithkotlin.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController,
    navigateTo: (String) -> Unit
) {
    val screens = listOf(
        BottomBarScreen.Password,
        BottomBarScreen.Chats,
        BottomBarScreen.Notes,
        BottomBarScreen.Settings,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEachIndexed { index, itemScreen ->
            AddItem(
                screen = itemScreen,
                currentDestination = currentDestination,
                onClicked = {
                    navigateTo(itemScreen.route)
                }
            )
        }

    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    onClicked: () -> Unit
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        label = { Text(text = screen.title)},
        icon = {
            Icon(imageVector = screen.icon, contentDescription = "Navigation Icon")
        },
        alwaysShowLabel = false,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Black,
            unselectedIconColor = Color.LightGray
        ),
        onClick = {
            onClicked()
        }
    )
}
