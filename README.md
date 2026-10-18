# OOTD – Outfit of the Day for Weather

**OOTD** is a utility app for one daily question: *"What should I take and wear when I go out today?"*

Open the app and the answer is on one screen: today's weather for your city, plus a short list of advice. Should you take an umbrella? Do you need sunscreen? What should you wear? Is it windy? It is built with Kotlin, Jetpack Compose and Material Design 3, and uses the free [Open-Meteo](https://open-meteo.com) web APIs (no API key needed).

Built for **CP3406 Assessment 1: Utility App** (James Cook University), starting from the course's starter template.

---

## Features

### Today screen (main screen)

| Section | What it shows |
|---|---|
| **Header** | City name, the time the data was updated, a refresh button |
| **Current weather card** | Condition icon (day/night), temperature, condition name, feels-like temperature, today's high/low |
| **Today's advice card** | 🌂 umbrella, 🧴 sun protection, 👕 clothing and (when it is windy) 💨 wind advice, each in one short line |
| **Detail tiles** | Rain chance, UV now, wind speed and humidity. Each tile can be switched on or off in Settings |
| **Sunrise & sunset card** | Today's sunrise and sunset in the city's local time (optional) |

- **Pull to refresh**, or tap the refresh button. The current weather stays on screen while new data loads.
- If a refresh fails, the older data stays visible and a short message explains why.
- A full loading or error screen (with a **Retry** button) appears only on first launch or after changing city or unit. The error message tells "no internet" apart from "server problem".

### Settings screen

| Setting | Effect on the Today screen |
|---|---|
| **City search** | Search any city as you type (Open-Meteo geocoding). Tapping a result loads that city's weather |
| **Temperature unit** | °C / °F toggle. Weather is fetched again in the chosen unit |
| **Umbrella threshold** | Slider, 0–100 %. Rain chance at which the umbrella is suggested (default 40 %) |
| **Feel the cold / heat** | Slider, −5 to +5 °C. Shifts the clothing advice for people who feel the cold (or heat) more |
| **Show on main screen** | Switches for each detail tile and the sunrise/sunset card |

Settings are kept in memory while the app runs. The assignment does not require them to persist.

### How the advice is worked out

All the rules live in one pure Kotlin object, `domain/OutfitAdvisor.kt`, which has unit tests.

| Advice | Rule |
|---|---|
| **Umbrella** | Suggested if it is raining **now** (WMO weather code) or today's highest rain chance ≥ the user's threshold |
| **Sun protection** | Based on today's **maximum** UV index, using the WHO bands: Low 0–2, Moderate 3–5, High 6–7, Very high 8–10, Extreme 11+. Using the daily maximum means a morning check still warns about midday sun |
| **Clothing** | Based on the feels-like temperature, converted to °C first so the same bands work in °F, then shifted by the user's offset. Bands: Hot ≥ 28, Warm ≥ 22, Mild ≥ 16, Cool ≥ 10, Cold ≥ 0, Freezing < 0 |
| **Wind** | Mentioned only when the wind is ≥ 30 km/h |

---

## Web APIs

Both APIs are free, need no API key and are called with **Retrofit** + **Gson** using Kotlin `suspend` functions.

| API | Used for | Example request |
|---|---|---|
| [Open-Meteo Forecast](https://open-meteo.com/en/docs) | Current conditions + today's summary in one request | `https://api.open-meteo.com/v1/forecast?latitude=-27.47&longitude=153.03&current=temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m,uv_index,is_day&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max,uv_index_max,sunrise,sunset&temperature_unit=celsius&timezone=auto&forecast_days=1` |
| [Open-Meteo Geocoding](https://open-meteo.com/en/docs/geocoding-api) | City search (name → coordinates) | `https://geocoding-api.open-meteo.com/v1/search?name=Brisbane&count=10&language=en` |

The fields requested are the `CURRENT_FIELDS` / `DAILY_FIELDS` constants in `data/remote/OpenMeteoApi.kt`.

---

## Architecture

The app follows Android's recommended layered architecture: **UI → ViewModel → Repository → data source**. It uses manual dependency injection.

```
 ┌───────────── UI (Jetpack Compose) ─────────────┐
 │  WeatherScreen            SettingsScreen       │
 │       │ collectAsStateWithLifecycle  │         │
 │  WeatherViewModel         SettingsViewModel    │
 └───────┼──────────────────────────────┼─────────┘
         │                              │
         ▼                              ▼
  WeatherRepository  ◄── reads ── SettingsRepository
  (interface)                    (StateFlow<UserSettings>,
         │                        shared by both screens)
         ▼
  NetworkWeatherRepository
         │  Retrofit
         ▼
  OpenMeteoApi / GeocodingApi  ──►  Open-Meteo web APIs

  domain/OutfitAdvisor (pure Kotlin) ── weather + settings → advice
```

### Layers

| Layer | Package | Responsibility |
|---|---|---|
| **Data – remote** | `data/remote` | Retrofit interfaces and Gson DTOs that match the JSON exactly |
| **Data – repository** | `data/repository` | `WeatherRepository` turns DTOs into app models (`Weather`, `City`) and hides Retrofit from the rest of the app. `SettingsRepository` holds the settings in a `StateFlow` |
| **Domain** | `domain` | App models and the business rules: `OutfitAdvisor` and the `WeatherCondition` code-to-text table. No Android code, so it is easy to test |
| **UI** | `ui/weather`, `ui/settings` | ViewModels plus Compose screens, each split into small components |
| **DI** | `di/AppContainer.kt` | Builds Retrofit and the repositories once. `WeatherApplication` owns the container, and each ViewModel gets its dependencies through a `ViewModelProvider.Factory` |

### Data flow and state

- **Unidirectional data flow.** Each screen has a ViewModel that exposes state as `StateFlow`. The UI observes it with `collectAsStateWithLifecycle()` and sends events back through lambdas (`onRefresh`, `onCitySelected`, …).
- **Sealed UI state.** `WeatherUiState` is `Loading`, `Success` or `Error`, so the screen handles every case.
- **Only fetch when needed.** `WeatherViewModel` watches the settings:
  - If the **city or unit** changes, it downloads the weather again.
  - Every other setting goes through `combine(weather, settings)`, so moving a slider or flipping a switch recalculates the advice instantly, without a network call.
- **Coroutines.** Network calls run in `viewModelScope`. A new request cancels an older one still in progress.
- **Debounced search.** The city search waits 400 ms after the last keystroke (`snapshotFlow` + `debounce` + `collectLatest`). Typing "Brisbane" sends one request, not eight.
- **Stateful / stateless split.** Each screen is a thin stateful wrapper (`WeatherScreen`, `SettingsScreen`) around a stateless composable (`WeatherContent`, `SettingsContent`). The stateless part takes plain data, so it can be previewed without a ViewModel or network.

### Jetpack Compose and Material 3

- Material 3 components: `Scaffold` + `NavigationBar`, `Card`, `OutlinedTextField`, `ListItem`, `SingleChoiceSegmentedButtonRow`, `Slider`, `Switch`, `PullToRefreshBox`, progress indicators.
- Colour scheme generated with **Material Theme Builder** to match the app icon. Light and dark themes are both supported. Dynamic colour is turned off so the custom palette is always used.
- Reusable components in `ui/*/components/`, for example `AdviceCard`, `MetricTiles`, `SettingsSection` and `CitySearch`.
- `@PreviewLightDark` previews for every component and screen, plus a 1.5× large-text preview. Shared sample data lives in `ui/preview/PreviewData.kt`.
- All user-facing text is in `res/values/strings.xml`.
- The selected tab survives screen rotation (`rememberSaveable`).

---

## Assessment topics covered

| Week | Topic | Where it is used |
|---|---|---|
| 1 | Kotlin & Android Studio | Whole project: data classes, enums, sealed interfaces, extension functions, `object`s |
| 2 | Layouts with Jetpack Compose | `ui/weather/components/*`, `ui/settings/components/*`: `Column`/`Row`, weights, scrolling, tile grid |
| 3 | Material Design 3 | `ui/theme/` (Theme Builder palette, light/dark), M3 components listed above |
| 4 | App architecture (ViewModel, DI, Repository) | `WeatherViewModel`, `SettingsViewModel`, `data/repository/*`, `di/AppContainer.kt`, `WeatherApplication` |
| 5 | Web APIs with Retrofit | `data/remote/*`, `NetworkWeatherRepository`: two Open-Meteo APIs, suspend functions, Gson |
| Extra | Coroutines & Flow | `StateFlow`, `combine`, `stateIn`, `debounce`, `collectLatest`, job cancellation |
| Extra | Unit testing | `OutfitAdvisorTest`, `SettingsRepositoryTest` (JUnit 4) |

---

## Project structure

```
app/src/main/java/au/edu/jcu/assessment/utilityapp/
├── MainActivity.kt          # Scaffold + bottom navigation (Today / Settings)
├── WeatherApplication.kt    # Creates the DI container
├── di/                      # AppContainer (manual dependency injection)
├── data/
│   ├── remote/              # Retrofit APIs + JSON DTOs
│   └── repository/          # WeatherRepository, SettingsRepository
├── domain/                  # OutfitAdvisor, WeatherCondition, models
└── ui/
    ├── weather/             # Today screen, ViewModel, UI state, components
    ├── settings/            # Settings screen, ViewModel, components
    ├── preview/             # Sample data for @Preview
    └── theme/               # Material 3 theme (Theme Builder)
app/src/test/                # JVM unit tests
```

---

## Getting started

**Requirements:** Android Studio (recent stable), Android SDK 36, and a device or emulator running **Android 8.0 (API 26)** or later with internet access.

1. Clone the repository and open the folder in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or phone.

The app opens on **Brisbane**. Change the city in **Settings**.

Command line (from the project root; on Windows use `gradlew.bat`):

```bash
./gradlew assembleDebug        # build the debug APK
./gradlew installDebug         # install on a connected device / emulator
./gradlew test                 # run the unit tests
./gradlew lint                 # Android lint
```

---

## Limitations and possible improvements

- Settings reset when the app is closed. They could be saved with Jetpack DataStore.
- The city is chosen by search. Using the phone's location (with permission) would be quicker.
- Weather icons are emoji, so they look slightly different on each phone brand.
- Only today's forecast is shown, to keep the app "at a glance". An hourly rain timeline would be a natural next feature.

---

## Credits

- Weather and geocoding data: [Open-Meteo.com](https://open-meteo.com), licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).
- Starter template: CP3406 Mobile Technology, James Cook University.
