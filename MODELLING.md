
<!--
Beskrivelse og diagrammer, vi anbefaler å generere dem med
Mermaid som vist på forelesning. Se kravene til modellering
lenger ned i dette dokumentet. Ha med hvorfor diagrammet er
valgt og hva dere ønsker å med det.`
-->

# Modelling



This is a structures view just from using the /tree /f
```Text
C:.
└───team16
    ├───data
    │   ├───ai
    │   ├───artsdatabasen
    │   ├───di
    │   ├───kartverket
    │   ├───met
    │   │   ├───frost
    │   │   └───locationForecast
    │   ├───nibio
    │   ├───onBoard
    │   ├───profileScreen
    │   ├───roomDatabase
    │   │   └───userDatabase
    │   └───species
    ├───domain
    │   ├───ai
    │   ├───logic
    │   │   └───mushroom
    │   ├───model
    │   └───usecase
    └───ui
        ├───common
        ├───dev
        ├───loading
        ├───map
        ├───minSide
        │   └───dropDownOptions
        ├───mushroomInfo
        ├───navigation
        ├───onboard
        ├───popUp
        └───theme
```
And here is an overview of the test files
```Text
    ExampleUnitTest.kt
    MapViewModelTest.kt
    MushroomInfoViewModelTest.kt
    MushroomRepoTest.kt
    MushroomScorerTest.kt
    SpeciesRepoTest.kt
    SpeciesTest.kt
```
And here it is with all files
``` Text
C:.
└───team16
│   Application.kt
│   MainActivity.kt
│   Profile.kt
│   
├───data
│   ├───ai
│   │       AiDataSource.kt
│   │       AiDataSourceModels.kt
│   │       AiRepository.kt
│   │       
│   ├───artsdatabasen
│   │       LocalMushroomDataSoruce.kt
│   │       LocalSpeciesMapper.kt
│   │       MushroomDataSource.kt
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
│   │       AddressRepository.kt
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
│   ├───profileScreen
│   │       profileScreenDropdownItem.kt
│   │       profileScreenRepository.kt
│   │       
│   ├───roomDatabase
│   │   │   AppDatabase.kt
│   │   │   DatabaseModule.kt
│   │   │   
│   │   └───userDatabase
│   │           User.kt
│   │           UserDao.kt
│   │           UserRepository.kt
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
│       Text.kt
│       UiState.kt
│       
├───dev
│       DevScreen.kt
│       DevViewModel.kt
│       
├───loading
│       Animation.kt
│       SplashViewModel.kt
│       
├───map
│       DropDownMenu.kt
│       MapScreen.kt
│       MapViewModel.kt
│       MenuOptions.kt
│       OnboardingModel.kt
│       ScoreCard.kt
│       UiState.kt
│       
├───minSide
│   │   ProfileScreen.kt
│   │   ProfileScreenViewModel.kt
│   │   
│   └───dropDownOptions
│           HistoryScreen.kt
│           InfoScreen.kt
│           SettingsScreen.kt
│           ShroomBankScreen.kt
│           ShroomControlScreen.kt
│           ShroomSpotScreen.kt
│           
├───mushroomInfo
│       MushroomInfoScreen.kt
│       MushroomInfoViewModel.kt
│       MushroomListScreen.kt
│       
├───navigation
│       BottombarNavItem.kt
│       BottomNavigationBar.kt
│       Navigation.kt
│       Screens.kt
│       
├───onboard
│       MainModule.kt
│       onboardingPage.kt
│       onboardingScreen.kt
│       OnBoardItem.kt
│       OnBoardModel.kt
│       WelcomeViewModel.kt
│       
├───popUp
│       PopUpScreen.kt
│       
└───theme
Color.kt
Shape.kt
Theme.kt
Type.kt
```

## Use case text

### Use case 1

**Name:** Calculation of mushroom score  
**Primary actor:** Mushroom forager  
**Secondary actor:** Proxy server  
**Precondition:** The user has opened the map page and the map screen is loaded  
**Postcondition:** The user has been presented with a score above 0  

**Main flow:**  
1. The user taps the map   
2. The system retrieves the necessary information from the APIs MET (Frost and Location Forecast) and Nibio (SAT-skog and AR5)  
3. The system checks whether the mushroom is in season  
4. The mushroom is in season and the system calculates a score  
5. The user is presented with a scorecard that contains the score and relevant information  

**Alternative flow:**  
3.1 The mushroom is not in season  
3.2 The scorecard tells the user that the mushroom is not in season  
3.3 The user selects a mushroom that is in season   

### Use case 2  

**Name:** Selecting mushrooms for the map   
**Primary actor:** Mushroom forager  
**Secondary actor:** -  
**Precondition:** The user has opened the map screen and the map screen is loaded  
**Postcondition:** The user has changed mushrooms  

**Main flow:**  
1. The user opens the drop-down menu in the map  
2. The user has added a favorite mushroom, which is therefore displayed as a quick selection in the menu  
3. The user taps on this mushroom and thus selects it  

**Alternative flow 1:**  
2.1 The user has not selected a favorite mushroom, and therefore has no quick selection in the menu  
2.2 The user taps on a mushroom and selects it as a favorite  
2.3 The user taps on the map screen again and opens the drop-down menu  

**Alternative flow 2:**  
2.1 The user taps on the Mushroom page   
2.2 The user finds the desired mushroom  

### Use case 3  

**Name:** Navigation  
**Primary actor:** Mushroom picker  
**Secondary actor:** Proxy server    
**Precondition:** The user has opened the map page and the map page is loaded  
**Postcondition:** The user has navigated to the desired location on the map  

**Main flow:**
1. The user presses the search bar  
2. The system opens the search bar  
3. The user enters “Lillehammer”  
4. The system retrieves the necessary information from the Addresses and Places APIs from the Norwegian Mapping Authority  
5. The system displays the Lillehammer area on the map  

**Alternative flow:**  
4.1 The system fails to retrieve the necessary information  
4.2 The user presses out of the search function  
4.3 The user navigates to Lillehammer by dragging the map  

## Use case diagrams  

### Use case diagram 1  

### Use case diagram 2  

### Use case diagram 3  
 
## Sequence diagrams  

### Sequence diagram 1  

```mermaid
---
config:
  layout: elk
---
sequenceDiagram
    actor F as Mushroom Forager
    participant UI as MapScreen (System UI)
    participant V as MapViewModel
    participant P as Repositories
    participant M as MET APIs (Frost, Location Forecast)
    participant N as Nibio APIs (SAT-skog, AR5)


    Note over UI: Precondition: Map screen loaded


    F->>UI: Taps map
    loop calculate score
    UI->>V: Collects lat and lon
    V->>P: onMapClicked(lat, lon)
    P->>M: Get weather data
    M-->>P: Return weather data
    P->>N: Get forest and land data
    N-->>P: Return forest data and land data
    P->>P: Check if mushroom is in season


    alt Mushroom in season
        P->>P: Calculate mushroom score
        P-->>UI: Provide score and relevant display information
        UI-->>F: Displays the information in a score card
        Note over UI,F: Postcondition: Score above 0 shown
    else Mushroom not in season
        P-->>UI: Provide score and relevant display information
        UI-->>F: Show “Not in season” message
        F->>UI: Selects another mushroom
    end
    end
```

### Sequence diagram 2  

```mermaid
---
config:
  layout: elk
---
sequenceDiagram


    actor Forager as Mushroom Forager
    participant MS as MapScreen
    participant NH as NavHost
    participant ML as MushroomListScreen
    participant MI as MushroomInfoScreen
    title Selecting Mushrooms for the Map


    Note over Forager,MS: Precondition: Map screen is open and loaded
    loop Selecting mushroom from dropdown menu
        Forager->>MS: Opens drop-down menu
        alt User has favorite mushroom
            MS-->>Forager: Displays favorite in quick selection
            Forager->>MS: Taps on favorite mushroom
           
           
           
    end
    alt User does not have favorite mushroom
         MS-->>Forager: No quick selection available
            Forager->>MS: Taps "Soppsiden" on navBar
            NH->>ML: Navigates to MushroomListScreen
            ML-->>Forager: Displays MushroomListScreen
            Forager->>ML: Selects desired mushroom
            NH->>MI: Navigates to MushroomInfoScreen
            MI-->>Forager: Displays MushroomInfoScreen


            Forager->>MI: Taps a mushroom to mark as favorite
            MI-->>Forager: Indicates favorite being selected
            Forager->>MI: Taps "Kart" on navBar
            NH->>MS: Navigate to MapScreen
            MS-->>Forager: Displays MapScreen
            Forager->>MS: Reopens drop-down menu
            MS-->>Forager: Displays favorite in quick selection
            Forager->>MS: Taps on favorite mushroom


    end
    alt User does not want to favorite any mushrooms
             Forager->>MS: Taps "Soppsiden" on navBar
            NH->>ML: Navigates to MushroomListScreen
            ML-->>Forager: Displays MushroomListScreen
            Forager->>ML: Checks desired mushroom
            ML->>MS: Sends selected mushroom
    end
    end

    Note over Forager,MS: Postcondition: User has changed mushrooms

```

### Sequence diagram 3  

```mermaid
---
config:
  layout: elk
---
sequenceDiagram
    actor Forager 
    participant MS as MapScreen
    participant V as MapViewModel
    participant R as LocationRepository
    participant API as Norwegian Mapping Authority

    Note over Forager,API: Precondition: Map screen loaded

    Forager->>MS: Presses search bar
    MS->>MS: Opens search bar
    Forager->>MS: Enters "Lillehammer"
    MS->>V: Collects query
    V->>R: onSearchQueryChanged(query)
    R->>API: Requests location data

    alt Successful retrieval
        API-->>R: Returns location data
        R-->>V: Formats and forwards location data
        V-->>MS: Forwards location data
        MS-->>Forager: Displays Lillehammer option
        Forager->>MS: Selects Lillehammer option
        MS->>V: Pings location change
        V->>V: Updates location
        V-->>MS: Recenteres map view
        MS-->>Forager: Shows updated map
    else Retrieval fails
        API-->>R: Fails to fetch data
        R-->>V: Returns emptyList()
        V-->>MS: Returns emptyList()
        MS-->>Forager: Displays no options
    
        Forager->>MS: Presses out of search
        MS->>MS: Closes search bar
        Forager->>MS: Drags map to Lillehammer
        MS-->>Forager: Displays dragged location
    end

    Note over Forager,API: Postcondition: User navigated to desired location
```

## Activity diagrams  

### Activity diagram 1  

```mermaid
---
config:
  layout: elk
---
flowchart TD
    title["Calculation of <br>Mushroom Score"]


    start((Start))


    taps([User taps <br>the map])
    calculate([Score is <br>calculated])
    present([Score is presented])
    message([Score card shows <br>that the mushroom is <br>not in season])
    change([User chooses a <br>mushroom that is <br>in season])


    inSeason{Mushroom in season?}


    ending((End))


    start --> taps
    taps --> inSeason
    inSeason --YES--> calculate
    inSeason --NO--> message
    message --> change
    change --> calculate
    calculate --> present
    present --> ending
```

### Activity diagram 2   

```mermaid
---
config:
  layout: elk
---
flowchart TD
    title["Selecting mushrooms <br>for the app"]


    start((Start))


    dropdown([User opens <br>dropdown menu])
    taps([User taps and selects <br>mushroom])
    addFavorite([User adds a <br>mushroom to favorites])
    select([User selects mushroom <br>directly from the list])
    list([User navigates to <br>mushroom screen])
    map([User navigates <br>to map screen])


    hasFavorite{Favorites added?}
    wantsFavorite{User wants to add favorite?}


    ending((End))


    start --> dropdown
    dropdown --> hasFavorite
    hasFavorite --YES--> taps
    hasFavorite --NO--> list
    list --> wantsFavorite
    wantsFavorite --YES--> addFavorite
    wantsFavorite --NO--> select
    addFavorite --> map
    map --> dropdown
    select --> ending
    taps --> ending
```

### Activity diagram 3   

```mermaid
flowchart TD
    title["Navigation"]


    start((Start))


    bar([User presses and <br>opens the search bar])
    enter([User enters <br>'Lillehammer'])
    display([Lillehammer area <br>displays on screen])
    out([User closes the <br>search bar])
    nav([User navigates to <br>Lillehammer by dragging <br>the map])


    success{API retrieving successful?}


    ending((End))


    start --> bar
    bar --> enter
    enter --> success
    success --YES--> display
    success --NO--> out
    out --> nav
    nav --> display
    display --> ending
```


## Class diagrams

### Class diagram 1

This is a class diagram that visualizes the process of changing screens. 
```mermaid

classDiagram
direction TB
    class NavHost {
	    +navigate(route: String)
    }

    class MenuScreen {
	    +navController: NavController
	    +darkTheme: Boolean
	    +onToggleTheme:() -> Unit
    }

    class MenuItemData {
	    +text: String
	    +icon: ImageVector
	    +iconDescription: String
	    +route: String
    }

    class MushroomListScreen {
	    +viewModel: MushroomInfoViewModel
	    +navigateToMushroom:() -> Unit
	    +onMushroomControl:() -> Unit
    }

    class MushroomInfoScreen {
	    +viewModel: MushroomInfoViewModel
	    +onNavigateBack:() -> Unit
	    +onMushroomControl:() -> Unit
	    +onNavigateToMap:() -> Unit
    }

    class MushroomInfoViewModel {
	    +toggleFavorite(mushroom: Mushroom)
	    +onMushroomSelected(mushroom: Mushroom)
    }

    class HelpSection {
	    +title: String
	    +text: String
	    +imageRes: Int
    }

    class InfoScreen {
	    +onNavigateBack:() -> Unit
    }

    class AlgorithmScreen {
	    +onNavigateBack:() -> Unit
    }

    class PoisonControl {
	    +onNavigateBack:() -> Unit
    }

    class Security {
	    +onNavigateBack:() -> Unit
    }

    class SummaryScreen {
	    title: String
	    imageRes. Int
	    +ImageDialog
	    +onNavigateBack:() -> Unit
	    +HelpScreen()
	    +HelpSectionItem(section: HelpSection)
	    onDismiss:() -> Unit)
    }

    class SpeciesDataSource {
	    +amount: Int
	    +mushroomList: List~Mushroom~
	    +allSpeciesProfiles: List~SpeciesProfile~
	    +getAllMushrooms() : List~Mushroom~
	    +getAmountOfMushrooms() : Int
    }

    class Mushroom {
	    +commonName: String
	    +latinName: String
	    +imageRes: Int
	    +bioRes: String
	    +properties: List~MushroomProperty~
	    +profile: SpeciesProfile
    }

    class MushroomProperty {
	    +label: String
	    +iconRes: Int
	    +isNegative: Boolean
    }

    class SpeciesRepository {
	    +speciesDataSource: SpeciesDataSourceInterface
	    +dataStore: DataStore
	    +initializeDefaults()
	    +toggleFavorite(mushroom: Mushroom)
	    +getMushroomByName(name: String) : Mushroom?
	    +updateSelectedMushroom(mushroom: Mushroom)
	    +getAllMushrooms() : List~Mushroom~
	    +getMushroomCount() : Int
    }

    class SpeciesProfile {
	    +name: String
	    +taxKey: String
	    +optimalBonitet: Double
	    +optimalForestAge: Double
	    +minForestAge: Double
	    +spruceAffinity: Float
	    +pineAffinity: Float
	    +broadleafAffinity: Float
	    +optimalAvgTemp: Double
	    +hardinessMinTemp: Double
	    +maxToleranceTemp: Double
	    +optimalTotalRain14d: Double
	    +activeMonths: List~Int~
	    +optimalCurrentHumidity: Double
	    +maxCurrentRainMm: Double
	    +minRaw: Float
	    +maxRaw: Float
    }

	note "Composable screen, not a regular Kotlin class"
	note for SummaryScreen "Uses HelpSection data to display the guide"
	note for HelpSection "data class"
	note for SpeciesProfile "Had to cut off som attributes,
	to keep it short"

    NavHost --> MenuScreen
    NavHost --> MushroomListScreen
    NavHost --> MushroomInfoScreen
    NavHost --> InfoScreen
    NavHost --> AlgorithmScreen
    NavHost --> SummaryScreen
    NavHost --> Security
    NavHost --> PoisonControl
    MenuScreen --> MenuItemData
    MushroomListScreen --> MushroomInfoViewModel
    MushroomInfoScreen --> MushroomInfoViewModel
    MushroomListScreen --> MushroomInfoScreen
    SummaryScreen --> HelpSection
    MushroomInfoViewModel --> SpeciesRepository
    SpeciesRepository --> SpeciesDataSource
    SpeciesDataSource --> Mushroom
    SpeciesDataSource --> SpeciesProfile
    Mushroom --> MushroomProperty
    Mushroom --> SpeciesProfile
```

### Class diagram 2
This is a class diagram that visualizes how the app evaluates the chosen area on the map
```mermaid

classDiagram
direction TB
    class MapViewModel {
	    +speciesRepository: SpeciesRepositoryInterface
	    +locationRepository: LocationRepositoryInterface
	    +getEnvironmentContextUseCase: GetEnvironmentContextUseCase
	    +calculateHeatmapAreaUseCase: CalculateHeatmapAreaUseCase
	    +scorerProvider: ScorerProvider
	    +onMapClicked(lat: Double, lon: Double)
	    +fetchHeatmapData(bounds: BoundingBox)
	    +toggleHeatmap()
	    +onMushroomSelected(mushroom: Mushroom)
	    +onSearchQueryChanged(query: String)
	    +searchAddressByText(query: String)
	    +searchLocation(location: SearchableLocation)
	    +centerOnUser()
	    +onCameraMoved()
	    +onCameraIdle(target: LatLng?, zoom: Double, visibleBounds: BoundingBox)
	    +onUserLocationUpdated(lat: Double, lon: Double)
	    +onPendingCameraTargetConsumed()
	    +toggleSatellite()
	    +toggleWmsForest()
	    +toggleHistoricalFinds()
	    +dismissEvaluation()
	    +refreshCurrentLocation()
	    setSearchBarExpanded(expanded: Boolean)
    }

    class GetEnvironmentContextUseCase {
	    +locationForecastRepository: LocationForecastRepositoryInterface
	    +mushroomRepository: MushroomRepositoryInterface
	    +forestRepository: SatSkogRepositoryInterface
	    +frostRepository: FrostRepositoryInterface
	    +ar5Repository: ForestRepositoryInterface
	    +invoke(lat: Double, lon: Doubleprofile: SpeciesProfile? = null) : EnvironmentContext
    }

    class CalculateHeatmapAreaUseCase {
	    +locationForecastRepository: LocationForecastRepositoryInterface
	    +mushroomRepository: MushroomRepositoryInterface
	    +forestRepository: SatSkogRepositoryInterface
	    +frostRepository: FrostRepositoryInterface
	    +scorerProvider: ScorerProvider
	    +invoke(bounds: BoundingBox, profile: SpeciesProfile,
	    ): List
	    existingTiles: List = emptyList()
	    +getStepSize(mapHeight: Double) : Double
	    +generateGridPoints(bounds: BoundingBox, step: Double) : List
	    +calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double) : Double
    }

    class FrostRepository {
	    +weatherHistory(lat: Double, lon: Double) : WeatherHistory?
    }

    class LocationForecastRepository {
	    +weatherNext8Hours(lat: Double, lon: Double) : WeatherData?
	    +roundToTwoDecimals(value: Double) : Double
    }

    class SatSkogRepository {
	    +fetchAndMapForestData(lat: Double, lon: Double) : ForestData?
	    +getForestData(lat: Double, lon: Double) : ForestData?
	    +getForestDataForHeatmap(lat: Double, lon: Double) : ForestData?
    }

    class ScoreProvider {
	    +getScorer() : MushroomScorer
    }

    class MushroomScorer {
	    +calculateScore(context: EnvironmentContext, species: SpeciesProfile) : EvaluationResult
    }

    class EnvironmentContext {
	    +weatherHistory: WeatherHistory?
	    +weatherData: WeatherData?
	    +forestData: ForestData?
    }

    class EvaluationResult {
	    +score: Int
	    +description: String
	    +isInSeason: Boolean
    }

    class SpeciesProfile {
	    +name: String
	    +taxKey: String
	    +optimalBonitet: Double
	    +optimalForestAge: Double
	    +minForestAge: Double
	    +spruceAffinity: Float
	    +pineAffinity: Float
	    +broadleafAffinity: Float
	    +optimalAvgTemp: Double
	    +hardinessMinTemp: Double
	    +maxToleranceTemp: Double
	    +optimalTotalRain14d: Double
	    +activeMonths: List~Int~
	    +optimalCurrentHumidity: Double
	    +maxCurrentRainMm: Double
	    +minRaw: Float
	    +maxRaw: Float
    }

    class WeatherHistory {
	    +avgTempPast14Days: Double?
	    +totalRainPast14Days: Double?
	    +minTempPast14Days: Double?
	    +maxTempPast14Days: Double?
    }

    class WeatherData {
	    +temperature: Double
	    +precipitation: Double
	    +windSpeed: Double
    }

    class ForestData {
	    +forestType: String
	    +bonitet: String
    }

    class BoundingBox {
	    +north: Double
	    +south: Double
	    +east: Double
	    +west: Double
    }

    class HeatmapTile {
	    +score: Int
	    +bounds: BoundingBox
    }

    class SearchableLocation {
	    +name: String
	    +lat: Double
	    +lon: Double
    }

    class LocationRepository {
	    +placeDataSource: PlaceDataSourceInterface
	    +addressDataSource: AddressDataSourceInterface
	    +search(query: String) : List~SearchableLocation~
	    +getLocationCoordinates(query: String) : SearchableLocation?
    }

    class AiAlgorithm {
	    -aiRepository: AiService
	    +calculateScore(context: EnvironmentContext, species: SpeciesProfile) EvaluationResult
    }

    class BasicAlgorithm {
	    +calculateScore(context: EnvironmentContext, species: SpeciesProfile) EvaluationResult
	    -buildDescription(forest: Double, history: Double, current: Double) String
    }

    class AiService {
	    +getAiScore(prompt: String) EvaluationResult
    }

    class MlAlgorithm {
	    -contextApp: Context
	    +calculateScore(context: EnvironmentContext, species: SpeciesProfile) EvaluationResult
	    -buildDescription(ml: Double, current: Double) String
    }

	<<interface>> MushroomScorer
	<<Interface>> AiService

    MapViewModel --> GetEnvironmentContextUseCase
    MapViewModel --> CalculateHeatmapAreaUseCase
    MapViewModel --> LocationRepository
    MapViewModel --> ScoreProvider
    MapViewModel --> EvaluationResult
    MapViewModel --> SpeciesProfile
    MapViewModel --> BoundingBox
    MapViewModel --> SearchableLocation
    GetEnvironmentContextUseCase --> FrostRepository
    GetEnvironmentContextUseCase --> LocationForecastRepository
    GetEnvironmentContextUseCase --> SatSkogRepository
    GetEnvironmentContextUseCase --> EnvironmentContext
    FrostRepository --> WeatherHistory
    LocationForecastRepository --> WeatherData
    SatSkogRepository --> ForestData
    EnvironmentContext --> WeatherHistory
    EnvironmentContext --> WeatherData
    EnvironmentContext --> ForestData
    ScoreProvider --> MushroomScorer
    MushroomScorer --> EnvironmentContext
    MushroomScorer --> SpeciesProfile
    MushroomScorer --> EvaluationResult
    AiAlgorithm ..|> MushroomScorer
    AiAlgorithm ..|> AiService
    AiAlgorithm --> EnvironmentContext
    AiAlgorithm --> SpeciesProfile
    AiAlgorithm --> EvaluationResult
    BasicAlgorithm ..|> MushroomScorer
    BasicAlgorithm --> EnvironmentContext
    BasicAlgorithm --> SpeciesProfile
    BasicAlgorithm --> EvaluationResult
    MlAlgorithm ..|> MushroomScorer
    MlAlgorithm --> EnvironmentContext
    MlAlgorithm --> SpeciesProfile
    MlAlgorithm --> EvaluationResult
    CalculateHeatmapAreaUseCase --> BoundingBox
    CalculateHeatmapAreaUseCase --> HeatmapTile
    HeatmapTile --> BoundingBox
    LocationRepository --> SearchableLocation
    SatSkogRepository --> ForestData
```

