package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun MinFab(
    item: MinFabItem,
    alpha: Float,
    textShadow: Dp,
    fabScale: Float,
    showLabel: Boolean = true,
    onMinFabItemClick: (MinFabItem) -> Unit
) {
    val buttonColor = MaterialTheme.colorScheme.secondary
    val shadow = Color.Black.copy(0.5f)
    Row {
        Text(
            text = item.label,
            modifier = Modifier
                .alpha(
                    animateFloatAsState(
                        targetValue = alpha,
                        animationSpec = tween(50)
                    ).value
                )
                .shadow(textShadow)
                .padding(start = 6.dp, end = 6.dp, top = 4.dp),
            fontSize = TextUnit.Unspecified,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(20.dp))
        Canvas(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    onClick = {
                        onMinFabItemClick(item)
                    },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 20.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
        ) {
            drawCircle(
                color = shadow,
                radius = fabScale,
                center = Offset(center.x + 2f, center.y + 2f)
            )
            drawCircle(
                color = buttonColor,
                radius = fabScale
            )
            //        drawImage(
            //            image = // complete this code
            //            topLeft = Offset(center.x - (item.icon.width) / 2, center.y - (item.icon.width) / 2)
            //        )
        }
    }
}