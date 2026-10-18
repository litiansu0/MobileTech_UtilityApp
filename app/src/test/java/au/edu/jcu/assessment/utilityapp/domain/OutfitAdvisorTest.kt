package au.edu.jcu.assessment.utilityapp.domain

import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OutfitAdvisorTest {

    private val defaultSettings = UserSettings() // umbrella threshold 40 %, cold offset 0

    // ---- Umbrella ----

    @Test
    fun umbrella_notNeeded_whenChanceBelowThreshold() {
        val advice = advise(weather(rainChance = 39))
        assertEquals(UmbrellaReason.NOT_NEEDED, advice.umbrella.reason)
        assertFalse(advice.umbrella.needed)
    }

    @Test
    fun umbrella_needed_whenChanceEqualsThreshold() {
        val advice = advise(weather(rainChance = 40))
        assertEquals(UmbrellaReason.LIKELY_RAIN, advice.umbrella.reason)
        assertTrue(advice.umbrella.needed)
    }

    @Test
    fun umbrella_followsUserThreshold() {
        val weather = weather(rainChance = 30)
        assertFalse(advise(weather).umbrella.needed)
        assertTrue(advise(weather, defaultSettings.copy(umbrellaThreshold = 20)).umbrella.needed)
    }

    @Test
    fun umbrella_needed_whenRainingNow_evenIfChanceIsLow() {
        val advice = advise(weather(weatherCode = 61, rainChance = 5)) // 61 = light rain
        assertEquals(UmbrellaReason.RAINING_NOW, advice.umbrella.reason)
        assertEquals(5, advice.umbrella.rainChance)
    }

    // ---- UV ----

    @Test
    fun uvLevel_usesWhoBands() {
        assertEquals(UvLevel.LOW, UvLevel.fromIndex(2))
        assertEquals(UvLevel.MODERATE, UvLevel.fromIndex(3))
        assertEquals(UvLevel.MODERATE, UvLevel.fromIndex(5))
        assertEquals(UvLevel.HIGH, UvLevel.fromIndex(6))
        assertEquals(UvLevel.VERY_HIGH, UvLevel.fromIndex(8))
        assertEquals(UvLevel.EXTREME, UvLevel.fromIndex(11))
        assertEquals(UvLevel.LOW, UvLevel.fromIndex(-1))
    }

    @Test
    fun uvAdvice_usesTodaysMaximum_notCurrentReading() {
        // Early morning: UV is 1 now but will reach 8 at midday.
        val advice = advise(weather(uvNow = 1.0, uvMax = 8.05))
        assertEquals(8, advice.uvIndexMax)
        assertEquals(UvLevel.VERY_HIGH, advice.uvLevel)
    }

    // ---- Clothing ----

    @Test
    fun clothing_bandsByFeelsLikeCelsius() {
        assertEquals(ClothingLevel.HOT, advise(weather(feelsLike = 30.0)).clothing)
        assertEquals(ClothingLevel.WARM, advise(weather(feelsLike = 22.0)).clothing)
        assertEquals(ClothingLevel.MILD, advise(weather(feelsLike = 21.9)).clothing)
        assertEquals(ClothingLevel.COOL, advise(weather(feelsLike = 12.0)).clothing)
        assertEquals(ClothingLevel.COLD, advise(weather(feelsLike = 0.0)).clothing)
        assertEquals(ClothingLevel.FREEZING, advise(weather(feelsLike = -5.0)).clothing)
    }

    @Test
    fun clothing_coldOffsetMakesItFeelColder() {
        val weather = weather(feelsLike = 23.0) // WARM normally
        val advice = advise(weather, defaultSettings.copy(coldOffset = 3)) // treated as 20°C
        assertEquals(ClothingLevel.MILD, advice.clothing)
    }

    @Test
    fun clothing_convertsFahrenheitBeforeChoosingBand() {
        // 77°F = 25°C, so the band must be WARM, not HOT (as 77 read as °C would be).
        val advice = advise(weather(feelsLike = 77.0, unit = TemperatureUnit.FAHRENHEIT))
        assertEquals(ClothingLevel.WARM, advice.clothing)
    }

    // ---- Wind ----

    @Test
    fun wind_onlyMentionedWhenStrong() {
        assertNull(advise(weather(windKmh = 29.0)).strongWindKmh)
        assertEquals(30, advise(weather(windKmh = 30.0)).strongWindKmh)
    }

    // ---- Helpers ----

    private fun advise(weather: Weather, settings: UserSettings = defaultSettings) =
        OutfitAdvisor.advise(weather, settings)

    /** A mild, dry, calm day; each test overrides only the values it cares about. */
    private fun weather(
        feelsLike: Double = 20.0,
        unit: TemperatureUnit = TemperatureUnit.CELSIUS,
        weatherCode: Int = 1,
        rainChance: Int = 0,
        uvNow: Double = 3.0,
        uvMax: Double = 3.0,
        windKmh: Double = 10.0,
    ) = Weather(
        temperature = feelsLike,
        apparentTemperature = feelsLike,
        maxTemperature = feelsLike + 3,
        minTemperature = feelsLike - 5,
        unit = unit,
        weatherCode = weatherCode,
        isDay = true,
        precipitationProbability = rainChance,
        uvIndex = uvNow,
        uvIndexMax = uvMax,
        windSpeedKmh = windKmh,
        humidity = 50,
        sunrise = LocalDateTime.of(2026, 10, 18, 5, 9),
        sunset = LocalDateTime.of(2026, 10, 18, 17, 57),
        updatedAt = LocalDateTime.of(2026, 10, 18, 10, 15),
    )
}
