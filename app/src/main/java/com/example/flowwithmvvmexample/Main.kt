package com.example.flowwithmvvmexample

import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReference

fun <A, B, R> Flow<A>.withLatestFrom(other: Flow<B>, transform: suspend (A, B) -> R): Flow<R> =
    flow {
        coroutineScope {
            val latestB = AtomicReference<Any>(null)
            val outerScope = this

            launch {
                try {
                    other.collect { latestB.set(it) }
                } catch (e: CancellationException) {
                    outerScope.cancel(e) // cancel outer scope on cancellation exception, too
                }
            }

            collect { a: A ->
                latestB.get().let {
                    if (it != null) {
                        emit(transform(a, it as B))
                    }
                }
            }
        }
    }

interface Main {
    interface Inputs {
        fun setText(text: CharSequence): Job
        fun setOption1(option: String): Job
        fun enable1(value: Boolean): Job
        fun enable2(value: Boolean): Job
        fun enable3(value: Boolean): Job
        fun getOptions2(): Job
        fun onButtonClick(): Job
    }

    interface Outputs {
        val enableButton: Flow<Boolean>
        val text: Flow<String>
        val options1: SharedFlow<List<String>>
        val selectedOption: Flow<String>
        val options2: Flow<List<String>>
        val didClickButton: SharedFlow<Unit>
        val progressBarVisibility: Flow<Int>
        val showBottomSheet: Flow<String>
        val switch1Value: Flow<Boolean>
        val switch1Enabled: Flow<Boolean>
        val switch2Enabled: Flow<Boolean>
        val switch3Enabled: Flow<Boolean>
        val enable2: Flow<Boolean>
        val enable3: Flow<Boolean>
        val bottomsheetOptions: Flow<Pair<List<String>, String?>>
    }

    interface ViewModelType {
        val inputs: Inputs
        val outputs: Outputs
    }

    open class ViewModel : androidx.lifecycle.ViewModel(), LifecycleObserver, ViewModelType, Inputs,
        Outputs {
        override val inputs: Inputs
            get() = this
        override val outputs: Outputs
            get() = this


        override fun setText(text: CharSequence): Job = launchUI {
            _text.emit(text.toString())
        }

        override fun setOption1(option: String) = launchUI {
            _selectedOption.emit(option)
        }

        override fun enable1(value: Boolean): Job = launchUI {
            _switch1Value.emit(value)
        }

        override fun enable2(value: Boolean): Job = launchUI {
            _enable2.emit(value)
        }

        override fun enable3(value: Boolean): Job = launchUI {
            _enable3.emit(value)
        }

        override fun getOptions2() = launchUI {

        }

        override fun onButtonClick(): Job = launchUI {
            _options2.emit(emptyList())
            _didClickButton.emit(Unit)
            _progressBarVisibility.emit(true)
            delay(2000)
            _options2.emit(mutableListOf("1", "2", "3", "4", "5"))
            _progressBarVisibility.emit(false)
        }

        override val enableButton: Flow<Boolean>
            get() = combine(
                _text,
                _switch1Value,
                _enable2,
                _enable3,
            ) { a, b, c, d ->
                return@combine !a.isNullOrEmpty() && b == true && c == true && d == true
            }

        override val text: Flow<String>
            get() = _text.filterNotNull()
        private val _text: MutableStateFlow<String?> = MutableStateFlow("")
        private val _switch1Value: MutableStateFlow<Boolean?> = MutableStateFlow(false)
        private val _enable2: MutableStateFlow<Boolean?> = MutableStateFlow(false)
        private val _enable3: MutableStateFlow<Boolean?> = MutableStateFlow(false)

        override val options1: SharedFlow<List<String>>
            get() = _options1
        private val _options1: MutableSharedFlow<List<String>> = MutableSharedFlow()
        override val selectedOption: Flow<String>
            get() = _selectedOption.filterNotNull()
        private val _selectedOption: MutableStateFlow<String?> = MutableStateFlow(null)
        override val bottomsheetOptions: Flow<Pair<List<String>, String?>>
            get() = _options2.filterNotNull().combine(_selectedOption.filterNotNull()) { a, b ->
                return@combine Pair(a, b)
            }

        private val _options2: MutableStateFlow<List<String>?> = MutableStateFlow(null)
        override val options2: Flow<List<String>>
            get() = _options2.filterNotNull()

        override val didClickButton: SharedFlow<Unit>
            get() = _didClickButton
        
            private val _progressBarVisibility: MutableStateFlow<Boolean?> = MutableStateFlow(null)
        override val progressBarVisibility: Flow<Int>
            get() = _progressBarVisibility.filterNotNull().map { if (it) View.VISIBLE else View.INVISIBLE }
        
            override val showBottomSheet: Flow<String>
            get() = didClickButton
                .withLatestFrom(text) { _, b ->
                    return@withLatestFrom b
                }.filter { it.isNotEmpty() }

        override val switch1Value: Flow<Boolean>
            get() = _switch1Value.filterNotNull().combine(_text.filterNotNull()) { enable1, text ->
                enable1 && text.isNotEmpty()
            }

        override val switch1Enabled: Flow<Boolean>
            get() = _text.map { !it.isNullOrEmpty() }

        override val switch2Enabled: Flow<Boolean>
            get() = switch1Value.map { it }

        override val switch3Enabled: Flow<Boolean>
            get() = switch1Value.map { it }

        override val enable2: Flow<Boolean>
            get() = _enable2.filterNotNull().combine(switch1Value) { enable2, enable1 ->
                enable1 && enable2
            }
        override val enable3: Flow<Boolean>
            get() = _enable3.filterNotNull().combine(enable2) { enable3, enable2 ->
                enable2 && enable3
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
    }
}