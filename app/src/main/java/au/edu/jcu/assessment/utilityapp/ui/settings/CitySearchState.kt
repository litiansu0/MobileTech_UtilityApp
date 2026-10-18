package au.edu.jcu.assessment.utilityapp.ui.settings

import au.edu.jcu.assessment.utilityapp.domain.model.City

/** What the city search area below the search box is showing. */
sealed interface CitySearchState {

    /** Nothing typed yet (or fewer than [SettingsViewModel.MIN_QUERY_LENGTH] letters). */
    data object Idle : CitySearchState

    data object Loading : CitySearchState

    data class Results(val cities: List<City>) : CitySearchState

    data class NoResults(val query: String) : CitySearchState

    data object Error : CitySearchState
}
