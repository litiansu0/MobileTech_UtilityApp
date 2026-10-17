package au.edu.jcu.assessment.utilityapp.viewmodel;

// CounterViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//ViewModel: store and manage UI-related data that survives configuration changes, such as screen rotations
class CounterViewModel : ViewModel() {
    //MutableStateFlow: manage state changes in a reactive way.
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count
    fun increment() {
        _count.value++
    }
}
