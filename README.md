<!-- Beskriver hvordan appen kjøres og avhengigheter (f.eks. krever
lokasjon). Bør også inneholde informasjon om biblioteker som er
brukt og en enkel forklaring for bibliotekene som ikke er vist i
kurset. -->

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
    Build the project by clicking on the <strong>hammer icon</strong> in the left hand bar.
  </li>

  <li>
    Start the app:
    <ol>
      <li>Open the <strong>Device manager</strong> on the right hand bar.</li>
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
## Dependencies
___
## Libraries

**MapLibre Native Android SDK:** provides high-performance vector map rendering, offline map support, and comprehensive mapping features without requiring additional frameworks. The SDK includes full Android lifecycle management and MapView Components (https://madewithmaplibre.com/sdks/maplibre-native-android-sdk).

**Google Mobile Ads SDK:** is a mobile advertising library which makes it possible to feauture ads in the app. Used in our app for asking the user for permission to use their location.

**Ktor:** is an asynchronous framework for creating microservices, web applications and more (https://github.com/ktorio/ktor). 

**Hilt / Dagger:** simplifies dependency injection (DI) by providing a standard, structured way to integrate the design pattern for DI into Android applications (https://dagger.dev/hilt/).

**Sprite:** is a simple UI component that allows you to render and play Sprite Sheet animation in your composable screen (https://github.com/stevdza-san/SpriteView-KMP). Used for the loading animation that runs when the app is starting up.

**MockK:** is a mocking library for Kotlin. In our app it is used for making mock objects for the unit tests.

**ONNX Runtime:** provides an open source format for AI models. It defines an extensible computation graph model, as well as definitions of built-in operators and standard data types (https://github.com/onnx/onnx). Used to run the ML models in our app.

Room

SplashScreen

DataStore

___
## Members of the team
* Harriet
* Mikael
* Jostein
* Adam
* Esma
* Heidi
