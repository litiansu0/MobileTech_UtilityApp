package au.edu.jcu.assessment.utilityapp.data.repository

import au.edu.jcu.assessment.utilityapp.data.remote.GeocodingApi
import au.edu.jcu.assessment.utilityapp.data.remote.OpenMeteoApi
import au.edu.jcu.assessment.utilityapp.data.remote.dto.ForecastResponse
import au.edu.jcu.assessment.utilityapp.data.remote.dto.GeocodingResultDto
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import java.time.LocalDateTime

/**
 * Single source of weather and city data for the app. ViewModels depend on this interface,
 * not on Retrofit, so a fake can be swapped in for tests.
 * Network errors are thrown to the caller, which decides how to show them.
 */
interface WeatherRepository {
    suspend fun getWeather(city: City, unit: TemperatureUnit): Weather
    suspend fun searchCities(query: String): List<City>
}

/** [WeatherRepository] backed by the Open-Meteo web APIs. */
class NetworkWeatherRepository(
    private val openMeteoApi: OpenMeteoApi,
    private val geocodingApi: GeocodingApi,
) : WeatherRepository {

    override suspend fun getWeather(city: City, unit: TemperatureUnit): Weather =
        openMeteoApi.getForecast(
            latitude = city.latitude,
            longitude = city.longitude,
            temperatureUnit = unit.apiValue,
            current = OpenMeteoApi.CURRENT_FIELDS,
            daily = OpenMeteoApi.DAILY_FIELDS,
        ).toWeather(unit)

    override suspend fun searchCities(query: String): List<City> {
        if (query.isBlank()) return emptyList()
        return geocodingApi.searchCities(query.trim()).results.orEmpty().map { it.toCity() }
    }
}

/** Converts the API's JSON shape into the app's [Weather] model. */
private fun ForecastResponse.toWeather(unit: TemperatureUnit): Weather = Weather(
    temperature = current.temperature,
    apparentTemperature = current.apparentTemperature,
    // Daily arrays hold one entry because the request asks for forecast_days=1.
    maxTemperature = daily.maxTemperature.first(),
    minTemperature = daily.minTemperature.first(),
    unit = unit,
    weatherCode = current.weatherCode,
    isDay = current.isDay == 1,
    precipitationProbability = daily.precipitationProbabilityMax.firstOrNull() ?: 0,
    uvIndex = current.uvIndex,
    windSpeedKmh = current.windSpeed,
    humidity = current.humidity,
    // Open-Meteo returns local ISO times without an offset, e.g. "2026-10-18T05:09".
    sunrise = LocalDateTime.parse(daily.sunrise.first()),
    sunset = LocalDateTime.parse(daily.sunset.first()),
    updatedAt = LocalDateTime.parse(current.time),
)

private fun GeocodingResultDto.toCity(): City = City(
    name = name,
    region = admin1,
    country = country,
    latitude = latitude,
    longitude = longitude,
)
