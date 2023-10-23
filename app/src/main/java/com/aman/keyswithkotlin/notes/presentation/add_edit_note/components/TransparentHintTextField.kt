package com.aman.keyswithkotlin.notes.presentation.add_edit_note.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TransparentHintTextField(
    text: String,
    label: String? = "",
    hint: String? = "",
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    minLines: Int = 1,
    showIndicator: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    onFocusChange: ((FocusState) -> Unit)? = null
) {
    Box(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(FocusRequester())
                .onFocusChanged {
                    onFocusChange?.invoke(it)
                },
            label = {
                label?.let {
                    Text(text = label)
                }
            },
            placeholder = {
                hint?.let {
                    Text(text = hint)
                }
            },
            singleLine = singleLine,
            textStyle = textStyle,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                disabledLabelColor = Color.Black,
                focusedPlaceholderColor = Color.Black,
                unfocusedPlaceholderColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedIndicatorColor = if (showIndicator) Color.Blue else Color.Transparent,
                unfocusedIndicatorColor = if (showIndicator) Color.Blue else Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),

            )
    }
}

@Composable
fun CustomTransparentTextField(
    text: String,
    label: String? = "",
    hint: String? = "",
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable() (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    minLines: Int = 1,
    showIndicator: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    onFocusChange: ((FocusState) -> Unit)? = null
) {
    Box(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(FocusRequester())
                .onFocusChanged {
                    onFocusChange?.invoke(it)
                },
            label = {
                label?.let {
                    Text(text = label)
                }
            },
            placeholder = {
                hint?.let {
                    Text(text = hint)
                }
            },
            trailingIcon = {
                trailingIcon?.invoke()
            },
            singleLine = singleLine,
            textStyle = textStyle,
            minLines = minLines,
            maxLines = 1,
            keyboardOptions = keyboardOptions,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = if (showIndicator) Color.Blue else Color.Transparent,
                unfocusedIndicatorColor = if (showIndicator) Color.Blue else Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}