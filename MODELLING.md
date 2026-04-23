
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
And here is a overview of the test files
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