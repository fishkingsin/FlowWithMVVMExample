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
        val bottomsheetOptions: Flow<Pair<List<String>, String?>>
    }

    interface ViewModelType {
        val inputs: Inputs
        val outputs: Outputs
    }

    class ViewModel : androidx.lifecycle.ViewModel(), LifecycleObserver, ViewModelType, Inputs,
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
            _enable1.emit(value)
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
            _didClickButton.emit(Unit)
            _progressBarVisibility.emit(true)
            delay(3000)
            _options2.emit(mutableListOf("1", "2", "3", "4", "5"))
            _progressBarVisibility.emit(false)
        }

        override val enableButton: Flow<Boolean>
            get() = combine(
                _text,
                _enable1,
                _enable2,
                _enable3,
            ) { a, b, c, d ->
                return@combine !a.isNullOrEmpty() && b == true && c == true && d == true
            }

        override val text: Flow<String>
            get() = _text.filterNotNull()
        private val _text: MutableStateFlow<String?> = MutableStateFlow("")
        private val _enable1: MutableStateFlow<Boolean?> = MutableStateFlow(null)
        private val _enable2: MutableStateFlow<Boolean?> = MutableStateFlow(null)
        private val _enable3: MutableStateFlow<Boolean?> = MutableStateFlow(null)

        override val options1: SharedFlow<List<String>>
            get() = _options1
        private val _options1: MutableSharedFlow<List<String>> = MutableSharedFlow()
        override val selectedOption: Flow<String>
            get() = _selectedOption.filterNotNull()
        private val _selectedOption: MutableStateFlow<String?> = MutableStateFlow(null)
        override val bottomsheetOptions: Flow<Pair<List<String>, String?>>
            get() = options2.combine(_selectedOption) { a, b ->
                return@combine Pair(a, b)
            }

        private val _options2: MutableStateFlow<List<String>?> = MutableStateFlow(null)
        override val options2: Flow<List<String>>
            get() = _options2.mapNotNull { it }

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
        private val _didClickButton: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 0)

        protected val error by lazy { MutableLiveData<Exception>() }

        protected val finally by lazy { MutableLiveData<Int>() }

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