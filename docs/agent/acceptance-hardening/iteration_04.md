# Acceptance iteration 04

Branch: `agent/acceptance-hardening`

Status: automatic-review fixes implemented; rereview pending

## Kind acceptance advocate

The GitHub reviewer did exactly what this policy is meant to invite: it looked
past a green compilation result and found two ways the journey could claim more
than it proved.

1. The first-run test already selected a real item, waited for DataStore, and
   checked recall, so the intended product path was strong.
2. The management journey already exercised default, custom, and reset palette
   actions through real UI controls.
3. The reviewer identified the remaining assertion/control gaps precisely,
   without proposing product bloat or architecture churn.
4. Both fixes live entirely in instrumentation code; production behavior stays
   small and stable.

## Impossible-to-please acceptance critic

The automatic review raised two actionable P2 findings, both valid.

### C1 — First-run could still be bypassed

Persisted app state or method order could remove the `Skip` node, causing the
setup block and its final Shake Shack recall assertion to be skipped. A test
named as end-to-end first-run acceptance must arrange incomplete setup before
the Activity launches and must fail, not adapt, when that UI is absent.

### C2 — Palette actions were clicked but not verified

The journey chose Amethyst and later restored defaults, but name/category
assertions alone could pass if palette saving or reset wiring regressed. It must
assert selected semantics for the Ruby catalog default, the persisted Amethyst
override after reopening, and Ruby again after reset.

No other automatic-review findings were posted for reviewed commit
`f954b8217b`.

## Seasoned software architect

Use test-only state control and semantic assertions:

1. Wrap the Activity Compose rule in an outer `ClearFirstRunStateRule`. For the
   first-run method only, delete the exact Preferences DataStore file before the
   inner Activity rule launches.
2. Give the two app journeys stable alphabetical names and enforce JUnit name
   ordering. The first-run journey can also run independently because its outer
   rule clears state by method name.
3. Remove all conditional setup and rapid-add branches. With isolated state,
   setup, Add, and persisted Shake Shack recall are mandatory assertions.
4. Reuse the editor's RadioButton selected semantics. Assert Ruby before
   customization, Amethyst after saving/reopening, and Ruby after reset/reopen.
5. Recompile the entire debug matrix, record the updated instrumentation APK,
   push, resolve both exact review threads, and request rereview.

## Senior software developer

Implemented the plan in `FaveitJourneyTest.kt`:

- Added an outer, instrumentation-only rule that clears only
  `files/datastore/faveit_preferences.preferences_pb` before the first-run
  Activity launches.
- Enforced `NAME_ASCENDING` order and renamed the journeys with `a`/`b`
  prefixes.
- Made setup, Add, persisted Shake Shack recall, and all first-run assertions
  unconditional.
- Added selected/not-selected assertions for Ruby → Amethyst → Ruby across
  customize and reset.

Validation:

- `testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug` passed.
- 21 JVM tests remain green.
- All four instrumentation tests compile with the new rule and assertions.
- Lint remains at 0 errors and 3 intentional dependency-version warnings.
- `git diff --check` passed.
- Updated instrumentation APK SHA-256:
  `135ff2f6a8196868781f4cc205108e36082c58a99d4994cde5eed1b530cb8628`.

Both review threads are ready to resolve after the fix commit is pushed.
