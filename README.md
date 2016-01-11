# super-duo
Contains two projects which are part of Udacity's Android Nanodegree program.

Notes:

Alexandria: I use Glide for image loading. This makes it easy to handle the situation when there is no internet connection. Glide also solves two other problems: images are cached (instead of reloaded each time), and there are no more exceptions when the screen is rotated during loading.

FootballScores apiKey: I applied what I learned in the Android course and did not commit my private apiKey to my public github repository. In order to build the project, you need to define a property

FootballDataApiKey=`<your api key>`

in the file `<USER_HOME>`/.gradle/gradle.properties.

