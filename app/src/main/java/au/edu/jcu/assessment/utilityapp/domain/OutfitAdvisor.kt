package au.edu.jcu.assessment.utilityapp.domain

import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import kotlin.math.roundToInt

/** Today's advice, ready for the UI to turn into text. */
data class OutfitAdvice(
    val umbrella: UmbrellaAdvice,
    /** Today's highest UV index, rounded, shown alongside [uvLevel]. */
    val uvIndexMax: Int,
    val uvLevel: UvLevel,
    val clothing: ClothingLevel,
    /** Current wind speed in km/h when it is strong enough to mention, otherwise null. */
    val strongWindKmh: Int?,
)

/** Whether to bring an umbrella, and why. */
data class UmbrellaAdvice(val reason: UmbrellaReason, val rainChance: Int) {
    val needed: Boolean get() = reason != UmbrellaReason.NOT_NEEDED
}

enum class UmbrellaReason { RAINING_NOW, LIKELY_RAIN, NOT_NEEDED }

/** UV risk bands from the WHO UV index scale. */
enum class UvLevel(val minIndex: Int) {
    LOW(0),
    MODERATE(3),
    HIGH(6),
    VERY_HIGH(8),
    EXTREME(11);

    companion object {
        fun fromIndex(index: Int): UvLevel =
            entries.last { index.coerceAtLeast(0) >= it.minIndex }
    }
}

/** What to wear, from the warmest weather to the coldest. */
enum class ClothingLevel(val minFeelsLikeCelsius: Int) {
    HOT(28),
    WARM(22),
    MILD(16),
    COOL(10),
    COLD(0),
    FREEZING(Int.MIN_VALUE);

    companion object {
        fun fromFeelsLike(celsius: Double): ClothingLevel =
            entries.first { celsius >= it.minFeelsLikeCelsius }
    }
}

/**
 * Turns raw weather plus the user's preferences into simple "what to take / wear" advice.
 * A pure function with no Android code, so it is easy to unit test.
 */
object OutfitAdvisor {

    /** Wind at or above this speed (km/h) is worth a warning: umbrellas struggle and it feels colder. */
    const val STRONG_WIND_KMH = 30

    fun advise(weather: Weather, settings: UserSettings): OutfitAdvice {
        val uvIndexMax = weather.uvIndexMax.roundToInt()
        val wind = weather.windSpeedKmh.roundToInt()
        return OutfitAdvice(
            umbrella = umbrellaAdvice(weather, settings.umbrellaThreshold),
            uvIndexMax = uvIndexMax,
            uvLevel = UvLevel.fromIndex(uvIndexMax),
            clothing = clothingLevel(weather, settings.coldOffset),
            strongWindKmh = wind.takeIf { it >= STRONG_WIND_KMH },
        )
    }

    private fun umbrellaAdvice(weather: Weather, threshold: Int): UmbrellaAdvice {
        val chance = weather.precipitationProbability
        val reason = when {
            WeatherCondition.fromCode(weather.weatherCode).isRainy -> UmbrellaReason.RAINING_NOW
            chance >= threshold -> UmbrellaReason.LIKELY_RAIN
            else -> UmbrellaReason.NOT_NEEDED
        }
        return UmbrellaAdvice(reason, chance)
    }

    /**
     * Uses the feels-like temperature (it already includes wind and humidity), converted to °C
     * so the bands work in either unit. [coldOffset] makes it "feel" colder for people who
     * feel the cold, e.g. an offset of 3 treats 20°C like 17°C.
     */
    private fun clothingLevel(weather: Weather, coldOffset: Int): ClothingLevel {
        val feelsLikeCelsius = when (weather.unit) {
            TemperatureUnit.CELSIUS -> weather.apparentTemperature
            TemperatureUnit.FAHRENHEIT -> (weather.apparentTemperature - 32) * 5 / 9
        }
        return ClothingLevel.fromFeelsLike(feelsLikeCelsius - coldOffset)
    }
}
