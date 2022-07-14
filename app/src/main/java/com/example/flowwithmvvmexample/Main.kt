package com.example.flowwithmvvmexample

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
        fun getOptions2(): Job
        fun onButtonClick(): Job
    }

    interface Outputs {
        val text: Flow<String>
        val options1: SharedFlow<List<String>>
        val selectedOption: Flow<String>
        val options2: Flow<List<String>>
        val didClickButton: SharedFlow<Unit>
    }

    interface ViewModelType {
        val inputs: Inputs
        val outputs: Outputs
    }

    class ViewModel: androidx.lifecycle.ViewModel(), LifecycleObserver, ViewModelType, Inputs, Outputs {
        override val inputs: Inputs
            get() = this
        override val outputs: Outputs
            get() = this


        override fun setText(text: CharSequence): Job = launchUI{
            _text.emit(text.toString())
        }

        override fun setOption1(option: String) = launchUI {
            _selectedOption.emit(option)
        }

        override fun getOptions2() = launchUI {

        }

        override fun onButtonClick(): Job = launchUI {
            _didClikcButton.emit(Unit)
            _options2.emit(mutableListOf("1", "2", "3", "4", "5"))
        }

        override val text: Flow<String>
            get() = _text.filterNotNull()
        private val _text: MutableStateFlow<String?> = MutableStateFlow("")

        override val options1: SharedFlow<List<String>>
            get() = _options1
        private val _options1: MutableSharedFlow<List<String>> = MutableSharedFlow()
        override val selectedOption: Flow<String>
            get() = _selectedOption.filterNotNull()
        private val _selectedOption: MutableStateFlow<String?> = MutableStateFlow(null)
        private val _options2: MutableStateFlow<List<String>?> = MutableStateFlow(null)
        override val options2: Flow<List<String>>
            get() = _options2.mapNotNull { it }

        override val didClickButton: SharedFlow<Unit>
            get() = _didClikcButton
        private val _didClikcButton: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 0)

        protected val error by lazy { MutableLiveData<Exception>() }

        protected val finally by lazy { MutableLiveData<Int>() }

        private fun launchUI(block: suspend CoroutineScope.() -> Unit) : Job = viewModelScope.async {
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