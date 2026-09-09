# Depth Keyboard for Android

Depth Keyboard is a native Android input method for Android 8.0 and newer. It is privacy-first by default: typing personalization and clipboard history remain local to the device, and Incognito mode disables both.

## Implemented features

The current APK includes QWERTY, QWERTZ, AZERTY, DVORAK, and COLEMAK layouts; English, Urdu, Arabic, Spanish, French, German, Italian, Portuguese, and Turkish language options; local suggestions and autocorrect; double-space punctuation; shift and caps lock; long-press accent input; symbol mode; optional number row; adjustable keyboard size, width, and height; standard, one-handed, floating, and split/thumb layouts; compact keys; local clipboard history with clear controls; categorized emoji groups with recents; theme selection; smartbar controls; and privacy settings.

The settings screen is grouped into Typing, Gestures & key behavior, Layout & keys, Rich input & toolbar, Themes & accessibility, Privacy & data, and Setup. Settings are persisted locally and can be reset to defaults.

Online translation, GIFs, stickers, voice transcription, cloud synchronization, and AI provider calls are not bundled in this release. Their toolbar entries fail safely or explain that a provider must be configured.

## Enable and use

1. Open **Depth Keyboard**.
2. Open **Enable / choose keyboard** and enable Depth Keyboard in Android Settings.
3. Select Depth Keyboard from the system input-method picker.
4. Open any text field and type.
5. Open the keyboard gear button to return to settings.

## Build and verify

Open the `android` folder in Android Studio or run the repository’s GitHub Actions workflow. The workflow assembles the debug APK and uploads it as an artifact. Representative checks should cover keyboard activation, size and mode persistence, long-press accents, double-space punctuation, autocorrect, number row, split layout, emoji insertion, clipboard clearing, incognito behavior, and theme persistence.

## Privacy

No network permission is required for the local typing core. Clipboard entries and learned words are stored in app-private preferences. Use **Incognito mode** before entering sensitive text, and use **Clear learned words** or **Clear clipboard history** when you want to remove local data.
