package com.example.flowwithmvvmexample.view

import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.flowwithmvvmexample.viewmodel.MainViewModelInputs

@Composable
fun BottomSheet(
    modifier: Modifier,
    selectionType: SelectionType = SelectionType.Single,
    showBottomSheet: Boolean,
    selectedIds: Set<UserGroupMemberPresentable>,
    options: List<UserGroupMemberPresentable>,
    inputs: MainViewModelInputs,
) {
    BottomSheetWrapper(
        modifier,
        showBottomSheet,
        inputs::onDismissClick
    ) {
        // MARK: - https://developer.android.com/develop/ui/compose/mental-model
        Selectable(
            selectionType,
            modifier,
            selectedIds,
            options,
            content = { modifier, item, isSelected, onSelected ->
                println("isSelected $isSelected")
                BottomSheetViewListItem(
                    modifier = Modifier.Companion,
                    ListItemDefaults.colors(
                        containerColor = if (isSelected) Color.Companion.Green else Color.Companion.Transparent
                    ),
                    item = item,
                )
                {
                    println("onSelected isSelected $!isSelected")
                    onSelected(!isSelected)
                }
            },
            inputs::setSelectedId
        )
    }
}