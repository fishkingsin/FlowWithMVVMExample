package com.example.flowwithmvvmexample.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Toggle(
    title: String,
    switch1Value: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row {
        Text(title)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            switch1Value,
            onCheckedChange,
            Modifier.padding(start = 8.dp),
            enabled = enabled
        )
    }
}