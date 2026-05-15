# ARCHITECTURE

## 1. Introduction

This is an Android app that helps users check the chances of mushrooms in an area. It combines weather data, forest data, and historical observations into a heat map and pin for a scorecard for selectable mushroom species. It currently supports 11 mushroom species, each with some basic information.

The codebase follows **MVVM** with a lightweight **clean architecture** split into three layers  `ui`, `domain`, and `data` wired together with **Dagger Hilt**. State is exposed through `StateFlow` and consumed by Jetpack Compose under a **Unidirectional Data Flow (UDF)** model.

## 2. Technology Stack

| Technology | Role in the app |
|---|---|
| **Kotlin** | Primary language. JVM toolchain 21. |
| **Jetpack Compose + Material 3** | UI framework. Navigation via `androidx.navigation:navigation-compose`. |
| **Kotlin Coroutines + StateFlow** | Asynchronous work and reactive UI state. |
| **Ktor Client** | HTTP client (single shared instance) with `ContentNegotiation` + `kotlinx-serialization` JSON and `Logging`. |
| **Dagger Hilt** | Dependency injection. `@HiltAndroidApp` on the `Application`, `@HiltViewModel` on view models, `@Binds` / `@Provides` modules under `data/di/`. |
| **DataStore Preferences** | Persists onboarding flag and small settings. (Room was not adopted; persistence needs are small enough to not justify a SQL database.) |
| **MapLibre Android SDK** | Vector map with the GeoJSON heatmap layer on the map screen. |
| **ONNX Runtime (Android)** | Runs the 11 species-specific scoring models bundled under `app/src/main/assets/`. |
| **Azure OpenAI** | Optional scoring backend (LLM-driven) selected from the settings menu. |
| **kotlinx.serialization** | DTO (de)serialization for all network responses. |

**Offline tooling (not part of the Android module):** the ONNX models were trained outside this repository using Python (SciKit / standard ML stack). The models ship as static assets, Python does not run on-device.

## 3. Project file Structure

This shows the file structure of the app. Separated into 3 main folders Data, Domain and Ui.

```
C:TEAM 16
│   Application.kt
│   MainActivity.kt
│   
├───data
│   ├───ai
│   │       AiDataSource.kt
│   │       AiDataSourceModels.kt
│   │       AiRepository.kt
│   │       
│   ├───artsdatabasen
│   │       LocalMushroomDataSource.kt
│   │       LocalSpeciesMapper.kt
│   │       MushroomInfo.kt
│   │       MushroomRepository.kt
│   │       
│   ├───di
│   │       AiModule.kt
│   │       DataModule.kt
│   │       JsonModule.kt
│   │       
│   ├───kartverket
│   │       Address.kt
│   │       AddressDataSource.kt
│   │       LocationRepository.kt
│   │       Place.kt
│   │       PlaceDataSource.kt
│   │       SearchableLocation.kt
│   │       
│   ├───met
│   │   ├───frost
│   │   │       FrostDataSource.kt
│   │   │       FrostDataSourceModel.kt
│   │   │       FrostRepository.kt
│   │   │       StationLocator.kt
│   │   │       
│   │   └───locationForecast
│   │           LocationForecastDataSource.kt
│   │           LocationForecastModels.kt
│   │           LocationForecastRepository.kt
│   │           
│   ├───nibio
│   │       ForestDataSource.kt
│   │       ForestRepository.kt
│   │       SatSkogDataSource.kt
│   │       SatSkogModels.kt
│   │       SatSkogRepository.kt
│   │       
│   ├───onBoard
│   │       DataStoreRepository.kt
│   │       
│   └───species
│           SpeciesDataSource.kt
│           SpeciesRepository.kt
│           
├───domain
│   ├───ai
│   │       AiService.kt
│   │       
│   ├───logic
│   │   └───mushroom
│   │           AiAlgorithm.kt
│   │           BasicAlgorithm.kt
│   │           KillSwitches.kt
│   │           MlAlgorithm.kt
│   │           MushroomScorer.kt
│   │           ScorerProvider.kt
│   │           Scorers.kt
│   │           
│   ├───model
│   │       EvalutationModels.kt
│   │       HeatmapModels.kt
│   │       Mushroom.kt
│   │       SpeciesProfile.kt
│   │       
│   └───usecase
│           CalculateHeatmapAreaUseCase.kt
│           GetEnvironmentContextUseCase.kt
│           
└───ui
    ├───common
    │       Buttons.kt
    │       ErrorScreen.kt
    │       Images.kt
    │       Text.kt
    │       
    ├───loading
    │       Animation.kt
    │       SplashViewModel.kt
    │       
    ├───map
    │       MapDropDown.kt
    │       MapLayers.kt
    │       MapLifeCycle.kt
    │       MapScreen.kt
    │       MapUiOverlay.kt
    │       MapUiState.kt
    │       MapViewModel.kt
    │       ScoreCard.kt
    │       ScoreCardViewModel.kt
    │       
    ├───menu
    │   │   AlgorithmViewModel.kt
    │   │   MenuScreen.kt
    │   │   ThemeToggle.kt
    │   │   
    │   └───dropDownOptions
    │           AlgorithmScreen.kt
    │           FromMenuDefaultScreen.kt
    │           InfoScreen.kt
    │           PoisonControl.kt
    │           Security.kt
    │           SummaryScreen.kt
    │           
    ├───mushroomInfo
    │       MushroomInfoScreen.kt
    │       MushroomInfoViewModel.kt
    │       MushroomListScreen.kt
    │       ShroomControlScreen.kt
    │       
    ├───navigation
    │       BottombarNavItem.kt
    │       BottomNavigationBar.kt
    │       Navigation.kt
    │       Screens.kt
    │       
    ├───onboard
    │       OnBoardingScreen.kt
    │       WelcomeViewModel.kt
    │       
    ├───popUp
    │       CrashActivity.kt
    │       CrashHandler.kt
    │       PopUpScreen.kt
    │       
    ├───snackbar
    │       LocationConnectivityObserver.kt
    │       NetworkConnectivityObserver.kt
    │       
    └───theme
            Color.kt
            Padding.kt
            Shape.kt
            Theme.kt
            Type.kt
```

## 4. Architecture: MVVM + Clean Architecture


A domain layer was not strongly recommended in the [https://developer.android.com/topic/architecture/recommendations](https://developer.android.com/topic/architecture/recommendations) for android app, but was just a recommendation. Even if our app is not huge, we would deem it appropriate to use a domain layer. Mainly because of the amount of logic and the usefulness of usecases minimalizing the amount of codes in the UI layer, and gives us better reusability.

We have followed the MVVM , here is what each role corresponds to in our project.

- **View** is the Composable screens under `ui/`. They render state from their ViewModel and forward user events back The View never calls repositories or use cases directly. It only knows about its ViewModel.

- **ViewModel** is the `@HiltViewModel` annotated classes that sit next to each screen. They hold the UI state in a `StateFlow` and call the relevant use case when something happens.

- **Model** is everything that produces the data the ViewModel needs. For us that means the domain layer (use cases, scoring algorithms, domain models) and the data layer (repositories and data sources). Example: `CalculateHeatmapAreaUseCase` is what produces the heatmap data that `MapViewModel` exposes to `MapScreen`.



### 4.1 UI Layer

- Composable screens hold **no business logic**. They read state via `viewModel.uiState.collectAsState()` and forward user events to ViewModel methods.
- Each feature screens ViewModel exposes a single `StateFlow<XxxUiState>` backed by an immutable `data class`. (The splash screen is a short-lived exception that uses Compose MutableState.) 
- Side effects (network calls, DataStore writes, scoring) run inside `viewModelScope.launch { ... }`.

### 4.2 Domain Layer

- **Use cases** are plain classes injected with repositories. They orchestrate multiple data sources and call the active scorer.
  - `CalculateHeatmapAreaUseCase` builds a grid over the visible bounding box and scores points in parallel with `supervisorScope` and `Dispatchers.Default`.
  - `GetEnvironmentContextUseCase`  parallel `async` / `await` fan-out to weather, forest, and history sources.
- **Scoring** is hidden behind the `MushroomScorer` interface, with three implementations:
  - `BasicAlgorithm`: rule-based, weighted features.
  - `MlAlgorithm`: ONNX inference per species.
  - `AiAlgorithm`: Azure OpenAI prompt-based scoring.
- `ScorerProvider` is the **Strategy** selector and is switched at runtime from the settings menu. The algorithm for heatmap is pinned to `MlAlgorithm` to avoid continuous calls to the AI.

### 4.3 Data Layer

- Each external API follows the same shape. a `*DataSource` (raw Ktor calls + DTOs) and a `*Repository` (caching and mapping into domain models).
- Repositories are exposed as **interfaces**; concrete classes are bound in Hilt modules under `data/di/`. This keeps the domain layer decoupled from any specific data implementation.
- A **single shared `HttpClient`** is configured once in `DataModule` (JSON content negotiation, logging, request-timing interceptor) and injected wherever needed.
- A `DataStoreRepository` backs onboarding state and small preferences.

## 5. Principles in the app

### 5.1 Low Coupling

- All cross-layer dependencies are on **interfaces**, wired by Hilt. Swapping the scoring algorithm or replacing a data source requires no UI changes.
- The UI layer never imports Ktor, ONNX, or any concrete data-source class.

### 5.2 High Cohesion

- Each package owns exactly one concern (for example, `data/met/frost/` is only MET Frost). Files inside a feature package change together.
- Most screens have a single state object rather than many overlapping flows, keeping rendering logic predictable.
- Larger functions are broken down into small, named helper functions instead of one long body.

### 5.3 Unidirectional Data Flow (UDF)

- **State flows down**: `StateFlow` → Composable.
- **Events flow up**: lambdas → ViewModel methods.
- ViewModels mutate state with `_uiState.update { it.copy(...) }`. Composables never mutate state directly.
- Configuration changes (rotation, dark mode toggle) and short-lived process death are handled because state is owned by the ViewModel and re-collected on recomposition.

## 6. API Level

- `minSdk = 24` (Android 7.0 Nougat). Below 24 we lose Java 8 stream APIs and reliable `java.time` support that the app relies on through Kotlin stdlib.
- `targetSdk = 36`, the reason we chose this targetSdk was simply because android studio recommended it when we first made the project.


## 7. Robustness and Lifecycle

- **No-network / API-failure paths** surface through snackbars driven by `NetworkConnectivityObserver` and `LocationConnectivityObserver`. The UI never silently shows stale or empty data without an indicator.
- A global `CrashHandler` (under `ui/popUp/`) catches uncaught exceptions and routes the user to a `CrashActivity` rather than letting the process die without feedback.
- **Configuration changes** are non destructive: state is owned by ViewModels (Hilt-scoped to the nav back-stack entry) and exposed as `StateFlow`, so rotation and recomposition do not re-trigger network work.
- All network calls are **asynchronous** (Ktor + coroutines) and cancellation-aware via structured concurrency.

## 8. Testing

Unit tests live under `app/src/test/java/no/uio/ifi/in2000/team16/`:

- `MushroomScorerTest`
- `MushroomRepoTest`
- `SpeciesRepoTest`
- `SpeciesDataSourceTest`
- `MapViewModelTest`
- `MushroomInfoViewModelTest`
- `ExampleUnitTest`
- `Fakes.kt` (shared test fixtures)

Test stack: **JUnit 4**, **MockK**, and `kotlinx-coroutines-test`.

## 9. Warnings
- 2 warnings about **Duplicated icons under different names** (`cute_mushroom.png`, `sprite01.png`): These warnings are fixable, we just did not have the time and the functional gain was low.
- 1 Warning **Image defined in density-independent drawable folder**(`sprite01.png`): This file should have been in a different folder. since there is little functional gain we did not prioritize to reorder the files. 

## 10. Future Development

Notes for others picking up the project:

- **Keep new logic out of Composables and ViewModels.** Push orchestration into use cases inside the `domain/` layer. Composables stay declarative, ViewModels stay thin.
- **Adding a new external API** mirror the existing `data/<provider>/` shape: a `DataSource` for raw calls and DTOs, a `Repository` interface for the domain layer, and Hilt bindings in `data/di/`.
- **Stick with Hilt for DI.** Don't introduce a second DI framework.
- **Theme tokens belong in `ui/theme/`.** Don't hardcode Material 3 colors in screens.
