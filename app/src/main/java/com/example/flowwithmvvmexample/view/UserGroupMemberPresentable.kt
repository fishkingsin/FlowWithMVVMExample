package com.example.flowwithmvvmexample.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

data class UserGroupMemberPresentable(
    val name: String,

    val role: String
) {
    val id: String = name.hashCode().toString()
    override fun hashCode(): Int {
        return id.hashCode() + name.hashCode() + role.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as com.example.flowwithmvvmexample.view.UserGroupMemberPresentable

        if (name != other.name) return false
        if (role != other.role) return false
        if (id != other.id) return false

        return true
    }
}

@Composable
fun BottomSheetViewListItem(
    modifier: Modifier,
    colors: ListItemColors,
    item: UserGroupMemberPresentable,
    onClick: () -> Unit = {},
) {
    Column {
        ListItem(
            headlineContent = { Text(item.name) },
            modifier = modifier.clickable {
                onClick()
            },
            trailingContent = {
                Row {
                    Text(item.role)
                }
            },
            colors = colors
        )
        HorizontalDivider()
    }

}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewBottomSheetViewListItem() {
    BottomSheetViewListItem(
        modifier = Modifier,
        item = UserGroupMemberPresentable("John Doe", "Admin"),
        colors = ListItemDefaults.colors()


    )
}
