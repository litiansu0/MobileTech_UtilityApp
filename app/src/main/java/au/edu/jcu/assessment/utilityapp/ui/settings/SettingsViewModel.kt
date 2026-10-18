package au.edu.jcu.assessment.utilityapp.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Backs the Settings screen. Reads and writes the shared [SettingsRepository], so changes made
 * here show up on the main screen straight away, and searches for cities as the user types.
 */
class SettingsViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsRepository.settings

    /**
     * Text in the search box. Kept as Compose state (not a StateFlow) because text fields
     * must be updated immediately, or the cursor can jump while typing.
     */
    var query by mutableStateOf("")
        private set

    private val _searchState = MutableStateFlow<CitySearchState>(CitySearchState.Idle)
    val searchState: StateFlow<CitySearchState> = _searchState.asStateFlow()

    init {
        searchAsUserTypes()
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
    }

    fun onCitySelected(city: City) {
        settingsRepository.setCity(city)
        clearSearch()
    }

    fun clearSearch() {
        query = ""
        _searchState.value = CitySearchState.Idle
    }

    fun onTemperatureUnitSelected(unit: TemperatureUnit) {
        settingsRepository.setTemperatureUnit(unit)
    }

    fun onUmbrellaThresholdChange(percent: Int) {
        settingsRepository.setUmbrellaThreshold(percent)
    }

    fun onColdOffsetChange(degrees: Int) {
        settingsRepository.setColdOffset(degrees)
    }

    fun onMetricToggled(metric: Metric, visible: Boolean) {
        settingsRepository.setMetricVisible(metric, visible)
    }

    /**
     * Waits until the user stops typing for [DEBOUNCE_MS] before calling the API, so typing
     * "Brisbane" sends one request instead of eight. A newer query cancels an older search.
     */
    @OptIn(FlowPreview::class)
    private fun searchAsUserTypes() {
        viewModelScope.launch {
            snapshotFlow { query }
                .map { it.trim() }
                .debounce(DEBOUNCE_MS)
                .distinctUntilChanged()
                .collectLatest { text ->
                    if (text.length < MIN_QUERY_LENGTH) {
                        _searchState.value = CitySearchState.Idle
                        return@collectLatest
                    }
                    _searchState.value = CitySearchState.Loading
                    _searchState.value = try {
                        val cities = weatherRepository.searchCities(text)
                        if (cities.isEmpty()) {
                            CitySearchState.NoResults(text)
                        } else {
                            CitySearchState.Results(cities)
                        }
                    } catch (e: CancellationException) {
                        throw e // The user typed more; this search is no longer needed.
                    } catch (e: Exception) {
                        CitySearchState.Error
                    }
                }
        }
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
        private const val DEBOUNCE_MS = 400L

        /** Builds the ViewModel with repositories from the app's [WeatherApplication.container]. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as WeatherApplication).container
                SettingsViewModel(container.weatherRepository, container.settingsRepository)
            }
        }
    }
}
