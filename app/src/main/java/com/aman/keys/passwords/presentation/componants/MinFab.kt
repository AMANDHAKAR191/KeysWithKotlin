package com.aman.keys.passwords.presentation.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun MinFab(
    item: MinFabItem,
    alpha: Float,
    translateY: Dp,
    onMinFabItemClick: (MinFabItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .offset(y = -translateY)
            .alpha(alpha)
            .clickable(
                enabled = alpha > 0.6,
                onClick = {
                    onMinFabItemClick(item)
                }
            )
            .then(if (alpha > 0.0f) Modifier else Modifier.alpha(0f).requiredWidth(0.dp).requiredHeight(0.dp)),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.label,
            modifier = Modifier
                .padding(start = 6.dp, end = 6.dp, top = 4.dp),
            fontSize = TextUnit(18f, type = TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(20.dp))
        FloatingActionButton(
            onClick = { onMinFabItemClick(item) },
            shape = CircleShape
        ) {
            Icon(imageVector = item.icon, contentDescription = "")
        }
    }
}