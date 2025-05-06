package com.example.flowwithmvvmexample.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowwithmvvmexample.ui.theme.FlowWithMVVMExampleTheme
import com.example.flowwithmvvmexample.viewmodel.MainViewModel
import com.example.flowwithmvvmexample.viewmodel.MainViewModelType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

@Composable
fun <ViewModelType : MainViewModelType> MainView(
    viewModel: ViewModelType,
    modifier: Modifier = Modifier
) {
    val text by viewModel.outputs.text.collectAsState("")
    val switch1Value by viewModel.outputs.switch1Value.collectAsState(false)
    val switch2Value by viewModel.outputs.switch2Value.collectAsState(false)
    val switch3Value by viewModel.outputs.switch3Value.collectAsState(false)
    val switch1Enable by viewModel.outputs.enable1.collectAsState(false)
    val switch2Enable by viewModel.outputs.enable2.collectAsState(false)
    val switch3Enable by viewModel.outputs.enable3.collectAsState(false)
    val enableButton by viewModel.outputs.enableButton.collectAsState(false)
    val showBottomSheet by viewModel.outputs.showBottomSheet.collectAsState(false)
    val options: List<UserGroupMemberPresentable> by viewModel.outputs.options2.collectAsState(
        emptyList()
    )
    val selectedIds by viewModel.outputs.selectedIds.collectAsState(emptySet())
    val selected by viewModel.outputs.selected.collectAsState(emptyList())
    var selectionType by remember { mutableStateOf(SelectionType.Single) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        CharacterLimitedTextField(
            modifier = Modifier.fillMaxWidth(),
            input = text,
            onTextChanged = viewModel.inputs::setText,
            maxCharacters = 10
        )
        Toggle("Enable 1", switch1Value, switch1Enable, viewModel.inputs::setSwitch1)
        Toggle("Enable 2", switch2Value, switch2Enable, viewModel.inputs::setSwitch2)
        Toggle("Enable 3", switch3Value, switch3Enable, viewModel.inputs::setSwitch3)

        Button(
            viewModel.inputs::onButtonClick,
            enabled = enableButton
        ) {
            Text("Show Bottom sheet")
        }
        SingleChoiceSegmentedButton(modifier) {
            selectionType = it
        }
        LazyColumn {
            items(selected) { item ->
                ListItem(
                    headlineContent = { Text(item.name) },
                )
            }
        }

        BottomSheet(
            modifier = modifier,
            selectionType = selectionType,
            showBottomSheet = showBottomSheet,
            selectedIds = selectedIds,
            options = options,
            inputs = viewModel.inputs
        )

    }
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    onSelectionType: (SelectionType) -> Unit = {}
) {
    var selectedIndex: Int by remember { mutableIntStateOf(0) }

    val options = SelectionType.entries.map { it.name }

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    onSelectionType(SelectionType.entries[index])
                },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}


@Preview(
    showSystemUi = true,
    device = Devices.DEFAULT,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun MainViewPreview() {
    FlowWithMVVMExampleTheme {
        MainView(MainViewModel("Adam"))
    }
}