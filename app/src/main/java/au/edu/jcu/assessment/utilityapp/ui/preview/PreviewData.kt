package au.edu.jcu.assessment.utilityapp.ui.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import au.edu.jcu.assessment.utilityapp.domain.OutfitAdvisor
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.ui.weather.WeatherUiState
import java.time.LocalDateTime

/** Sample data shared by all @Preview functions, so previews never need the network. */
internal object PreviewData {

    /** A real Brisbane reading: drizzly, mild, high UV later in the day. */
    val weather = Weather(
        temperature = 21.4,
        apparentTemperature = 21.1,
        maxTemperature = 21.8,
        minTemperature = 14.7,
        unit = TemperatureUnit.CELSIUS,
        weatherCode = 53,
        isDay = true,
        precipitationProbability = 73,
        uvIndex = 7.95,
        uvIndexMax = 8.05,
        windSpeedKmh = 13.9,
        humidity = 56,
        sunrise = LocalDateTime.of(2026, 10, 18, 5, 9),
        sunset = LocalDateTime.of(2026, 10, 18, 17, 57),
        updatedAt = LocalDateTime.of(2026, 10, 18, 10, 15),
    )

    /** A windy day, so the optional wind line in the advice card shows up. */
    val windyWeather = weather.copy(windSpeedKmh = 35.0)

    val success = WeatherUiState.Success(
        city = City.BRISBANE,
        weather = weather,
        advice = OutfitAdvisor.advise(weather, UserSettings()),
        visibleMetrics = Metric.entries.toSet(),
    )
}

/**
 * App theme plus a [Surface], so previews get the theme's background colour.
 * Without it, dark-mode previews would draw light text on a white background.
 */
@Composable
internal fun PreviewSurface(content: @Composable () -> Unit) {
    MobileTech_UtilityAppTheme {
        Surface(content = content)
    }
}
