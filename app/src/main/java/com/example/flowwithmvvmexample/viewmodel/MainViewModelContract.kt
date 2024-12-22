package com.example.flowwithmvvmexample.viewmodel

import com.example.flowwithmvvmexample.view.UserGroupMemberPresentable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow

interface MainViewModelInputs {
    fun setText(text: String): Job
    fun setSelectedId(selectedId: Set<UserGroupMemberPresentable>): Job
    fun setOption1(option: String): Job
    fun setSwitch1(value: Boolean): Job
    fun setSwitch2(value: Boolean): Job
    fun setSwitch3(value: Boolean): Job
    fun getOptions2(): Job
    fun onButtonClick(): Job
    fun onDismissClick(): Job
}

interface MainViewModelOutputs {
    val enableButton: Flow<Boolean>
    val text: Flow<String>
    val options1: SharedFlow<List<String>>
    val selectedOption: Flow<Set<UserGroupMemberPresentable>>
    val options2: Flow<List<UserGroupMemberPresentable>>
    val didClickButton: SharedFlow<Unit>
    val progressBarVisibility: Flow<Int>
    val showBottomSheet: Flow<Boolean>
    val switch1Value: Flow<Boolean>
    val switch2Value: Flow<Boolean>
    val switch3Value: Flow<Boolean>
    val enable1: Flow<Boolean>
    val enable2: Flow<Boolean>
    val enable3: Flow<Boolean>
    val selectedIds: Flow<Set<UserGroupMemberPresentable>>
    val selected: Flow<List<UserGroupMemberPresentable>>
}

interface MainViewModelType {
    val inputs: MainViewModelInputs
    val outputs: MainViewModelOutputs
}

