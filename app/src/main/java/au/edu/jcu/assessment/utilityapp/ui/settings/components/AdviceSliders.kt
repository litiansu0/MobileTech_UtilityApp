package au.edu.jcu.assessment.utilityapp.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import kotlin.math.abs
import kotlin.math.roundToInt

private const val UMBRELLA_STEP_PERCENT = 10

/** Rain chance (%) at which the main screen starts suggesting an umbrella. */
@Composable
fun UmbrellaThresholdSlider(
    percent: Int,
    onPercentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    IntSlider(
        label = stringResource(R.string.settings_umbrella_threshold, percent),
        value = percent,
        range = UserSettings.UMBRELLA_THRESHOLD_RANGE,
        step = UMBRELLA_STEP_PERCENT,
        onValueChange = onPercentChange,
        modifier = modifier,
    )
}

/** Personal "I feel the cold / heat" adjustment for the clothing advice. */
@Composable
fun ColdOffsetSlider(
    degrees: Int,
    onDegreesChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = when {
        degrees > 0 -> stringResource(R.string.settings_cold_offset_cold, degrees)
        degrees < 0 -> stringResource(R.string.settings_cold_offset_heat, abs(degrees))
        else -> stringResource(R.string.settings_cold_offset_normal)
    }
    IntSlider(
        label = label,
        value = degrees,
        range = UserSettings.COLD_OFFSET_RANGE,
        step = 1,
        onValueChange = onDegreesChange,
        modifier = modifier,
    )
}

/** A Material [Slider] that snaps to whole-number steps and shows a text label above it. */
@Composable
private fun IntSlider(
    label: String,
    value: Int,
    range: IntRange,
    step: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Slider's "steps" counts the stops between the two ends, e.g. 0..100 by 10 → 9 stops.
    val stopsBetweenEnds = (range.last - range.first) / step - 1
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = stopsBetweenEnds,
        )
    }
}
