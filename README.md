# IN2000 project team 16:
## About
An app for finding mushrooms in Norway. The app offers a simple map ui where users can pick a mushroom and a spot and get
an evaluation of its potentials. The app also offers a heatmap to view the potential of a larger area.

To evaluate an area the app collects information on historical weather (MET-Frost), current weather (MET-LocationForecast),
Mushroom findings (GBIF), biological data (NIBIO SAT-SKOG) and resource type data (NIBIO AR5). The context is then as a default
used in a local ML-model to calculate a score. The app also offers an option for AI-evaluation and a local simplified algorithm.
___
## Requirements 
The app needs an internet connection to be able to offer a full user-experience. The app does not crash at the lack of an internet connection
but the functionality will be severally limited.  

**API-keys**
To run the application locally, you must provide your own API credentials. The app should not crash without these credentials but the AI functions will not work. For later deployment the storing of the API keys should be changed to secure them better.
At the moment the local.properties file with our API credentials have been added to the git include and manually to the zip file, but this is REAL BAD code practises. This has been done just so that the evaluator could get the app working straight out of the zip file. 

``` Text 
AZURE_API_KEY= xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AZURE_ENDPOINT= https://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

___
## How to run
<ol>
  <li>
    Clone the project via the GitHub page:
    <ol>
      <li>Click the green <strong>&lt;&gt; Code</strong> button above the repository contents.</li>
      <li>Clone the repository by copying the link for HTTPS.</li>
    </ol>
  </li>

  <li>
    Open the project in Android Studio:
    <ol>
      <li>Open Android Studio on your PC.</li>
      <li>Click the <strong>Clone Repository</strong> button.</li>
      <li>Paste the HTTPS link you copied from GitHub into the field for URL.</li>
      <li>Click the <strong>Clone</strong> button in the bottom of the window.</li>
    </ol>
  </li>

  <li>
    Add a local.properties file:
    <ol>
      <li>In your project folder, add a <code>local.properties</code> file.</li>
      <li>Paste your <code>AZURE_API_KEY</code> in this file, like described above.</li>
    </ol>
  </li>

  <li>
    Build the project by clicking on the <strong>hammer icon</strong> in the left sidebar.
  </li>

  <li>
    Start the app:
    <ol>
      <li>Open the <strong>Device manager</strong> on the right sidebar.</li>
      <li>
        Start up using an emulator:
        <ol>
          <li>Add a new device by clicking the <strong>+</strong> button in the Device Manager.</li>
          <li>Click on <strong>Create Virtual Device</strong>.</li>
          <li>Select your desired device and click the <strong>Next</strong> button.</li>
          <li>Select your desired API level. F.eks. 36.0.</li>
          <li>Click the <strong>Finish</strong> button.</li>
        </ol>
      </li>
      <li>
        Start up using a physical device:
        <ol>
          <li>Connect your device to your PC with a cable. F.eks. USB.</li>
          <li>Make sure your device is in Developer mode.</li>
          <li>Accept the connection on your device.</li>
          <li>Your device should appear in the <strong>Device Manager</strong>. Click <strong>Run</strong> on your remote device in the Device Manager.</li>
        </ol>
      </li>
    </ol>
  </li>

  <li>
    Press the <strong>Run</strong> button in Android Studio.
  </li>
</ol>

___
## Libraries

**AndroidX ConstraintLayout (Views/XML):** is used to position the MapLibre map view on the screen, and to lay it out relative to other views and UI elements.

**AndroidX Navigation:** is used to define and manage navigation between the app screens in a structured way.  

**[DataStore](https://developer.android.com/topic/libraries/architecture/datastore):** is a data storage solution that lets you store key-value pairs or typed objects with protocol buffers. In our app it is used for saving user settings and favorite mushrooms.

**[Hilt / Dagger](https://dagger.dev/hilt/):** simplifies dependency injection (DI) by providing a standard, structured way to integrate the design pattern for DI into Android applications.

**Jetpack Compose:** is a UI toolkit for building user interfaces with composable functions.

**[Ktor](https://github.com/ktorio/ktor):** is an asynchronous framework for creating microservices, web applications and more.

**[MapLibre Native Android SDK](https://madewithmaplibre.com/sdks/maplibre-native-android-sdk):** provides high-performance vector map rendering, offline map support, and comprehensive mapping features without requiring additional frameworks. The SDK includes full Android lifecycle management and MapView Components. It is used in our app to display an interactive map on the screen.

**MockK:** is a mocking library for Kotlin. In our app it is used for making mock objects for the unit tests.

**[ONNX Runtime](https://github.com/onnx/onnx):** provides an open source format for AI models. It defines an extensible computation graph model, as well as definitions of built-in operators and standard data types. Used to run the ML models in our app.

**[SplashScreen](https://developer.android.com/develop/ui/views/launch/splash-screen):** lets the app launch with an animation, including an into-app motion at launch, a splash screen showing your app icon, and a transition to your app itself.

**[Sprite](https://github.com/stevdza-san/SpriteView-KMP):** is a simple UI component that allows you to render and play Sprite Sheet animation in your composable screen. Used for the loading animation that runs when the app is starting up.
