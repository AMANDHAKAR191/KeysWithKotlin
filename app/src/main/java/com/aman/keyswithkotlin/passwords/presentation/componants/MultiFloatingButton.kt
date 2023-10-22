package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class MultiFloatingState {
    Expended, Collapsed
}

class MinFabItem(
    val icon: ImageVector, val label: String, val identifier: String
)

enum class Identifier {
    AddEditPassword, GeneratePassword, Profile
}

@Composable
fun ExpendableFloatingButton(
    multiFloatingState: MultiFloatingState,
    onMultiFabStateChange: (MultiFloatingState) -> Unit,
    item: List<MinFabItem>,
    onMinFabItemClick: (MinFabItem) -> Unit,
    onFABClickGloballyPositioned: ((LayoutCoordinates) -> Unit)? = null,
) {
    val transition = updateTransition(
        targetState = multiFloatingState, label = "transition"
    )
    val rotate by transition.animateFloat(label = "rotate", transitionSpec = {
        tween(durationMillis = 300, easing = FastOutSlowInEasing)
    }) {
        if (it == MultiFloatingState.Expended) 45f else 0f
    }
    val translateY by transition.animateDp(label = "translateY", transitionSpec = {
        tween(durationMillis = 300, easing = FastOutSlowInEasing)
    }) {
        if (it == MultiFloatingState.Expended) 90.dp else 0.dp
    }
    val alpha by transition.animateFloat(label = "alpha", transitionSpec = {
        tween(durationMillis = 300)
    }) {
        if (it == MultiFloatingState.Expended) 1f else 0f
    }


    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        MinFab(
            item = item.get(0),
            alpha = alpha,
            translateY = translateY,
            onMinFabItemClick = { minFabItem ->
                onMinFabItemClick(minFabItem)
            }
        )
        MinFab(item = item.get(1),
            alpha = alpha,
            translateY = (translateY*16)/9,
            onMinFabItemClick = { minFabItem ->
                onMinFabItemClick(minFabItem)
            }
        )
        MinFab(item = item.get(2),
            alpha = alpha,
            translateY = (translateY*5)/2,
            onMinFabItemClick = { minFabItem ->
                onMinFabItemClick(minFabItem)
            }
        )
        FloatingActionButton(
            onClick = {
                onMultiFabStateChange(
                    if (transition.currentState == MultiFloatingState.Expended) {
                        MultiFloatingState.Collapsed
                    } else {
                        MultiFloatingState.Expended
                    }
                )
            }, modifier = Modifier.onGloballyPositioned { coordinates ->
                if (onFABClickGloballyPositioned != null) {
                    onFABClickGloballyPositioned(coordinates)
                }
            }
        ) {
            Icon(
                Icons.Default.Add, contentDescription = "", modifier = Modifier.rotate(rotate)
            )
        }
    }
}

@Preview
@Composable
fun MultiFloatingButtonPreview() {
    var multiFloatingState by remember { mutableStateOf(MultiFloatingState.Collapsed) }
    val items = listOf(
        MinFabItem(
            icon = Icons.Default.Person, label = "Profile", identifier = Identifier.Profile.name
        ), MinFabItem(
            icon = Icons.Default.Password,
            label = "Generate Password",
            identifier = Identifier.GeneratePassword.name
        ), MinFabItem(
            icon = Icons.Default.Create,
            label = "Add Password",
            identifier = Identifier.AddEditPassword.name
        )
    )
    ExpendableFloatingButton(multiFloatingState = multiFloatingState, onMultiFabStateChange = {
        multiFloatingState = it
    }, item = items, onMinFabItemClick = { minFabItem ->
        when (minFabItem.identifier) {
            Identifier.AddEditPassword.name -> {
//                    navigateToAddEditPasswordScreen()
            }

            Identifier.GeneratePassword.name -> {
//                    navigateToGeneratePasswordScreen()
            }

            Identifier.Profile.name -> {
//                    navigateToProfileScreen()
            }

        }
    })
}


