package au.edu.jcu.assessment.utilityapp.data.remote

import au.edu.jcu.assessment.utilityapp.data.remote.dto.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** Open-Meteo weather forecast API (https://open-meteo.com). Free, no API key needed. */
interface OpenMeteoApi {

    /**
     * Gets current conditions plus today's daily summary.
     *
     * @param current comma-separated list of current-weather fields to include.
     * @param daily comma-separated list of daily fields to include.
     * @param timezone "auto" returns times in the city's local time zone.
     */
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("current") current: String,
        @Query("daily") daily: String,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 1,
    ): ForecastResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"

        const val CURRENT_FIELDS = "temperature_2m,apparent_temperature,relative_humidity_2m," +
            "weather_code,wind_speed_10m,uv_index,is_day"

        const val DAILY_FIELDS = "temperature_2m_max,temperature_2m_min," +
            "precipitation_probability_max,sunrise,sunset"
    }
}
