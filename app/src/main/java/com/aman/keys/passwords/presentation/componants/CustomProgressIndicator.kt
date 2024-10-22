package com.aman.keys.passwords.presentation.componants

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.keys.ui.theme.passwordPoor
import com.aman.keys.ui.theme.passwordStrong
import com.aman.keys.ui.theme.passwordWeak

@Composable
fun CustomProgressIndicator(
    totalBudgetAmount: Float,
    progress: Float,
    strokeWidth: Dp = 15.dp,
) {
    val normalizeValue = progress / totalBudgetAmount
    val size by animateFloatAsState(
        targetValue = normalizeValue,
        tween(
            durationMillis = 1500,
            delayMillis = 200,
            easing = LinearOutSlowInEasing
        ), label = ""
    )


    val progressBarColor = remember { mutableStateOf(passwordStrong) }
    val percentage = (progress * 100) / totalBudgetAmount
    var textValue = remember {
        mutableStateOf("")
    }
    when {
        percentage <= 50 -> {
            val color by animateColorAsState(targetValue = passwordPoor, label = "")
            progressBarColor.value = color
            textValue.value = "Weak password"
        }

        (percentage >= 50) && (percentage < 70) -> {
            val color by animateColorAsState(targetValue = passwordWeak, label = "")
            progressBarColor.value = color
            textValue.value = "Poor password"
        }

        percentage >= 70 -> {
            val color by animateColorAsState(targetValue = passwordStrong, label = "")
            progressBarColor.value = color
            textValue.value = "Strong password"
        }

        else -> {

        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
    ) {
        Text(
            text = textValue.value,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(strokeWidth)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(size)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(30.dp))
                    .background(progressBarColor.value)
                    .animateContentSize()
            )
        }
    }
}