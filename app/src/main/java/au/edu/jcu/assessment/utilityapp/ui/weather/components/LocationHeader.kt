package au.edu.jcu.assessment.utilityapp.ui.weather.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.ui.preview.PreviewSurface

/**
 * Top row of the main screen: city name, last update time and a refresh button.
 *
 * @param refreshError shown under the update time when the last refresh failed, else null.
 */
@Composable
fun LocationHeader(
    cityName: String,
    updatedAt: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshError: String? = null,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null, // Decorative; the city name is read out instead.
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = cityName, style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(R.string.weather_updated_at, updatedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (refreshError != null) {
                Text(
                    text = refreshError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.weather_refresh),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LocationHeaderPreview() {
    PreviewSurface {
        LocationHeader(cityName = "Brisbane", updatedAt = "10:15", onRefresh = {})
    }
}

@PreviewLightDark
@Composable
private fun LocationHeaderRefreshFailedPreview() {
    PreviewSurface {
        LocationHeader(
            cityName = "Brisbane",
            updatedAt = "10:15",
            onRefresh = {},
            refreshError = "Couldn't refresh – no internet connection",
        )
    }
}
