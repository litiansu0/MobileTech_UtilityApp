package au.edu.jcu.assessment.utilityapp.domain.model

/** A place the user can get weather for. Coordinates come from the Open-Meteo geocoding API. */
data class City(
    val name: String,
    val region: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
) {
    /** Text shown in the UI, e.g. "Brisbane, Queensland, Australia". */
    val displayName: String
        get() = listOfNotNull(name, region, country).distinct().joinToString(", ")

    companion object {
        /** City used when the app starts. */
        val BRISBANE = City(
            name = "Brisbane",
            region = "Queensland",
            country = "Australia",
            latitude = -27.46794,
            longitude = 153.02809,
        )
    }
}
