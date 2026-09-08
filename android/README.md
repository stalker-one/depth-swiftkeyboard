# Depth Keyboard for Android

This folder contains the native Android Input Method Service for Depth Keyboard. The website at the repository root is a feature-focused home page; the live keyboard runs only inside the Android application.

## Build with Android Studio

1. Open the `android` folder in Android Studio Ladybug or newer.
2. Let Gradle sync and select a connected Android device or emulator.
3. Run the `app` configuration to install the Depth Keyboard app.

## Enable and use the keyboard

1. Open **Depth Keyboard**.
2. Tap **Enable Depth Keyboard**, then turn on Depth Keyboard in Android Settings.
3. Return to the app and tap **Choose keyboard**.
4. Select **Depth Keyboard** in the system picker.
5. Open any text field and type.

The keyboard includes English and Spanish subtype entries, shift and symbol layouts, next-word suggestions, emoji tools, clipboard paste, language switching, backspace, punctuation, editor-aware Enter/Done actions, and theme-ready toolbar controls. It supports Android 8.0 (API 26) and newer.

## Troubleshooting activation

If the keyboard does not appear after enabling it, return to the Depth Keyboard app and tap **Choose keyboard**, then select Depth Keyboard manually. On some Android versions, the input-method picker is also available from the keyboard icon in the navigation bar. The app now reports whether the service is enabled and resets its input state whenever a new text field opens.

## GitHub Actions and Releases

Every push to `master` and every pull request runs `.github/workflows/android.yml`. The workflow builds the debug APK and uploads it as a workflow artifact. Debug APKs are for testing and are not signed for Play Store distribution.
