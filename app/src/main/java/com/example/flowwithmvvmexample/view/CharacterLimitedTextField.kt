package com.example.flowwithmvvmexample.view

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun CharacterLimitedTextField(
    input: String,
    onTextChanged: (String) -> Unit,
    maxCharacters: Int,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(input) }
    TextField(
        value = text,
        onValueChange = {

            if (it.length <= maxCharacters) {
                text = it
                onTextChanged(it)
            }
        },
        modifier = modifier,
        label = { Text("Label") }
    )
}