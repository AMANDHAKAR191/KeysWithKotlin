package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.keyswithkotlin.passwords.domain.model.Password

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PasswordItem(
    password: Password?,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit,
    onMoreClick: () -> Unit,
    onClickGloballyPositioned: ((LayoutCoordinates) -> Unit)? = null,
    onLongPressGloballyPositioned: ((LayoutCoordinates) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .combinedClickable(
                onClick = {
                    onItemClick()
                },
                onClickLabel = "view password",
                onLongClick = {
                    onItemLongClick()
                },
                onLongClickLabel = "view password setting"
            )
    ) {
        Surface(
            shape = RoundedCornerShape(10f),
            tonalElevation = 5.dp,
            shadowElevation = 5.dp,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    if (onClickGloballyPositioned != null) {
                        onClickGloballyPositioned(coordinates)
                    }
                }
                .size(width = 80.dp, height = 50.dp)
                .align(CenterVertically),
            color = MaterialTheme.colorScheme.primary,
            content = {
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

                        val scaleFactor = 80 / temp.length
                        val calculatedSize = (temp.length).toInt()

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
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(start = 20.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            if (password != null) {
                Text(
//                    modifier = if (onGloballyPositioned != null) {
//                        Modifier.onGloballyPositioned { coordinates ->
//                            onGloballyPositioned(coordinates)
//                        }
//                    } else {
//                        Modifier
//                    },
                    text = password.websiteName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (password != null) {
                Text(
                    text = password.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(
            onClick = { onMoreClick() },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    if (onLongPressGloballyPositioned != null) {
                        onLongPressGloballyPositioned(coordinates)
                    }
                }
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Mmore",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    Divider()
}