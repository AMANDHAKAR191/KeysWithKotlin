package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    text: String,
    label: String?,
    onValueChange: (it: String) -> Unit,
    enabled: Boolean,
    leadingIcon: (() -> ImageVector)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions,
    onClick: (() -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    onFocusChange: ((FocusState) ->Unit)? = null
) {
    TextField(
        value = text,
        label = {
            if (label != null) {
                Text(text = label)
            }
        },
        onValueChange = { onValueChange(it) },
        enabled = enabled,
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(leadingIcon(), contentDescription = "")
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                trailingIcon()
            }
        },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            }
            .fillMaxWidth()
            .focusRequester(focusRequester = FocusRequester())
            .onFocusChanged {
                if (onFocusChange != null) {
                    onFocusChange(it)
                }
            }
            .padding(horizontal = 20.dp),
        shape = TextFieldDefaults.filledShape,
        colors = TextFieldDefaults.outlinedTextFieldColors(),
    )
}