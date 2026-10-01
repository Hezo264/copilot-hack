# Offline Android APK

The Android app loads the calculator from a local HTML asset packaged inside the APK. It has no Internet permission and does not need a network connection to calculate grades.

The app targets Android 5.0 (API 21) and newer. On launch, it briefly shows **KABONA** in bold red text before opening the calculator. The APK includes the calculator HTML locally, so calculations do not require an internet connection.

The GitHub Pages version also caches the calculator for offline use after the first successful online visit in a browser that supports service workers.

The HTML source stays in the repository root at `grade-calculator.html`; Gradle copies it into the Android assets directory during the build.

## Build locally

Install JDK 17, Gradle 8.9, and Android SDK platform/build tools 35, then run from the repository root:

```sh
gradle --no-daemon -p android assembleDebug
```

The debug APK is generated at `android/app/build/outputs/apk/debug/app-debug.apk`.

The `Build offline Android APK` GitHub Actions workflow also builds this APK for pull requests targeting `main`. Download it from the workflow run's Artifacts section. This debug build is for testing; publishing to Uptodown requires a release-signed APK and store listing details.
