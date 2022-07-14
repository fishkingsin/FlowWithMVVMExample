package com.example.flowwithmvvmexample

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.flowwithmvvmexample.databinding.MySelectionItemLayoutBinding
import kotlinx.coroutines.launch

class MySelectionBottomSheetFragment : BottomSheetDialogFragment() {
    companion object {
        val TAG: String = MySelectionBottomSheetFragment::class.java.simpleName
    }

    private lateinit var adapter: MySelectionListAdapter
    private lateinit var recyclerView: RecyclerView
    val viewModel: Main.ViewModelType by activityViewModels<Main.ViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_selection_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView?.let { recyclerView ->
            recyclerView.isNestedScrollingEnabled = true


            adapter = MySelectionListAdapter(viewModel)
            recyclerView.adapter = adapter

            bindViewModel(viewModel)
        }
    }

    private fun bindViewModel(viewModel: Main.ViewModelType) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.outputs.options2.collect {
                if (BuildConfig.DEBUG) Log.d(TAG, "viewModel.outputs.options2.collect { $it ")
                adapter.setData(it)
                adapter.notifyDataSetChanged()
            }
        }

    }
}



class MySelectionListAdapter(
    private val viewModel: Main.ViewModelType
) : RecyclerView.Adapter<MySelectionListAdapter.ViewHolder>() {

    private var _list: MutableList<String> = mutableListOf()
    fun setData(data: List<String>) {
        _list = data.toMutableList()
    }
    private val list: List<String>
        get() = _list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    fun getItem(index: Int): String? {
        return when {
            index < list.size -> {
                list[index]
            }
            else -> {
                null
            }
        }
    }


    class ViewHolder private constructor(private val binding: MySelectionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: Main.ViewModelType, data: String) {
            binding.viewModel = viewModel
            binding.item = data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding =
                    MySelectionItemLayoutBinding.inflate(layoutInflater, parent, false)


                return ViewHolder(binding)
            }
        }

    }

}