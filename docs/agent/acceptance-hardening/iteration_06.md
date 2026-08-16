# Acceptance iteration 06

Branch: `agent/acceptance-hardening`

Status: third rereview fix implemented; rereview pending

## Kind acceptance advocate

The third automatic review confirmed that the live DataStore reset and the
expanded customization assertions solved the prior findings. It then sharpened
the first-run evidence one final step by distinguishing Compose idleness from
repository readiness on a cold asynchronous preference read.

The production app remained untouched by this finding. The correction makes the
acceptance journey patient enough for valid slow-device behavior while retaining
a strict ten-second failure bound.

## Impossible-to-please acceptance critic

One new P2 finding was valid.

### C1 — The first setup assertion raced asynchronous loading

The outer rule clears the active DataStore before activity creation, but
`FaveitRoot` initially renders a loading label while the repository emits its
first snapshot. Compose can be idle in that state. Calling `assertIsOff()`
immediately therefore does not retry until the setup node appears and can fail
on a correct cold or slow device.

The test must wait for `setup_item_restaurant_shake_shack` to exist before
asserting its unselected state.

## Seasoned software architect

1. Keep the awaited live-DataStore reset from iteration 5.
2. Add a bounded Compose semantics poll immediately after initial idle and
   before the first setup assertion.
3. Poll the stable setup tile tag already used by the journey; do not add sleeps
   or weaken any assertions.
4. Recompile instrumentation tests and rerun the complete debug and release
   validation matrix.

## Senior software developer

Implemented the readiness gate with `composeRule.waitUntil(timeoutMillis =
10_000)`, polling until the tagged Shake Shack setup tile exists. The following
`assertIsOff()`, click, persistence poll, and `assertIsOn()` remain
unconditional.

Validation:

- `testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug` passed.
- 21 JVM tests remain green; all four instrumentation tests compile.
- Lint remains at 0 errors and 3 intentional dependency-version warnings.
- `assembleRelease` passed through R8, Compose mapping, release lint, resource
  shrinking, and packaging.
- Debug APK SHA-256:
  `f7f5a4550f07dd998a48d51570065417f1b1a907bded78ae1347f598d8814ea1`.
- Instrumentation APK SHA-256:
  `7d580d4b990e00271a603feb656e8ec0863a7cde9fdab448798485e6dcde3ecf`.
- Unsigned release APK SHA-256:
  `6c974685bced99b3f434909d57b59ccb464c19679ed7ac0480b2803a2f1cf893`.

The thread is ready to resolve after this fix is pushed.
