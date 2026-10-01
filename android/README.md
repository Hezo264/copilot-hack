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

## Prepare a signed APK for Uptodown

The repository includes a manual `Build signed release APK` workflow. It signs the release with the private PKCS#12 keystore and uploads the APK as a workflow artifact. Never commit the keystore or its passwords.

Before running the workflow, add these repository Actions secrets under **Settings > Secrets and variables > Actions**:

- `ANDROID_KEYSTORE_BASE64`: Base64-encoded contents of the `.p12` keystore.
- `ANDROID_KEYSTORE_PASSWORD`: The keystore password.
- `ANDROID_KEY_PASSWORD`: The private-key password.

The key alias is `AfroBuildersGradeCalculator`. In PowerShell, encode the downloaded keystore with:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("afro-builders-grade-calculator-release.p12"))
```

Then run **Actions > Build signed release APK > Run workflow** and download the `afro-builders-grade-calculator-release-apk` artifact. Uptodown submission itself requires signing in to the publisher account and completing its current review and listing flow. Back up the keystore and passwords securely; they are required to sign all future updates for this app.
