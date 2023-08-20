package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.passwords.domain.model.Password

@Composable
fun SearchedPasswordItem(
    password: Password?,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 10.dp)) {
        Surface(
            shape = RoundedCornerShape(10f),
            tonalElevation = 5.dp,
            shadowElevation = 5.dp,
            modifier = Modifier
                .weight(3f)
                .aspectRatio(2.0f)
                .clickable {
                    onItemClick()
                }
                .align(CenterVertically),
            color = MaterialTheme.colorScheme.primary,
            content = {
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(vertical = 5.dp, horizontal = 10.dp)
                        ,
                    contentAlignment = Alignment.Center,
                ) {
                    if (password != null) {
                        Text(
                            text = password.websiteName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .wrapContentSize()
                .weight(6f)
                .padding(16.dp)
                .clickable {
                    onItemClick()
                }
        ) {
            if (password != null) {
                Text(
                    text = password.websiteName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = password.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    Divider()
}

@Composable
@Preview
fun Preview1() {
    SearchedPasswordItem(
        password = Password("AMAN", "DFSFS", "AMAN", "", "CDJSCJSOI"),
        onItemClick = { /*TODO*/ },
        onDeleteClick = {})
}