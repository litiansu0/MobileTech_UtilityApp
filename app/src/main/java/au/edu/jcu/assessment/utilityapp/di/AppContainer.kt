package au.edu.jcu.assessment.utilityapp.di

import au.edu.jcu.assessment.utilityapp.data.remote.GeocodingApi
import au.edu.jcu.assessment.utilityapp.data.remote.OpenMeteoApi
import au.edu.jcu.assessment.utilityapp.data.repository.NetworkWeatherRepository
import au.edu.jcu.assessment.utilityapp.data.repository.SettingsRepository
import au.edu.jcu.assessment.utilityapp.data.repository.WeatherRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Manual dependency injection: the app's shared objects are created here once and handed to
 * ViewModels through their factories, instead of each class building its own dependencies.
 */
interface AppContainer {
    val weatherRepository: WeatherRepository
    val settingsRepository: SettingsRepository
}

class DefaultAppContainer : AppContainer {

    // The forecast and geocoding APIs live on different hosts, so each gets its own Retrofit.
    private val openMeteoApi: OpenMeteoApi by lazy {
        buildRetrofit(OpenMeteoApi.BASE_URL).create(OpenMeteoApi::class.java)
    }

    private val geocodingApi: GeocodingApi by lazy {
        buildRetrofit(GeocodingApi.BASE_URL).create(GeocodingApi::class.java)
    }

    override val weatherRepository: WeatherRepository by lazy {
        NetworkWeatherRepository(openMeteoApi, geocodingApi)
    }

    override val settingsRepository: SettingsRepository by lazy { SettingsRepository() }

    private fun buildRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
