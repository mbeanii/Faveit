# Install Faveit on a phone

Faveit is ready for a short prototype trial on an Android 6.0 (API 23) or newer
phone. The current artifact is debug-signed for direct testing; it is not a
Play Store or production release.

## Fastest route: Android Studio

1. Open this repository in a current Android Studio release with JDK 17 and
   Android SDK 36 available.
2. Enable Developer options and USB debugging on the phone, connect it, and
   accept the phone's RSA prompt.
3. Select the phone and run the `app` configuration.

## Terminal route

With JDK 17, Android SDK 36, and `adb` on `PATH`:

```bash
./gradlew assembleDebug
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Open **Faveit** from the phone's app launcher. If a differently signed Faveit
build is already installed, uninstall that copy first; uninstalling removes the
favorites and customizations stored locally by that installation.

## A worthwhile three-minute first session

1. On the first Restaurants page, choose one genuine favorite and tap
   **Start now**. Notice whether selection depth, haptic feedback, and the
   confirmation feel satisfying rather than noisy.
2. Search for `In N Out`, confirm that the result says **Restaurant**, and tap
   Add once. The app should confirm `In-N-Out is in your favorites`.
3. Clear search, open **Restaurants**, and judge whether your two choices are
   readable at conversational speed.
4. Tap a favorite to rename it, move its category, change its gem, reset it,
   and remove it. The muted discovery section should make it easy to add back.
5. Only if useful, open the bell and opt into the restrained weekly local
   reminder. Notification delivery is intentionally not enabled by default.

## Privacy and cleanup

The APK has no `INTERNET` permission, account, backend, ads, analytics, or
tracking SDK. Catalog data is bundled; favorites and overrides stay in Android
Preferences DataStore on the phone. WorkManager contributes notification,
boot, wake-lock, foreground-service, and network-state permissions, but Faveit
cannot open network sockets because the APK has no `INTERNET` permission.

To remove the prototype and its local data:

```bash
adb uninstall com.faveit.app
```
