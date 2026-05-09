<!-- Beskriver hvordan appen kjøres og avhengigheter (f.eks. krever
lokasjon). Bør også inneholde informasjon om biblioteker som er
brukt og en enkel forklaring for bibliotekene som ikke er vist i
kurset. -->

# IN2000 project team 16:
## About
A app for finding mushroom in Norway. The app offers a simple map ui where users can pick a mushroom and spot and get
an evaluation of its potentials. The app also offers heatmap to view the potential of a larger area.

To evaluate an area the app collects information on historical weather (MET-Frost), current weather (MET-LocationForecast),
Mushroom findings (GBIF), biological data (NIBIO SAT-SKOG) and resource type data (NIBIO AR5). The Context is then as a default
used in a local ML-model to calculate a score. The app also offers a option for AI-evaluation and Local simplified algorithm
___
## Requirements 
The app needs an internet connection to be able to offer a full user-experience. The app does not crash at the lack of a internet connection
but the functionality will be severally limited.  

**API-keys**
To run the application locally, you must provide your own API credentials. The app should not crash without these credentials but the AI functions will not work
For later deployment the storing of the API keys should be changed to secure them better.

``` Text 
AZURE_API_KEY= xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AZURE_ENDPOINT= https://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

___
## How to run
1. Clone the project: 
2. Open the project in Android studio
3. Add a local.properties file as described above
4. Build the project and start up either a emulator or a physical phone
5. Press the **Run** button in Android studio

___
## Dependencies
___
## Libraries

**WE NEED TO ADD MORE AND GO THRU THEN TOGETHER**

* MapLibre GL Android?
* ONNX Runtime  - used to run the ML models

* Dagger Hilt?
* Room Database?
* Jsoup?
* MoCKK?


___
## Members of the team
* Harriet
* Mikael
* Jostein
* Adam
* Esma
* Heidi
