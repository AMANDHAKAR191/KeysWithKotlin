package com.aman.keyswithkotlin.on_boarding_screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.ui.theme.ColorGreen
import com.aman.keyswithkotlin.ui.theme.ColorYellow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    navigateToAuthScreen: () -> Unit,
) {
    val items = ArrayList<OnBoardingData>()
    items.add(
        OnBoardingData(
            R.raw.animation_password,
            "Secure Authentication",
            "Log in with a high level of security and privacy. Protects the app data and functionality from malicious attacks.",
            backgroundColor = Color(0xFF0189C5),
            mainColor = Color(0xFF00B5EA)
        )
    )

    items.add(
        OnBoardingData(
            R.raw.animation_add_password_note,
            "Save password and Note more Securely",
            "Store your passwords and notes in an encrypted vault. You can access them anytime with your biometric authentication.",
            backgroundColor = Color(0xFFE4AF19),
            mainColor = ColorYellow
        )
    )

    items.add(
        OnBoardingData(
            R.raw.animation_autofill,
            "Autofill Password",
            "Automatically fill in your login credentials on any android app that supports it.",
            backgroundColor = Color(0xFF96E172),
            mainColor = ColorGreen
        )
    )
    items.add(
        OnBoardingData(
            R.raw.animation_password,
            "Easy Sharing",
            "Share you password and note in clicks",
            backgroundColor = Color(0xFF0189C5),
            mainColor = Color(0xFF00B5EA)
        )
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { items.size }
    )

    OnBoardingPager(
        item = items, pagerState = pagerState, modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
        navigateToAuthScreen = {
            navigateToAuthScreen()
        }
    )

}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    DelicateCoroutinesApi::class
)
@Composable
fun OnBoardingPager(
    item: List<OnBoardingData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    navigateToAuthScreen: () -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )


    Box(modifier = Modifier.fillMaxSize()) {
        SimpleBottomSheetScaffoldSample(
            sheetContent = {
                Column(
                    modifier = Modifier.height(300.dp).padding(all = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    PagerIndicator(items = item, currentPage = pagerState.currentPage)
                    Text(
                        text = item[pagerState.currentPage].title,
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = item[pagerState.currentPage].desc,
                        modifier = Modifier.padding(all = 10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Normal
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (pagerState.currentPage != 3) {
                            TextButton(onClick = {
                                navigateToAuthScreen()
                            }) {
                                Text(
                                    text = "Skip Now",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = {
                                    GlobalScope.launch {
                                        pagerState.scrollToPage(
                                            pagerState.currentPage + 1,
                                            pageOffsetFraction = 0f
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.size(65.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    //show home screen
                                    navigateToAuthScreen()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(),
                                contentPadding = PaddingValues(vertical = 12.dp),
                                elevation = ButtonDefaults.elevatedButtonElevation()
                            ) {
                                Text(
                                    text = "Get Started",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            },
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(state = pagerState) { page ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(item[page].backgroundColor),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            val composition by rememberLottieComposition(
                                spec = LottieCompositionSpec.RawRes(
                                    item[page].image
                                )
                            )
                            val progress by animateLottieCompositionAsState(
                                composition,
                                iterations = LottieConstants.IterateForever
                            )
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier
                                    .width(400.dp)
                                    .height(400.dp)
                                    .testTag("LottieAnimation")
                            )
                        }
                    }

                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBottomSheetScaffoldSample(
    sheetContent: @Composable (() -> Unit),
    content: @Composable (() -> Unit)
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 350.dp,
        sheetContent = {
            sheetContent()
        },
        content = {
            content()
        })
}


@Composable
fun PagerIndicator(currentPage: Int, items: List<OnBoardingData>) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(top = 10.dp)
    ) {
        repeat(items.size) {
            Indicator(isSelected = it == currentPage, color = items[it].mainColor)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean, color: Color) {
    val width = animateDpAsState(targetValue = if (isSelected) 40.dp else 10.dp, label = "")
    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) color else Color.Gray.copy(alpha = 0.5f)
            )
    )
}
