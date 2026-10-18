package au.edu.jcu.assessment.utilityapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/** JSON returned by Open-Meteo `/v1/forecast`. Only the fields the app requests are mapped. */
data class ForecastResponse(
    val current: CurrentDto,
    val daily: DailyDto,
)

data class CurrentDto(
    /** Local time of the reading, e.g. "2026-10-18T10:15". */
    val time: String,
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("uv_index") val uvIndex: Double,
    /** 1 during daylight, 0 at night. */
    @SerializedName("is_day") val isDay: Int,
)

/** Daily values come back as arrays with one entry per day; the app asks for one day only. */
data class DailyDto(
    @SerializedName("temperature_2m_max") val maxTemperature: List<Double>,
    @SerializedName("temperature_2m_min") val minTemperature: List<Double>,
    // Can be null for some locations, so the element type is nullable.
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?>,
    val sunrise: List<String>,
    val sunset: List<String>,
)
