package com.aman.keys.auth.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aman.keys.core.Constants.REVOKE_ACCESS
import com.aman.keys.core.Constants.SIGN_OUT
import com.aman.keys.navigation.PROFILE_SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    signOut: () -> Unit,
    revokeAccess: () -> Unit,
    navigateToPasswordScreen:()->Unit
) {
    var openMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = PROFILE_SCREEN
            )
        },
        actions = {
            IconButton(
                onClick = {
                    openMenu = !openMenu
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = openMenu,
                onDismissRequest = {
                    openMenu = !openMenu
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = SIGN_OUT)
                    },
                    onClick = {
                        signOut()
                        openMenu = !openMenu
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = REVOKE_ACCESS)
                    },
                    onClick = {
                        revokeAccess()
                        openMenu = !openMenu
                    }
                )
            }
        },
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.clickable { navigateToPasswordScreen() })
        }
    )
}