package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class MultiFloatingState {
    Expended,
    Collapsed
}

class MinFabItem(
    val icon: ImageVector,
    val label: String,
    val identifier: String
)

enum class Identifier {
    AddEditPassword,
    GeneratePassword,
    Profile
}

@Composable
fun MultiFloatingButton(
    multiFloatingState: MultiFloatingState,
    onMultiFabStateChange: (MultiFloatingState) -> Unit,
    item: List<MinFabItem>,
    onMinFabItemClick: (MinFabItem) -> Unit
) {
    val transition = updateTransition(
        targetState = multiFloatingState,
        label = "transition"
    )
    val rotate by transition.animateFloat(label = "rotate") {
        if (it == MultiFloatingState.Expended) 45f else 0f
    }

    val fabScale by transition.animateFloat(label = "fabScale") {
        if (it == MultiFloatingState.Expended) 50f else 0f
    }
    val fabImageScale by transition.animateDp(label = "fabImageScale") {
        if (it == MultiFloatingState.Expended) 50.dp else 0.dp
    }

    val alpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = {
            tween(durationMillis = 100)
        }) {
        if (it == MultiFloatingState.Expended) 1f else 0f
    }
    val textShadow by transition.animateDp(
        label = "alpha",
        transitionSpec = {
            tween(durationMillis = 100)
        }) {
        if (it == MultiFloatingState.Expended) 0.5.dp else 0.dp
    }


    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (transition.currentState == MultiFloatingState.Expended) {
            item.forEach {
                MinFab(
                    item = it,
                    alpha = alpha,
                    textShadow = textShadow,
                    fabScale = fabScale,
                    fabImageScale = fabImageScale,
                    onMinFabItemClick = { minFabItem ->
                        onMinFabItemClick(minFabItem)
                    })
                Spacer(modifier = Modifier.size(40.dp))
            }
        }

        FloatingActionButton(
            onClick = {
                onMultiFabStateChange(
                    if (transition.currentState == MultiFloatingState.Expended) {
                        MultiFloatingState.Collapsed
                    } else {
                        MultiFloatingState.Expended
                    }
                )
            }
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "",
                modifier = Modifier.rotate(rotate)
            )
        }
    }
}


