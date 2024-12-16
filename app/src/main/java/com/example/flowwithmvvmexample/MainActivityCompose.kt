package com.example.flowwithmvvmexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.flowwithmvvmexample.ui.theme.FlowWithMVVMExampleTheme
import com.example.flowwithmvvmexample.view.MainView
import com.example.flowwithmvvmexample.viewmodel.MainViewModel
import com.example.flowwithmvvmexample.viewmodel.MainViewModelFactory

class MainActivityCompose : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory("hoc081098")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowWithMVVMExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

