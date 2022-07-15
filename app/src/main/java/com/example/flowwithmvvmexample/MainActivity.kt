package com.example.flowwithmvvmexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.selectedOption.collect {
                    binding.textView.text = it
                    hideSelectionBottomSheet()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.enableButton.collect {
                    binding.button.isEnabled = it
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.showBottomSheet.collect {
                    showSelectionBottomSheet()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.text.collect {

                    binding.switch1.isEnabled = it.isNotEmpty()

                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.enable1.collect {

                        binding.switch1.isChecked = it
                        binding.switch2.isEnabled = it
                        binding.switch3.isEnabled = it

                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.enable2.collect {
                    binding.switch2.isChecked = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.enable3.collect {
                    binding.switch3.isChecked = it
                }
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