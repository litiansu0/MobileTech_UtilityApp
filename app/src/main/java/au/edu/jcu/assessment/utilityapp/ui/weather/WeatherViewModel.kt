package au.edu.jcu.assessment.utilityapp.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import au.edu.jcu.assessment.utilityapp.WeatherApplication
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.data.repository.WeatherRepository
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Loads the weather for the city chosen in Settings and exposes it as [uiState].
 * Reloads automatically whenever the city or temperature unit changes.
 */
class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // The request in progress, so a newer request can cancel an older one.
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.settings
                // Only these two settings change what the API returns; other settings
                // (thresholds, visible tiles) must not trigger a network call.
                .map { it.city to it.temperatureUnit }
                .distinctUntilChanged()
                .collect { (city, unit) -> loadWeather(city, unit) }
        }
    }

    /** Called by the refresh and retry buttons. */
    fun refresh() {
        val settings = settingsRepository.settings.value
        loadWeather(settings.city, settings.temperatureUnit)
    }

    private fun loadWeather(city: City, unit: TemperatureUnit) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            _uiState.value = try {
                WeatherUiState.Success(city, weatherRepository.getWeather(city, unit))
            } catch (e: CancellationException) {
                throw e // A newer request replaced this one; let the coroutine stop normally.
            } catch (e: IOException) {
                WeatherUiState.Error(ErrorReason.NETWORK)
            } catch (e: Exception) {
                // HTTP errors (HttpException) or unexpected JSON.
                WeatherUiState.Error(ErrorReason.SERVER)
            }
        }
    }

    companion object {
        /** Builds the ViewModel with repositories from the app's [WeatherApplication.container]. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as WeatherApplication).container
                WeatherViewModel(container.weatherRepository, container.settingsRepository)
            }
        }
    }
}
