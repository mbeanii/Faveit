# Acceptance iteration 02

Branch: `agent/acceptance-hardening`

Status: implementation complete; proceed to critic iteration 3

## Kind acceptance advocate

Iteration 1 converted every named weakness into product or test improvements,
and those victories are visible:

1. Setup gems now tell assistive technology exactly what they are: checked or
   unchecked choices. The visible check, tactile response, and semantic state
   finally describe one coherent interaction.
2. The defining rapid-add action now feels like Faveit. Both the full row and
   its add control use the same crisp click, confirmation haptic, and immediate
   favorite-state announcement.
3. Reminder state is legible without color or icon recognition.
4. The local-first projection contract is no longer implicit. Tests prove that
   defaults, favorite IDs, renamed items, moved categories, recolored gems, and
   retired catalog IDs resolve safely.
5. The exact `Remember In-N-Out?` promise is executable test data, including a
   locally customized Unicode name.
6. Favorite management now has a realistic compiled journey across every MVP
   operation rather than isolated callback coverage.
7. Debug, instrumentation, lint, and minified release artifacts all build from
   the hardened code. The improvements did not bloat the architecture or drift
   into post-MVP scope.

## Impossible-to-please acceptance critic

The implementation is materially stronger, but this iteration is not the
stopping point.

### C1 — First-run selection is still conditionally skipped in the app journey

The journey clicks `Skip` when setup appears. It does not prove the new toggle
semantics, selection callback, cross-category progression, or that a setup
selection survives into recall. Because DataStore may already mark setup
complete on a reused device, this path also needs deterministic component-level
coverage.

### C2 — Accessibility improvements have no direct semantic assertions

Compilation does not prove that Compose exposes checked/unchecked state, the
rapid-add state description, or the reminder on/off description. These are the
very contracts added in iteration 1 and should be asserted as such.

### C3 — Lint still reports two guarded API warnings

The notification permission constant is safe because all pre-API-33 paths make
the request branch unreachable, but the unexplained warnings obscure the three
intentional dependency-version notices. The guarded usage should be narrowly
and explicitly suppressed.

### C4 — Handoff evidence is stale

`START_HERE.md` still reports 16 host tests, the old APK digest, one compiled
journey, and the earlier emulator attempt. Acceptance documentation must match
the exact artifacts it hands off.

### C5 — No device test has actually executed

This remains the highest evidence gap. A fresh attempt should use the bundled
API 36 image, capture each readiness failure precisely, and distinguish a guest
boot/install failure from an application test failure.

## Seasoned software architect

Address the critic without adding product scope:

1. Extend the real journey so a fresh install selects Shake Shack during setup,
   verifies checked state, completes setup, and later finds that selection in
   Restaurants alongside the rapid-added In-N-Out favorite.
2. Add isolated Compose tests that deterministically exercise setup toggle
   semantics and category progression, plus rapid-add and reminder state
   descriptions. These tests must not depend on persisted app state.
3. Apply a function-level `InlinedApi` suppression at the already guarded
   notification-permission composition root. Do not suppress other lint
   categories.
4. Rebuild all artifacts and update `START_HERE.md`, README, iteration records,
   test counts, warnings, hashes, and device evidence.
5. Create an isolated temporary API 36 AVD and make bounded connected-test
   attempts. If pure software emulation cannot maintain Android framework
   services without KVM, terminate it cleanly and preserve the physical-device
   gate in TODO and START_HERE.

## Senior software developer

Implemented the architecture:

- The app journey now selects `restaurant_shake_shack` on first run, asserts
  off-to-on toggle state, finishes setup, and verifies the favorite in the
  Restaurants recall grid.
- `FaveitSemanticsTest` adds two state-independent Compose tests. One verifies
  setup toggle semantics and progression to Music; the other verifies rapid-add
  `Not a favorite` to `Favorite` state and reminder off-to-on descriptions.
- `FaveitRoot` has a narrow `@SuppressLint("InlinedApi")` annotation for the
  guarded notification permission constant. Lint now reports only the three
  deliberately pinned dependency notices.
- The instrumentation APK compiles four tests: two full app journeys and two
  isolated semantic/component journeys.
- The 21 host tests, instrumentation compilation, and debug lint all pass.

A current device attempt was made rather than inferred:

1. An isolated API 36 AVD was created in `/tmp` and launched with SwiftShader
   and software CPU emulation because the host has no `/dev/kvm`.
2. The guest moved from ADB offline to connected. The first Gradle attempt
   executed zero tests because ddmlib could not read properties and labeled it
   `Unknown API Level`.
3. After the guest reported API 36, the second attempt reached APK installation
   but executed zero tests because the package service disappeared during a
   `system_server` restart.
4. Guest logs showed the new system server booting; there was no Faveit test
   failure to diagnose. Repeating the unstable pure-TCG boot would not turn it
   into credible device evidence, so the emulator was stopped cleanly.

Physical performance, TalkBack traversal, haptic character, screenshots, and
notification delivery therefore remain an explicit real/accelerated-device
acceptance gate. The code and executable tests needed for that gate are present.
