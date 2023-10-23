package com.aman.keyswithkotlin.auth.presentation.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.core.Constants

@Composable
fun AuthContent(
    padding: PaddingValues,
    oneTapSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Image(
            painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.keys_full_logo_dark else R.drawable.keys_full_logo_light),
            contentDescription = "keys full logo"
        )
        Button(
            modifier = Modifier.padding(bottom = 100.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(),
            onClick = oneTapSignIn
        ) {
            val composition1 by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    R.raw.animation_sign_with_google
                )
            )
            val progress1 by animateLottieCompositionAsState(
                composition1,
                iterations = 3
            )
            LottieAnimation(
                composition = composition1,
                progress = { progress1 },
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .testTag("LottieAnimation")
            )
            Text(
                text = Constants.SIGN_IN_WITH_GOOGLE,
                modifier = Modifier.padding(6.dp),
                fontSize = 18.sp
            )
        }
    }
}