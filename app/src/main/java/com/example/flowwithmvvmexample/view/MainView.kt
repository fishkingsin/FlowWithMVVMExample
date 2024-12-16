package com.example.flowwithmvvmexample.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowwithmvvmexample.ui.theme.FlowWithMVVMExampleTheme
import com.example.flowwithmvvmexample.viewmodel.MainViewModel
import com.example.flowwithmvvmexample.viewmodel.MainViewModelType

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
    val options by viewModel.outputs.options2.collectAsState(emptyList())
    val selectedIds by viewModel.outputs.selectedIds.collectAsState(emptySet())
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

        BottomSheetWrapper(
            modifier,
            showBottomSheet,
            viewModel.inputs::onDismissClick
        ) {
            // MARK: - https://developer.android.com/develop/ui/compose/mental-model
            Selectable(
                SelectionType.Single,
                modifier,
                selectedIds,
                options,
                content = { modifier, item, isSelected, onSelected ->
                    println("isSelected $isSelected")
                    BottomSheetViewListItem(
                        modifier = Modifier,
                        ListItemDefaults.colors(
                            containerColor = if (isSelected) Color.Green else Color.Transparent
                        ),
                        item = item,
                    )
                    {
                        println("onSelected isSelected $!isSelected")
                        onSelected(!isSelected)
                    }


                },
                viewModel.inputs::setSelectedId
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