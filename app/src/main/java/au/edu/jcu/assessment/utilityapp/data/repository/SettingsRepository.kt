package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the user's settings in memory and shares them between screens.
 * The Settings screen writes to it; the main screen observes [settings] and reacts to changes.
 * One instance lives in the AppContainer, so both screens see the same values.
 */
class SettingsRepository {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    fun setCity(city: City) = _settings.update { it.copy(city = city) }

    fun setTemperatureUnit(unit: TemperatureUnit) =
        _settings.update { it.copy(temperatureUnit = unit) }

    fun setUmbrellaThreshold(percent: Int) =
        _settings.update { it.copy(umbrellaThreshold = percent.coerceIn(0, 100)) }

    fun setColdOffset(degrees: Int) = _settings.update { it.copy(coldOffset = degrees) }

    fun setMetricVisible(metric: Metric, visible: Boolean) = _settings.update {
        val metrics = if (visible) it.visibleMetrics + metric else it.visibleMetrics - metric
        it.copy(visibleMetrics = metrics)
    }
}
