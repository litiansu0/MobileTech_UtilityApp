package au.edu.jcu.assessment.utilityapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import au.edu.jcu.assessment.utilityapp.ui.weather.formatTime

/** Today's sunrise and sunset, in the city's local time. */
@Composable
fun SunTimesCard(weather: Weather, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SunTime(emoji = "🌅", label = stringResource(R.string.sun_sunrise), time = weather.sunrise.formatTime())
            SunTime(emoji = "🌇", label = stringResource(R.string.sun_sunset), time = weather.sunset.formatTime())
        }
    }
}

@Composable
private fun SunTime(emoji: String, label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = time, style = MaterialTheme.typography.titleMedium)
    }
}
