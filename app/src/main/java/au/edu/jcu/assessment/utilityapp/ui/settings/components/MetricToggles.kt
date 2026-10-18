package au.edu.jcu.assessment.utilityapp.ui.settings.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Metric

/** One switch per optional detail on the main screen. */
@Composable
fun MetricToggles(
    visibleMetrics: Set<Metric>,
    onMetricToggled: (Metric, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Metric.entries.forEach { metric ->
            val checked = metric in visibleMetrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // The whole row is the touch target, and screen readers announce it as one switch.
                    .toggleable(
                        value = checked,
                        role = Role.Switch,
                        onValueChange = { onMetricToggled(metric, it) },
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(metric.labelRes),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                // onCheckedChange = null: the row above already handles the tap.
                Switch(checked = checked, onCheckedChange = null)
            }
        }
    }
}

@get:StringRes
private val Metric.labelRes: Int
    get() = when (this) {
        Metric.RAIN -> R.string.metric_rain
        Metric.UV -> R.string.metric_uv
        Metric.WIND -> R.string.metric_wind
        Metric.HUMIDITY -> R.string.metric_humidity
        Metric.SUN_TIMES -> R.string.metric_sun_times
    }
