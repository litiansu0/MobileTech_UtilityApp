package au.edu.jcu.assessment.utilityapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.domain.model.TemperatureUnit
import au.edu.jcu.assessment.utilityapp.domain.model.UserSettings
import au.edu.jcu.assessment.utilityapp.ui.settings.components.CitySearch
import au.edu.jcu.assessment.utilityapp.ui.settings.components.SettingsSection
import au.edu.jcu.assessment.utilityapp.ui.settings.components.TemperatureUnitSelector
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

/** Settings tab. Connects the [SettingsViewModel] to the stateless [SettingsContent]. */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()

    SettingsContent(
        settings = settings,
        query = viewModel.query,
        searchState = searchState,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::clearSearch,
        onCitySelected = viewModel::onCitySelected,
        onTemperatureUnitSelected = viewModel::onTemperatureUnitSelected,
        modifier = modifier,
    )
}

/** Draws the settings. Has no ViewModel, so it can be previewed on its own. */
@Composable
fun SettingsContent(
    settings: UserSettings,
    query: String,
    searchState: CitySearchState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCitySelected: (City) -> Unit,
    onTemperatureUnitSelected: (TemperatureUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        SettingsSection(title = stringResource(R.string.settings_location)) {
            CitySearch(
                currentCity = settings.city,
                query = query,
                searchState = searchState,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery,
                onCitySelected = onCitySelected,
            )
        }

        HorizontalDivider()

        SettingsSection(title = stringResource(R.string.settings_temperature_unit)) {
            TemperatureUnitSelector(
                selected = settings.temperatureUnit,
                onSelected = onTemperatureUnitSelected,
            )
        }
    }
}

// ---- Previews (sample data only, no network) ----

@Preview(showBackground = true, name = "Default")
@Composable
private fun SettingsDefaultPreview() {
    MobileTech_UtilityAppTheme {
        SettingsContent(
            settings = UserSettings(),
            query = "",
            searchState = CitySearchState.Idle,
            onQueryChange = {},
            onClearQuery = {},
            onCitySelected = {},
            onTemperatureUnitSelected = {},
        )
    }
}

@Preview(showBackground = true, name = "Search results")
@Composable
private fun SettingsSearchPreview() {
    MobileTech_UtilityAppTheme {
        SettingsContent(
            settings = UserSettings(temperatureUnit = TemperatureUnit.FAHRENHEIT),
            query = "Bris",
            searchState = CitySearchState.Results(
                listOf(
                    City.BRISBANE,
                    City("Brisbane", "California", "United States", 37.68, -122.40),
                )
            ),
            onQueryChange = {},
            onClearQuery = {},
            onCitySelected = {},
            onTemperatureUnitSelected = {},
        )
    }
}
