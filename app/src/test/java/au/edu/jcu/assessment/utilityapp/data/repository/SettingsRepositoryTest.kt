package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {

    private val repository = SettingsRepository()
    private val settings get() = repository.settings.value

    @Test
    fun startsWithBrisbaneInCelsius() {
        assertEquals(City.BRISBANE, settings.city)
        assertEquals(TemperatureUnit.CELSIUS, settings.temperatureUnit)
    }

    @Test
    fun umbrellaThreshold_isKeptWithin0To100() {
        repository.setUmbrellaThreshold(150)
        assertEquals(100, settings.umbrellaThreshold)
        repository.setUmbrellaThreshold(-10)
        assertEquals(0, settings.umbrellaThreshold)
        repository.setUmbrellaThreshold(60)
        assertEquals(60, settings.umbrellaThreshold)
    }

    @Test
    fun coldOffset_isKeptWithinAllowedRange() {
        repository.setColdOffset(9)
        assertEquals(5, settings.coldOffset)
        repository.setColdOffset(-9)
        assertEquals(-5, settings.coldOffset)
    }

    @Test
    fun metricVisibility_canBeToggledOffAndOn() {
        assertTrue(Metric.RAIN in settings.visibleMetrics)

        repository.setMetricVisible(Metric.RAIN, false)
        assertFalse(Metric.RAIN in settings.visibleMetrics)

        repository.setMetricVisible(Metric.SUN_TIMES, true)
        assertTrue(Metric.SUN_TIMES in settings.visibleMetrics)
    }

    @Test
    fun changingOneSetting_leavesTheOthersAlone() {
        repository.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        repository.setColdOffset(2)
        assertEquals(TemperatureUnit.FAHRENHEIT, settings.temperatureUnit)
        assertEquals(2, settings.coldOffset)
        assertEquals(City.BRISBANE, settings.city)
    }
}
