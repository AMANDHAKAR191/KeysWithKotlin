package com.aman.keys.notes.presentation.note_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.aman.keys.notes.domain.model.Note

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(170.dp)
                .requiredHeightIn(min = 10.dp)
                .drawBehind {
                    val clipPath = Path().apply {
                        lineTo(size.width - cutCornerSize.toPx(), 0f)
                        lineTo(size.width, cutCornerSize.toPx())
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }

                    clipPath(clipPath) {
                        drawRoundRect(
                            color = Color(note.color.toColorInt()),
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                        drawRoundRect(
                            color = Color(
                                ColorUtils.blendARGB(note.color.toColorInt(), 0x000000, 0.2f)
                            ),
                            topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                            size = Size(cutCornerSize.toPx() + 100f, cutCornerSize.toPx() + 100f),
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                    }
                }
                .padding(top = 16.dp, start = 16.dp, bottom = 8.dp, end = 8.dp)
        ) {
            Text(
                text = note.noteBody,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete note",
                    tint = Color.Black
                )
            }
        }

    }
}