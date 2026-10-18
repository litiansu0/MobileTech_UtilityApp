package au.edu.jcu.assessment.utilityapp.domain

/**
 * Human-readable weather condition for a WMO weather code, as returned by Open-Meteo.
 * Code table: https://open-meteo.com/en/docs (section "WMO Weather interpretation codes").
 */
enum class WeatherCondition(
    val description: String,
    private val dayEmoji: String,
    private val nightEmoji: String = dayEmoji,
) {
    CLEAR("Clear sky", "☀️", "🌙"),
    MAINLY_CLEAR("Mainly clear", "🌤️", "🌙"),
    PARTLY_CLOUDY("Partly cloudy", "⛅", "☁️"),
    OVERCAST("Overcast", "☁️"),
    FOG("Fog", "🌫️"),
    DRIZZLE("Drizzle", "🌦️", "🌧️"),
    FREEZING_DRIZZLE("Freezing drizzle", "🌧️"),
    RAIN("Rain", "🌧️"),
    FREEZING_RAIN("Freezing rain", "🌧️"),
    SNOW("Snow", "❄️"),
    RAIN_SHOWERS("Rain showers", "🌦️", "🌧️"),
    SNOW_SHOWERS("Snow showers", "🌨️"),
    THUNDERSTORM("Thunderstorm", "⛈️"),
    UNKNOWN("Unknown", "🌡️");

    /** Emoji icon for the condition; some conditions use a different icon at night. */
    fun emoji(isDay: Boolean): String = if (isDay) dayEmoji else nightEmoji

    companion object {
        fun fromCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1 -> MAINLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            51, 53, 55 -> DRIZZLE
            56, 57 -> FREEZING_DRIZZLE
            61, 63, 65 -> RAIN
            66, 67 -> FREEZING_RAIN
            71, 73, 75, 77 -> SNOW
            80, 81, 82 -> RAIN_SHOWERS
            85, 86 -> SNOW_SHOWERS
            95, 96, 99 -> THUNDERSTORM
            else -> UNKNOWN
        }
    }
}
