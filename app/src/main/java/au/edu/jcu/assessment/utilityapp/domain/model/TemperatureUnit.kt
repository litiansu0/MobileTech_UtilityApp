package au.edu.jcu.assessment.utilityapp.domain.model

/**
 * Temperature unit chosen in Settings.
 *
 * @property apiValue value sent to Open-Meteo's `temperature_unit` query parameter.
 * @property symbol suffix shown after temperatures in the UI.
 */
enum class TemperatureUnit(val apiValue: String, val symbol: String) {
    CELSIUS("celsius", "°C"),
    FAHRENHEIT("fahrenheit", "°F"),
}
