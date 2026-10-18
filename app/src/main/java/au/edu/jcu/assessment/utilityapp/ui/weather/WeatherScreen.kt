package au.edu.jcu.assessment.utilityapp.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.OutfitAdvisor
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.ui.weather.components.AdviceCard
import au.edu.jcu.assessment.utilityapp.ui.weather.components.CurrentWeatherCard
import au.edu.jcu.assessment.utilityapp.ui.weather.components.LocationHeader
import java.time.LocalDateTime

/** Main (Utility) tab. Connects the [WeatherViewModel] to the stateless [WeatherContent]. */
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeatherContent(uiState = uiState, onRefresh = viewModel::refresh, modifier = modifier)
}

/** Draws one [WeatherUiState]. Has no ViewModel, so it can be previewed and tested on its own. */
@Composable
fun WeatherContent(
    uiState: WeatherUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        WeatherUiState.Loading -> LoadingState(modifier)
        is WeatherUiState.Error -> ErrorState(uiState.reason, onRetry = onRefresh, modifier)
        is WeatherUiState.Success -> WeatherDetails(uiState, onRefresh, modifier)
    }
}

@Composable
private fun WeatherDetails(
    state: WeatherUiState.Success,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LocationHeader(
            cityName = state.city.name,
            updatedAt = state.weather.updatedAt.formatTime(),
            onRefresh = onRefresh,
        )
        CurrentWeatherCard(weather = state.weather)
        AdviceCard(advice = state.advice)
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.weather_loading),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun ErrorState(reason: ErrorReason, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val message = when (reason) {
        ErrorReason.NETWORK -> stringResource(R.string.error_network)
        ErrorReason.SERVER -> stringResource(R.string.error_server)
    }
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "⚠️", style = MaterialTheme.typography.displayMedium)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

// ---- Previews (sample data only, no network) ----

private val previewWeather = Weather(
    temperature = 21.4,
    apparentTemperature = 21.1,
    maxTemperature = 21.8,
    minTemperature = 14.7,
    unit = TemperatureUnit.CELSIUS,
    weatherCode = 53,
    isDay = true,
    precipitationProbability = 73,
    uvIndex = 7.95,
    uvIndexMax = 8.05,
    windSpeedKmh = 13.9,
    humidity = 56,
    sunrise = LocalDateTime.of(2026, 10, 18, 5, 9),
    sunset = LocalDateTime.of(2026, 10, 18, 17, 57),
    updatedAt = LocalDateTime.of(2026, 10, 18, 10, 15),
)

@Preview(showBackground = true, name = "Success")
@Composable
private fun WeatherSuccessPreview() {
    MobileTech_UtilityAppTheme {
        WeatherContent(
            uiState = WeatherUiState.Success(
                city = City.BRISBANE,
                weather = previewWeather,
                advice = OutfitAdvisor.advise(previewWeather, UserSettings()),
            ),
            onRefresh = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun WeatherLoadingPreview() {
    MobileTech_UtilityAppTheme {
        WeatherContent(WeatherUiState.Loading, onRefresh = {})
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun WeatherErrorPreview() {
    MobileTech_UtilityAppTheme {
        WeatherContent(WeatherUiState.Error(ErrorReason.NETWORK), onRefresh = {})
    }
}
