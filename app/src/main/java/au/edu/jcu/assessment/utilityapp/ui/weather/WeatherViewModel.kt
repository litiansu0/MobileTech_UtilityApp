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
import au.edu.jcu.assessment.utilityapp.domain.OutfitAdvisor
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Loads the weather for the city chosen in Settings and exposes it, with today's advice,
 * as [uiState].
 *
 * - City or temperature unit changes → download the weather again.
 * - Any other setting change (umbrella threshold, cold offset, visible tiles) → only rebuild
 *   the UI state from the weather already downloaded.
 */
class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    /** Result of the latest download, before advice is added. */
    private sealed interface LoadState {
        data object Loading : LoadState
        data class Loaded(
            val city: City,
            val weather: Weather,
            val isRefreshing: Boolean = false,
            /** Set when a refresh failed; [weather] is then the older data still on screen. */
            val refreshError: ErrorReason? = null,
        ) : LoadState
        data class Failed(val reason: ErrorReason) : LoadState
    }

    private val loadState = MutableStateFlow<LoadState>(LoadState.Loading)

    // The request in progress, so a newer request can cancel an older one.
    private var loadJob: Job? = null

    val uiState: StateFlow<WeatherUiState> =
        combine(loadState, settingsRepository.settings) { load, settings ->
            when (load) {
                LoadState.Loading -> WeatherUiState.Loading
                is LoadState.Failed -> WeatherUiState.Error(load.reason)
                is LoadState.Loaded -> WeatherUiState.Success(
                    city = load.city,
                    weather = load.weather,
                    advice = OutfitAdvisor.advise(load.weather, settings),
                    visibleMetrics = settings.visibleMetrics,
                    isRefreshing = load.isRefreshing,
                    refreshError = load.refreshError,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            // Keep running for 5 s after the screen goes away, e.g. during rotation.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherUiState.Loading,
        )

    init {
        viewModelScope.launch {
            settingsRepository.settings
                .map { it.city to it.temperatureUnit }
                .distinctUntilChanged()
                .collect { (city, unit) -> loadWeather(city, unit) }
        }
    }

    /** Called by pull-to-refresh and the refresh and retry buttons. */
    fun refresh() {
        val settings = settingsRepository.settings.value
        loadWeather(settings.city, settings.temperatureUnit)
    }

    private fun loadWeather(city: City, unit: TemperatureUnit) {
        loadJob?.cancel()

        // Refreshing the same city and unit keeps the current weather on screen while the new
        // data loads. A different city or unit shows the full loading screen instead, so old
        // data is never shown under the wrong name or unit.
        val shown = (loadState.value as? LoadState.Loaded)
            ?.takeIf { it.city == city && it.weather.unit == unit }
        loadState.value = shown?.copy(isRefreshing = true, refreshError = null) ?: LoadState.Loading

        loadJob = viewModelScope.launch {
            loadState.value = try {
                LoadState.Loaded(city, weatherRepository.getWeather(city, unit))
            } catch (e: CancellationException) {
                throw e // A newer request replaced this one; let the coroutine stop normally.
            } catch (e: Exception) {
                // IOException = no connection; anything else = HTTP error or unexpected JSON.
                val reason = if (e is IOException) ErrorReason.NETWORK else ErrorReason.SERVER
                // A failed refresh keeps the older weather visible and just reports the problem.
                shown?.copy(isRefreshing = false, refreshError = reason) ?: LoadState.Failed(reason)
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
