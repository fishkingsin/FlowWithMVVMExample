import com.example.flowwithmvvmexample.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test

class MainViewModelTests {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    var sut = Main.ViewModel()
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_bottomsheetOptions() = runBlocking {

        val values = mutableListOf<Pair<List<String>, String?>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            sut.outputs.bottomsheetOptions.toList(values)
        }


        sut.inputs.setOption1("1")
        sut.inputs.enable1(true)
        sut.inputs.enable1(true)
        sut.inputs.enable1(true)
        sut.inputs.onButtonClick()
        delay(3000)
        assert(values.isNotEmpty()) {
            println("values $values")
        }

        collectJob.cancel()
    }
}