package com.aman.keys.passwords.presentation.componants

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.aman.keys.passwords.domain.model.Password

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
//            .padding(horizontal = 5.dp, vertical = 5.dp)
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
                        val temp = getOneWordName(password.websiteName)

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
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (password != null) {
                Text(
                    text = password.websiteName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (password != null) {
                Text(
                    text = password.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
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
                contentDescription = "more",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
//    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant)
}

fun getOneWordName(input: String): String {
    // Check if the input is a valid URL or app name

    val urlPattern1 = Regex("""^(https?://)?(www\.)?([a-zA-Z0-9-]+)\.([a-z]+)?(:\d+)?(/.*)?$""")
    val urlPattern2 = Regex("""^(https?://)?(www\.)?([a-zA-Z0-9-]+)\.([a-z]+)?\.([a-z]+)?\.([a-z]+)?(:\d+)?(/.*)?$""")
    val appPattern = Regex("""^[a-zA-Z0-9 -:\u2013&.]+$""") // Added \u2013 and \u003A to allow – and : and .

    if (
        !urlPattern1.matches(input) &&
        !urlPattern2.matches(input) &&
        !appPattern.matches(input)) {
        return input
    }

    // Extract the one word name from the input
    val urlMatch1 = urlPattern1.find(input)
    val urlMatch2 = urlPattern2.find(input)
    val appMatch1 = appPattern.find(input)
    return when {
        urlMatch1 != null -> {
            // Get the second group of the URL pattern, which is the subdomain name
            urlMatch1.groupValues[3] // Added groupValues[2] to include www. if present
        }
        urlMatch2 != null -> {
            // Get the second group of the URL pattern, which is the subdomain name
            urlMatch2.groupValues[3] // Added groupValues[2] to include www. if present
        }

        appMatch1 != null -> {

            // Preprocess the input to replace or remove special characters
            val cleanedInput = input.replace(Regex("[^a-zA-Z0-9 ]"), " ")

            // Get the first word of the app name
            val firstWord = cleanedInput.trim().split(" ")[0]
            firstWord
        }
        else -> {
            "Unknown error"
        }
    }
}