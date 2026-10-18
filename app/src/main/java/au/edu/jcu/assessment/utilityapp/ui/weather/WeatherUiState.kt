package au.edu.jcu.assessment.utilityapp.ui.weather

import au.edu.jcu.assessment.utilityapp.domain.OutfitAdvice
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.Weather

/** Everything the main screen can be showing at one moment. */
sealed interface WeatherUiState {

    data object Loading : WeatherUiState

    data class Success(
        val city: City,
        val weather: Weather,
        val advice: OutfitAdvice,
        /** Detail tiles the user chose to show in Settings. */
        val visibleMetrics: Set<Metric>,
    ) : WeatherUiState

    data class Error(val reason: ErrorReason) : WeatherUiState
}

/** Why loading failed. The screen turns this into a message the user can understand. */
enum class ErrorReason {
    /** No connection, timeout, DNS failure, etc. */
    NETWORK,

    /** The server replied with an error or data the app could not read. */
    SERVER,
}
