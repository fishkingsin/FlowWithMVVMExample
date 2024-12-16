package com.example.flowwithmvvmexample.viewmodel

import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flowwithmvvmexample.view.UserGroupMemberPresentable
import com.hoc081098.flowext.withLatestFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map


class MainViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(userId) as T
    }
}

open class MainViewModel(private val userId: String) : ViewModel(), LifecycleObserver, MainViewModelType,
    MainViewModelInputs,
    MainViewModelOutputs {
    override val inputs: MainViewModelInputs
        get() = this
    override val outputs: MainViewModelOutputs
        get() = this


    override fun setText(text: String): Job = launchUI {
        _text.emit(text)
    }

    override fun setSelectedId(selectedId: Set<UserGroupMemberPresentable>) = launchUI {
        _selectedOption.emit(selectedId)
    }

    override fun setOption1(option: String) = launchUI {
//        _selectedOption.emit(option)
    }

    override fun setSwitch1(value: Boolean) = launchUI {
        _switch1Value.emit(value)
    }

    override fun setSwitch2(value: Boolean) = launchUI {
        _switch2Value.emit(value)
    }

    override fun setSwitch3(value: Boolean) = launchUI {
        _switch3Value.emit(value)
    }

    override fun getOptions2() = launchUI {
        if (_options2.value.isNotEmpty()) return@launchUI
        _options2.emit(emptyList())
        _progressBarVisibility.emit(true)
//        delay(2000)
        _options2.emit(
            mutableListOf(
                UserGroupMemberPresentable(name = "Mike Yates", role = "Senior iOS Engineer"),
                UserGroupMemberPresentable(name = "Elbert Wilson", role = "Senior iOS Engineer"),
                UserGroupMemberPresentable(name = "Anita Thomas", role = "Senior iOS Engineer"),
                UserGroupMemberPresentable(name = "Leona Lane", role = "Senior iOS Engineer"),
                UserGroupMemberPresentable(name = "Chad Roy", role = "Senior iOS Engineer"),
                UserGroupMemberPresentable(
                    name = "Naida Schill",
                    role = "Staff Engineer - Mobile DevXP"
                )
            )
        )
        _progressBarVisibility.emit(false)
    }

    override fun onButtonClick(): Job = launchUI {

        _showBottomSheet.emit(true)

        _didClickButton.emit(Unit)


        getOptions2()

    }

    override fun onDismissClick(): Job = launchUI {
        _showBottomSheet.emit(false)
    }

    override val text: Flow<String>
        get() = _text.filterNotNull()
    private val _text: MutableStateFlow<String?> = MutableStateFlow("")

    private val _switch1Value: MutableStateFlow<Boolean?> = MutableStateFlow(false)
    private val _switch2Value: MutableStateFlow<Boolean?> = MutableStateFlow(false)
    private val _switch3Value: MutableStateFlow<Boolean?> = MutableStateFlow(false)

    override val options1: SharedFlow<List<String>>
        get() = _options1
    private val _options1: MutableSharedFlow<List<String>> = MutableSharedFlow()
    override val selectedOption: Flow<Set<UserGroupMemberPresentable>>
        get() = _selectedOption.filterNotNull()
    private val _selectedOption: MutableStateFlow<Set<UserGroupMemberPresentable>> = MutableStateFlow(emptySet())
    override val selectedIds: Flow<Set<UserGroupMemberPresentable>>
        get() = _selectedOption

    private val _options2: MutableStateFlow<List<UserGroupMemberPresentable>> =
        MutableStateFlow(emptyList())
    override val options2: Flow<List<UserGroupMemberPresentable>>
        get() = _options2.filterNotNull()

    override val didClickButton: SharedFlow<Unit>
        get() = _didClickButton

    private val _progressBarVisibility: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    override val progressBarVisibility: Flow<Int>
        get() = _progressBarVisibility
            .filterNotNull()
            .map { if (it) View.VISIBLE else View.INVISIBLE }

    override val showBottomSheet: Flow<Boolean>
        get() = _showBottomSheet

    private val _showBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)


    override val switch1Value: Flow<Boolean>
        get() = _switch1Value.filterNotNull()

    override val switch2Value: Flow<Boolean>
        get() = _switch2Value.filterNotNull()

    override val switch3Value: Flow<Boolean>
        get() = _switch3Value.filterNotNull()

    override val enable1: Flow<Boolean>
        get() = text.map { it.isNotEmpty() }

    override val enable2: Flow<Boolean>
        get() = _switch1Value
            .filterNotNull()
            .withLatestFrom(enable1) { enable2, enable1 ->
                enable1 && enable2
            }
    override val enable3: Flow<Boolean>
        get() = _switch2Value
            .filterNotNull()
            .withLatestFrom(enable2) { switch2Value, enable2 ->
                enable2 && switch2Value
            }

    override val enableButton: Flow<Boolean>
        get() = switch3Value.withLatestFrom(
            enable3
        ) { a, b ->
            return@withLatestFrom a && b
        }


    private val _didClickButton: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 0)

    private val error by lazy { MutableLiveData<Exception>() }

    private val finally by lazy { MutableLiveData<Int>() }

    private fun launchUI(block: suspend CoroutineScope.() -> Unit): Job = viewModelScope.async {
        try {
            block()
        } catch (e: Exception) {
            error.value = e
        } finally {
            finally.value = 200
        }
    }

    fun getError(): LiveData<Exception> {
        return error
    }

    fun getFinally(): LiveData<Int> {
        return finally
    }

    var jobs = mutableListOf<Job>()

    override fun onCleared() {
        super.onCleared()
        jobs.forEach(Job::cancel)
    }
}