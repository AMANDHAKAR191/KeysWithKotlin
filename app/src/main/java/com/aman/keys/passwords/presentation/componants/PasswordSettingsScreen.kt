package com.aman.keys.passwords.presentation.componants

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.keys.passwords.domain.model.Password

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordSettingsScreen(
    password: Password,
    spacerHeight: Dp = 10.dp,
    onCopyUsernameButtonClick: (Password) -> Unit,
    onCopyPasswordButtonClick: (Password) -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: (Password) -> Unit,
    onShareButtonClick: (Password) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10f),
        tonalElevation = 5.dp,
        shadowElevation = 5.dp,
        modifier = Modifier
            .wrapContentHeight(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10f),
                    tonalElevation = 5.dp,
                    shadowElevation = 5.dp,
                    modifier = Modifier
                        .size(width = 80.dp, height = 50.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.primary,
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 5.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
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
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(start = 20.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = password.websiteName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
            Divider()
            Spacer(modifier = Modifier.height(spacerHeight))
            OneRow(
                icon = Icons.Default.ContentCopy,
                text = "Copy username",
                description = "copy username",
                spacerHeight = 10.dp,
                spacerWidth = 10.dp,
                onClick = {
                    onCopyUsernameButtonClick(password)
                }
            )
            OneRow(
                icon = Icons.Default.ContentCopy,
                text = "Copy password",
                description = "copy username",
                spacerHeight = 10.dp,
                spacerWidth = 10.dp,
                onClick = {
                    onCopyPasswordButtonClick(password)
                }
            )
            OneRow(
                icon = Icons.Default.Edit,
                text = "Edit",
                description = "edit password",
                spacerHeight = 10.dp,
                spacerWidth = 10.dp,
                onClick = {
                    onEditButtonClick()
                }
            )
            OneRow(
                icon = Icons.Default.Share,
                text = "Share",
                description = "share password",
                spacerHeight = 10.dp,
                spacerWidth = 10.dp,
                onClick = {
                    onShareButtonClick(password)
                }
            )
            OneRow(
                icon = Icons.Default.Delete,
                text = "Delete",
                description = "delete password",
                spacerHeight = 10.dp,
                spacerWidth = 10.dp,
                onClick = {
                    onDeleteButtonClick(password)
                }
            )
        }
    }
}

@Composable
fun OneRow(
    icon: ImageVector,
    text: String,
    description: String,
    spacerHeight: Dp,
    spacerWidth: Dp,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(icon, contentDescription = description)
        }
        Spacer(modifier = Modifier.width(spacerWidth))
        Text(text = text)
    }
    Spacer(modifier = Modifier.height(spacerHeight))
}


