package au.edu.jcu.assessment.utilityapp.domain.model

import java.time.LocalDateTime

/**
 * Today's weather for one city, already converted from the API's JSON into app-friendly types.
 * Temperatures are in [unit]; times are local to the city.
 */
data class Weather(
    val temperature: Double,
    val apparentTemperature: Double,
    val maxTemperature: Double,
    val minTemperature: Double,
    val unit: TemperatureUnit,
    /** WMO weather code, e.g. 0 = clear sky, 61 = light rain. */
    val weatherCode: Int,
    val isDay: Boolean,
    /** Highest chance of rain today, 0–100 %. */
    val precipitationProbability: Int,
    /** UV index right now. */
    val uvIndex: Double,
    /** Highest UV index expected today; used for sun-protection advice. */
    val uvIndexMax: Double,
    val windSpeedKmh: Double,
    val humidity: Int,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    /** Time of the reading, in the city's local time. */
    val updatedAt: LocalDateTime,
)
