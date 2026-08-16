# Acceptance iteration 05

Branch: `agent/acceptance-hardening`

Status: second rereview fix implemented; rereview pending

## Kind acceptance advocate

The second automatic review went one level deeper than the first and protected
the integrity of the first-run evidence.

1. Iteration 4 correctly removed conditional assertions and made method order
   deterministic.
2. The reviewer noticed that process-level Application startup can precede the
   Activity rule and touch DataStore, a lifecycle detail invisible in the test
   body alone.
3. The requested correction improves determinism without changing user-facing
   behavior or adding an artificial production reset screen.
4. The palette assertions from iteration 4 received no further criticism.

## Impossible-to-please acceptance critic

One new P2 finding was valid.

### C1 — Backing-file deletion bypassed the active DataStore

`FaveitApplication.onCreate()` launches a read of
`repository.currentPreferences()` before the Activity rule. Deleting the
Preferences DataStore file afterward does not synchronize with that read or
invalidate DataStore's in-memory state. The journey could therefore open Home
from cached `setupComplete=true` and fail before first-run setup.

The state reset must be an awaited DataStore edit through the already active
repository instance before the inner Activity Compose rule launches.

## Seasoned software architect

1. Add a narrowly scoped, internal, `@VisibleForTesting` reset seam from
   `FaveitRepository` to `UserPreferencesStore`.
2. Implement the reset as `dataStore.edit { it.clear() }` so it is serialized
   with startup reads/writes, updates the in-memory cache, and returns only after
   the empty preferences are committed.
3. Change the outer rule to obtain `FaveitApplication`, call the repository
   reset in `runBlocking`, and only then evaluate the Activity rule.
4. Remove all direct filesystem knowledge from the instrumentation test.
5. Rebuild debug, instrumentation, lint, and release artifacts because main
   source now contains the internal test seam.

## Senior software developer

Implemented the active-store reset:

- `UserPreferencesStore.resetForTests()` performs an awaited DataStore edit
  and clear.
- `FaveitRepository.resetForTests()` exposes that operation internally.
- Both functions are marked `@VisibleForTesting`.
- `ResetFirstRunStateRule` invokes the live application repository before the
  Activity Compose rule; it no longer imports `File` or deletes storage
  behind DataStore's cache.

Validation:

- `testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug` passed.
- 21 JVM tests remain green; all four instrumentation tests compile.
- Lint remains at 0 errors and 3 intentional dependency-version warnings.
- `assembleRelease` passed through R8, Compose mapping, release lint, resource
  shrinking, and packaging.
- `git diff --check` passed.
- Debug APK SHA-256:
  `f7f5a4550f07dd998a48d51570065417f1b1a907bded78ae1347f598d8814ea1`.
- Instrumentation APK SHA-256:
  `1f1dc6773f6208ef85311c8e8035eea1c76aa010eeeb80cbf334a22dc2267334`.
- Unsigned release APK SHA-256:
  `ae5d0cb6e6381c49857ff7697527d83d2bbcf3fe340e547e319734d91334f7d9`.

The new thread is ready to resolve after this fix is pushed.
