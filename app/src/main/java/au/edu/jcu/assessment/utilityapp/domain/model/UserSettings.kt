package au.edu.jcu.assessment.utilityapp.domain.model

/** Optional detail tiles the user can show or hide on the main screen. */
enum class Metric {
    RAIN,
    UV,
    WIND,
    HUMIDITY,
    SUN_TIMES,
}

/**
 * Everything the Settings screen controls. Kept in memory only (the assignment
 * does not require settings to persist).
 */
data class UserSettings(
    val city: City = City.BRISBANE,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    /** Suggest an umbrella when the chance of rain is at least this many percent. */
    val umbrellaThreshold: Int = 40,
    /** Degrees (°C) added to how cold it feels, for people who feel the cold more. */
    val coldOffset: Int = 0,
    val visibleMetrics: Set<Metric> = setOf(Metric.RAIN, Metric.UV, Metric.WIND),
)
