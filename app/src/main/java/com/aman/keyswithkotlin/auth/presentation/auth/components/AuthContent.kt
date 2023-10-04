package com.aman.keyswithkotlin.auth.presentation.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.core.Constants

@Composable
fun AuthContent(
    padding: PaddingValues,
    oneTapSignIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            modifier = Modifier.padding(bottom = 100.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.purple_700)
            ),
            onClick = oneTapSignIn
        ) {
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    R.raw.animation_sign_with_google
                )
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = 3
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .testTag("LottieAnimation")
            )
//            Image(
//                painter = painterResource(
//                    id = R.drawable.ic_google_logo
//                ),
//                contentDescription = null
//            )
            Text(
                text = Constants.SIGN_IN_WITH_GOOGLE,
                modifier = Modifier.padding(6.dp),
                fontSize = 18.sp
            )
        }
    }
}