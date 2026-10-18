package au.edu.jcu.assessment.utilityapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.UvLevel
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.domain.model.Weather
import kotlin.math.roundToInt

private const val TILES_PER_ROW = 3

/** Small detail tiles (rain, UV, wind, humidity), only for the metrics switched on in Settings. */
@Composable
fun MetricTiles(weather: Weather, visibleMetrics: Set<Metric>, modifier: Modifier = Modifier) {
    // Sunrise/sunset has its own wider card (SunTimesCard), so it is not a tile.
    val tiles = Metric.entries.filter { it in visibleMetrics && it != Metric.SUN_TIMES }
    if (tiles.isEmpty()) return

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tiles.chunked(TILES_PER_ROW).forEach { rowMetrics ->
            // IntrinsicSize.Min + fillMaxHeight gives every tile in a row the same height.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowMetrics.forEach { metric ->
                    MetricTile(
                        metric = metric,
                        weather = weather,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                // Keep tiles the same width when the last row is not full.
                repeat(TILES_PER_ROW - rowMetrics.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MetricTile(metric: Metric, weather: Weather, modifier: Modifier = Modifier) {
    val (label, value) = when (metric) {
        Metric.RAIN -> stringResource(R.string.tile_rain) to
            stringResource(R.string.value_percent, weather.precipitationProbability)
        Metric.UV -> {
            val uvNow = weather.uvIndex.roundToInt()
            stringResource(R.string.tile_uv) to
                stringResource(R.string.value_uv, uvNow, uvLevelLabel(UvLevel.fromIndex(uvNow)))
        }
        Metric.WIND -> stringResource(R.string.tile_wind) to
            stringResource(R.string.value_wind_kmh, weather.windSpeedKmh.roundToInt())
        Metric.HUMIDITY -> stringResource(R.string.tile_humidity) to
            stringResource(R.string.value_percent, weather.humidity)
        Metric.SUN_TIMES -> return // Filtered out above; drawn by SunTimesCard.
    }

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun uvLevelLabel(level: UvLevel): String = stringResource(
    when (level) {
        UvLevel.LOW -> R.string.uv_level_low
        UvLevel.MODERATE -> R.string.uv_level_moderate
        UvLevel.HIGH -> R.string.uv_level_high
        UvLevel.VERY_HIGH -> R.string.uv_level_very_high
        UvLevel.EXTREME -> R.string.uv_level_extreme
    }
)
