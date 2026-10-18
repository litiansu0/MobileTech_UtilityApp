package au.edu.jcu.assessment.utilityapp.ui.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit

/** Two-option toggle between °C and °F (Material 3 segmented button). */
@OptIn(ExperimentalMaterial3Api::class) // Segmented buttons are still experimental in Material 3 1.3.
@Composable
fun TemperatureUnitSelector(
    selected: TemperatureUnit,
    onSelected: (TemperatureUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val units = TemperatureUnit.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        units.forEachIndexed { index, unit ->
            SegmentedButton(
                selected = unit == selected,
                onClick = { onSelected(unit) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = units.size),
            ) {
                Text(unit.symbol)
            }
        }
    }
}
