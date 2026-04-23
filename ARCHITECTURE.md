<!--
Dette dokumentet er tiltenkt for personer som skal videreutvikle
appen, sette seg inn i den og fortsette å jobbe med
“kodestil”/praksis som er brukt.

Beskriver arkitekturen som er benyttet i appen, gir en overordnet
introduksjon til hvordan prosjektet er strukturert og formidler
arkitekturvalgene som er gjort for videreutvikling.

Beskrivelse av hvordan viktige objektorienterte prinsipper som
lav kobling og høy kohesjon samt design patterns som MVVM
og UDF er ivaretatt i løsningen.

Beskriv løsningen beregnet på lesere som skal jobbe med drift,
vedlikehold og videreutvikling av løsningen. Beskriv hvilke
teknologier og arkitektur som brukes i løsningen. Beskriv hvilket
API-nivå (Android versjon) dere har valgt, og hvorfor.
-->



# ARCHITECTURE

## Introduction and Technology Stack
Kotlin as the primary language
Jetpack Compose as the UI framework
-Asynchronous operations kotlin coroutines and state flows were used
Network with Ktor and dependencies injectings
A room databse was set up but not used, but prepared for implementation of saving locations
Saving settings and mushroom data a simpler solution was using datastore
Python has been used on scripts on directly part of the app. The reason for using python for those cases were simple. We had more former knowledge with python so it was easier to use and python also has a large varitie of machine learning libraries like SciKit. together with being able to reuse code from other personal and scientific projects
* Collection of a local station list
* Collecting data and training species specific ML models (ONNX files)

## Architecture pattern
Followed Model-View-ViewModel architecture with clean architecture principles. three distinct layers as described bellow

Low coupling and high cohesion thru the codebase
Related functionalities in specific packages. NEtworking for weather in the met, and so onD Dependencie injection using dagger hilt 

### UI Layer
Jetpack Compose screens and Viewmodeles
Unidirectional dataflow
UI state as a read-only stateflow


### Domain Layer
Core logic and completly independent of the UI and data frameworks. Houses the scoring logic which is accesses thru interfaces. the machine learning algorithms 
A domain layer was not strongly recommended in the https://developer.android.com/topic/architecture/recommendations for android app, but was just a recommendation. Even if our app is not Huge 
We would deem it appropriate to use a domain layer. Mainly because of the amount og logic and the usefulness of usecases minimalizing the amount of codes in the UI-layer and reusability
* Use cases 

### Data Layer


## API level
API level __ s

## Future development
Avoid logic in screen and viewmodeles
Use usecases for bigger functions
Use Dagger hilt for dependencies
Theme is added in the theme in the ui layer