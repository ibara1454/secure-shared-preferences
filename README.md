# Secure shared preferences

[![Jitpack](https://jitpack.io/v/ibara1454/secure-shared-preferences.svg)](https://jitpack.io/#ibara1454/secure-shared-preferences)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

The encrypted SharedPreferences for Android.

*Secure Shared Preferences* provides the features on Android 4.4 and above:

- SharedPreferences's file name encryption.
- SharedPreferences's key / value pair encryption.

## Installation

Add it in your root `build.gradle` at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency.

```groovy
dependencies {
    implementation 'com.github.ibara1454:secure-shared-preferences:v0.1.0'
}
```

## Usage

You can use it like normal SharedPreferences.

In Kotlin,

```kotlin
val context = applicationContext
val preferences = context.getSecuredSharedPreferences("name", Context.MODE_PRIVATE)
```

or in Java,

```java
Context context = getApplicationContext();
SharedPreferences preferences = SecureSharedPreferences.getSecureSharedPreferences(
    context,
    "name",
    Context.MODE_PRIVATE
);
```

For more examples, please take a look on [example](https://github.com/ibara1454/secure-shared-preferences/tree/master/app/src/main/java/com/github/ibara1454/sample).

## License

[MIT](LICENSE)

Copyright 2020 Chiajun Wang (ibara1454).
