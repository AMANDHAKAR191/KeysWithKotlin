package com.aman.keys.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aman.keys.R

@Composable
fun CustomCircularProgressBar(showStatus: Boolean = false, status: String? = null) {
    if (showStatus && status == null) {
        throw IllegalArgumentException("Status is required when showStatus is true")
    }
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).zIndex(2f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(100.dp)
                .testTag("LottieAnimation")
        )
        Spacer(modifier = Modifier.size(10.dp))
        status?.let {
            Text(text = it, color = MaterialTheme.colorScheme.onSurface)
        }

    }
}


//@Preview
//@Composable
//fun PreviewCustomCircularProgressBar() {
//    CustomCircularProgressBar(true, "Hello")
//}