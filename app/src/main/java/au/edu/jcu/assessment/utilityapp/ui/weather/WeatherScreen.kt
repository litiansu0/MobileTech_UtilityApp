package au.edu.jcu.assessment.utilityapp.ui.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.model.Metric
import au.edu.jcu.assessment.utilityapp.ui.preview.PreviewData
import au.edu.jcu.assessment.utilityapp.ui.preview.PreviewSurface
import au.edu.jcu.assessment.utilityapp.ui.weather.components.AdviceCard
import au.edu.jcu.assessment.utilityapp.ui.weather.components.CurrentWeatherCard
import au.edu.jcu.assessment.utilityapp.ui.weather.components.LocationHeader
import au.edu.jcu.assessment.utilityapp.ui.weather.components.MetricTiles
import au.edu.jcu.assessment.utilityapp.ui.weather.components.SunTimesCard

/** Main (Today) tab. Connects the [WeatherViewModel] to the stateless [WeatherContent]. */
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeatherContent(uiState = uiState, onRefresh = viewModel::refresh, modifier = modifier)
}

/** Draws one [WeatherUiState]. Has no ViewModel, so it can be previewed and tested on its own. */
@Composable
fun WeatherContent(
    uiState: WeatherUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        WeatherUiState.Loading -> LoadingState(modifier)
        is WeatherUiState.Error -> ErrorState(uiState.reason, onRetry = onRefresh, modifier)
        is WeatherUiState.Success -> WeatherDetails(uiState, onRefresh, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class) // PullToRefreshBox is still experimental in Material 3 1.3.
@Composable
private fun WeatherDetails(
    state: WeatherUiState.Success,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pull down to refresh. The indicator also shows when the refresh button is tapped,
    // because both set isRefreshing in the ViewModel.
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LocationHeader(
                cityName = state.city.name,
                updatedAt = state.weather.updatedAt.formatTime(),
                onRefresh = onRefresh,
                refreshError = state.refreshError?.let { refreshErrorMessage(it) },
            )
            CurrentWeatherCard(weather = state.weather)
            AdviceCard(advice = state.advice)
            MetricTiles(weather = state.weather, visibleMetrics = state.visibleMetrics)
            if (Metric.SUN_TIMES in state.visibleMetrics) {
                SunTimesCard(weather = state.weather)
            }
        }
    }
}

@Composable
private fun refreshErrorMessage(reason: ErrorReason): String = when (reason) {
    ErrorReason.NETWORK -> stringResource(R.string.refresh_failed_network)
    ErrorReason.SERVER -> stringResource(R.string.refresh_failed_server)
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.weather_loading),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun ErrorState(reason: ErrorReason, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val message = when (reason) {
        ErrorReason.NETWORK -> stringResource(R.string.error_network)
        ErrorReason.SERVER -> stringResource(R.string.error_server)
    }
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "⚠️", style = MaterialTheme.typography.displayMedium)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

// ---- Previews (sample data from PreviewData, no network) ----

@PreviewLightDark
@Composable
private fun WeatherSuccessPreview() {
    PreviewSurface {
        WeatherContent(uiState = PreviewData.success, onRefresh = {})
    }
}

/** Checks the layout still works when the user has made system text 1.5× larger. */
@Preview(name = "Large text", showBackground = true, fontScale = 1.5f)
@Composable
private fun WeatherLargeTextPreview() {
    PreviewSurface {
        WeatherContent(uiState = PreviewData.success, onRefresh = {})
    }
}

@PreviewLightDark
@Composable
private fun WeatherRefreshFailedPreview() {
    PreviewSurface {
        WeatherContent(
            uiState = PreviewData.success.copy(refreshError = ErrorReason.NETWORK),
            onRefresh = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun WeatherLoadingPreview() {
    PreviewSurface {
        WeatherContent(WeatherUiState.Loading, onRefresh = {})
    }
}

@PreviewLightDark
@Composable
private fun WeatherErrorPreview() {
    PreviewSurface {
        WeatherContent(WeatherUiState.Error(ErrorReason.NETWORK), onRefresh = {})
    }
}
