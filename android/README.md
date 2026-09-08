# Depth Keyboard for Android

This folder contains the native Android Input Method Service for Depth Keyboard. The existing Next.js simulator remains at the repository root.

## Build with Android Studio

1. Open the `android` folder in Android Studio Ladybug or newer.
2. Let Gradle sync and select a connected Android device or emulator.
3. Run the `app` configuration to install the debug APK.

## Enable and use the keyboard

1. Open the installed **Depth Keyboard** app.
2. Tap **Enable Depth Keyboard** and enable it in Android Settings.
3. Return to the app and tap **Choose keyboard**.
4. Select Depth Keyboard in the system picker.

The service supports letters, shift, symbols, backspace, space, enter, emoji, and switching to the next installed input method. A debug APK is not signed for Play Store distribution.

## GitHub Actions and Releases

Every push to `master` and every pull request runs `.github/workflows/android.yml`. Download `app-debug.apk` from the workflow run's artifact, then install it on an Android device.

To publish an APK in GitHub Releases, create and push a version tag such as `v1.0.0`:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow builds the debug APK and attaches it to the release automatically. Debug APKs are for testing and are not signed for Play Store distribution.
