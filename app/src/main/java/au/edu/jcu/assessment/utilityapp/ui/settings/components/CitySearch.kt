package au.edu.jcu.assessment.utilityapp.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.City
import au.edu.jcu.assessment.utilityapp.ui.settings.CitySearchState

/** Search box plus the matching cities. Tapping a city makes it the current city. */
@Composable
fun CitySearch(
    currentCity: City,
    query: String,
    searchState: CitySearchState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_current_city, currentCity.displayName),
            style = MaterialTheme.typography.bodyMedium,
        )
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.settings_search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.settings_search_clear),
                        )
                    }
                }
            },
            // Searching already happens while typing, so "Search" on the keyboard just closes it.
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        )

        when (searchState) {
            CitySearchState.Idle -> Unit
            CitySearchState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is CitySearchState.NoResults -> SearchMessage(
                stringResource(R.string.settings_search_no_results, searchState.query)
            )
            CitySearchState.Error -> SearchMessage(stringResource(R.string.settings_search_error))
            is CitySearchState.Results -> searchState.cities.forEach { city ->
                CityResultItem(
                    city = city,
                    isCurrent = city == currentCity,
                    onClick = {
                        focusManager.clearFocus()
                        onCitySelected(city)
                    },
                )
            }
        }
    }
}

@Composable
private fun CityResultItem(city: City, isCurrent: Boolean, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(city.name) },
        supportingContent = {
            Text(listOfNotNull(city.region, city.country).joinToString(", "))
        },
        trailingContent = {
            if (isCurrent) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.settings_city_selected),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun SearchMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
}
