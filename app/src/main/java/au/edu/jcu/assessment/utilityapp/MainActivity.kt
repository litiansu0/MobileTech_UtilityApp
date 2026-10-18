package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import au.edu.jcu.assessment.utilityapp.ui.settings.SettingsScreen
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.ui.weather.WeatherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTech_UtilityAppTheme {
                UtilityApp()
            }
        }
    }
}

/** The two bottom-navigation tabs. */
private enum class AppTab(@StringRes val labelRes: Int, val icon: ImageVector) {
    TODAY(R.string.tab_today, Icons.Default.Home),
    SETTINGS(R.string.tab_settings, Icons.Default.Settings),
}

// No preview here: the weather tab needs WeatherApplication's container, which previews
// don't have. See the previews in WeatherScreen.kt instead.
@Composable
fun UtilityApp() {
    // rememberSaveable keeps the selected tab when the screen rotates.
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.TODAY) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        // The label already names the tab, so the icon needs no description.
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                AppTab.TODAY -> WeatherScreen()
                AppTab.SETTINGS -> SettingsScreen()
            }
        }
    }
}
