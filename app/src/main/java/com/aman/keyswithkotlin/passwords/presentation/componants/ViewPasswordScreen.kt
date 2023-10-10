package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.keyswithkotlin.core.BiometricStatus
import com.aman.keyswithkotlin.passwords.domain.model.Password
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPasswordScreen(
    password: Password,
    modifier: Modifier = Modifier,
    onCloseButtonClick:()->Unit,
    unHidePasswordChar:()-> Flow<BiometricStatus>,
    onEditButtonClick:()->Unit,
    onShareButtonClick:(Password)->Unit
) {
    val scope = rememberCoroutineScope()
    // Use this state variable to trigger the LaunchedEffect
    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff


    Surface(
        shape = RoundedCornerShape(10f),
        tonalElevation = 5.dp,
        shadowElevation = 5.dp,
        modifier = modifier
            .wrapContentHeight(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    onCloseButtonClick()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                Surface(
                    shape = RoundedCornerShape(10f),
                    tonalElevation = 5.dp,
                    shadowElevation = 5.dp,
                    modifier = Modifier
                        .size(width = 80.dp, height = 50.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 5.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (password != null) {
                            val temp = try {
                                password.websiteName.split('_')[2]
                            } catch (e: IndexOutOfBoundsException) {
                                password.websiteName
                            }

                            val scaleFactor =  80 / temp.length
                            val calculatedSize =  (temp.length).toInt()

                            val adjustedSize = when {
                                calculatedSize in 10..12 -> 10.sp
                                calculatedSize in 6..9 -> 12.sp // upper limit
                                calculatedSize in 3..5 -> 20.sp // lower limit
                                calculatedSize <= 2 -> 30.sp
                                else -> 10.sp
                            }
                            Text(
                                text = temp,
                                style = TextStyle(
                                    fontSize = adjustedSize,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    onShareButtonClick(password)
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Edit password")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = {
                    Text(
                        text = "Username"
                    )
                },
                value = password.userName,
                readOnly = true,
                onValueChange = {})
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                label = {
                    Text(
                        text = "Password"
                    )
                },
                value = password.password,
                readOnly = true,
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = {
                        if (passwordVisibility){
                            passwordVisibility = false
                        }else{
                            scope.launch {
                                unHidePasswordChar().collectLatest {
                                    when(it){
                                        BiometricStatus.SUCCEEDED -> {
                                            passwordVisibility = true
                                        }
                                        BiometricStatus.FAILED ->{
                                            passwordVisibility = false
                                        }
                                        BiometricStatus.ERROR -> {
                                            passwordVisibility = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
            )
            Button(onClick = {
                onEditButtonClick()
            }) {
                Text(text = "Edit Password")
            }
        }
    }
}


