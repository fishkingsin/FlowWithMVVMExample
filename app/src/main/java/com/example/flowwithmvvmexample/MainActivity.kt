package com.example.flowwithmvvmexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.flowwithmvvmexample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private fun providerVMClass(): Class<Main.ViewModel>? {
        return Main.ViewModel::class.java
    }

    private var mySelectionBottomSheetFragment: MySelectionBottomSheetFragment? = null
    private lateinit var viewModel: Main.ViewModelType
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        binding.data = "Hello World"


        providerVMClass()?.let {
            viewModel = ViewModelProvider(this)[it]
            bindViewModel()
            binding.viewModel = viewModel
        }
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            viewModel.outputs.selectedOption.collect {
                binding.textView.text = it
                hideSelectionBottomSheet()
            }
        }

        lifecycleScope.launch {
            viewModel.outputs.text.collect {
                binding.button.isEnabled = it.isNotEmpty()
            }
        }
        lifecycleScope.launch {
            viewModel.outputs.didClickButton.withLatestFrom(viewModel.outputs.text) { _, b ->
                return@withLatestFrom b
            }.filter { it.isNotEmpty() }.collect {
                showSelectionBottomSheet()
            }
        }
    }

    private fun showSelectionBottomSheet() {
        mySelectionBottomSheetFragment?.dismiss()

        MySelectionBottomSheetFragment().let {
            it.show(supportFragmentManager, it.tag)
            mySelectionBottomSheetFragment = it
        }
    }

    private fun hideSelectionBottomSheet() {
        mySelectionBottomSheetFragment?.dismiss()
        mySelectionBottomSheetFragment = null
    }
}