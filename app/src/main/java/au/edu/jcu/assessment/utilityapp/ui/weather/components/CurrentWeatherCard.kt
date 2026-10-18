package au.edu.jcu.assessment.utilityapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.WeatherCondition
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import au.edu.jcu.assessment.utilityapp.ui.weather.formatTemperature

/** Large "at a glance" card: condition icon, current temperature, feels-like and today's range. */
@Composable
fun CurrentWeatherCard(weather: Weather, modifier: Modifier = Modifier) {
    val condition = WeatherCondition.fromCode(weather.weatherCode)
    val unit = weather.unit

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = condition.emoji(weather.isDay), fontSize = 64.sp)
            Text(
                text = formatTemperature(weather.temperature, unit),
                style = MaterialTheme.typography.displayLarge,
            )
            Text(text = condition.description, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(
                    R.string.weather_feels_like,
                    formatTemperature(weather.apparentTemperature, unit),
                ),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(
                    R.string.weather_high_low,
                    formatTemperature(weather.maxTemperature, unit),
                    formatTemperature(weather.minTemperature, unit),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
