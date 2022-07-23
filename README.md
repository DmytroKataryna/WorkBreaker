# WorkBreaker
Simple app that was made to revise and play with the latest Android techs, app clean architecture was followed. 

Functionality of app: is to track user geo location and inform about the need to take a break, after certain time being in that zone
- User can pick a location on the google map
- By using geofencing API, this zone is marked as a "working" zone
- When user enters this zone, the app will be informing user to take a break every 45min, and to do some exercises.
- When user leaves zone, the app informs that zone has been left, and user should check whether he/she didn't forget anything


* MVVM (with state flow)
* Clean architecture
* Coroutines
* Hilt
* Google maps
* Geofencing
* WorkManager
* Jetpack Navigation
* DataStore
* Retrofit
* Glide
* Timber
* Data bindings
* Git-hooks (Detekt static code analysis)
* JUnit, Mockito
