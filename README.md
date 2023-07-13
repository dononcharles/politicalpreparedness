## REPLACE GOOGLE MAPS API KEY IN `gradle.properties` FILE

* `API_CALL_KEY="YOUR_API_KEY_HERE"`

## Political Preparedness

PoliticalPreparedness is an example application built to demonstrate core Android Development skills as presented in the Udacity Android Developers Kotlin curriculum.

## The final look of the app
<img src="https://github.com/dononcharles/politicalpreparedness/blob/master/gif/test.gif" width="250" height="450">

This app demonstrates the following views and techniques:

* [Retrofit](https://square.github.io/retrofit/) to make api calls to an HTTP web service.
* [Moshi](https://github.com/square/moshi) which handles the deserialization of the returned JSON to Kotlin data objects.
* [Glide](https://bumptech.github.io/glide/) to load and cache images by URL.
* [Room](https://developer.android.com/training/data-storage/room) for local database storage.

It leverages the following components from the Jetpack library:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding adapters
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) with the SafeArgs plugin for parameter passing between fragments

## Suggested Workflow

* It is recommend you save all beautification until the end of the project. Ensure functionality first, then clean up UI. While UI is a component of the application, it is best to deliver a functional product.
* Start by getting all screens in the application to navigate to each other, even with dummy data. If needed, comment out stub code to get the application to compile. You will need to create actions in *nav_graph.xml* and UI elements to
  trigger the navigation.
* Create an API key and begin work on the Elections Fragment and associated ViewModel.
    * Use the elections endpoint in the Civics API and requires no parameters.
    * You will need to create a file to complete the step.
    * This will require edits to the Manifest.
    * Link the election to the Voter Info Fragment.
* Begin work on the Voter Info Fragment and associated ViewModel.
* Begin work on the Representative Fragment and associated ViewModel.
    * This will require edits to Gradle.
    * You will need to create a file to complete the step.
