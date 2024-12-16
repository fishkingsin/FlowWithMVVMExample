package com.example.flowwithmvvmexample.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
enum class SelectionType {
    Single,
    Multiple
}

fun <T> SelectionType.function(isSelected: Boolean, selectedIds: Set<T>, option: T): Set<T> {
    return when (this) {
        SelectionType.Single -> {
            if (isSelected) {
                setOf(option)
            } else {
                emptySet()
            }
        }
        SelectionType.Multiple -> {
            if (isSelected) {
                selectedIds + option
            } else {
                selectedIds - option
            }
        }
    }
}

@Composable
fun <T> Selectable(
    selectionType: SelectionType = SelectionType.Single,
    modifier: Modifier,
    selectedIds: Set<T>,
    inputs: List<T>,
    content: @Composable (modifier: Modifier, T, Boolean, (Boolean) -> Unit ) -> Unit,
    onSelectedIdsChanged: (Set<T>) -> Unit) {
    var selectedIds by remember { mutableStateOf(selectedIds) }
    var options by remember { mutableStateOf(inputs) }
    LazyColumn {
        // Add 5 items
        items(options.size) { index ->
            content(modifier, options[index], selectedIds.contains(options[index])) { isSelected ->

                selectedIds = selectionType.function(isSelected, selectedIds, options[index])

                println("selectedIds: $selectedIds")
                onSelectedIdsChanged(selectedIds)
            }
        }
    }
}