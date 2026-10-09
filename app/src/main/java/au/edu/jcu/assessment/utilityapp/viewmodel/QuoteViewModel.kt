package au.edu.jcu.assessment.utilityapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuoteViewModel : ViewModel() {
    private val _quote = MutableStateFlow("Click button to load quote")
    val quote: StateFlow<String> = _quote

    fun loadQuote() {
        viewModelScope.launch {
            try {
                val quotes = RetrofitInstance.api.getQuotes()
                if (quotes.isNotEmpty()) {
                    val randomQuote = quotes.random()
                    _quote.value = "\"${randomQuote.q}\" — ${randomQuote.a}"
                }
            } catch (e: Exception) {
                _quote.value = "Error: ${e.message}"
            }
        }
    }
}
