# Acceptance iteration 03 — scale, evidence, and lifecycle rigor

Date: 2026-08-17
Branch: `agent/adaptive-catalog`

## Kind acceptance advocate

The app retains the original conversational-speed shape despite a 17.5x
catalog increase. Search owns reusable preprocessed data; category rendering is
lazy; setup renders 12-item batches; full snapshot projection is no longer UI
thread work. Three fresh API 30 screenshots show that the larger catalog did
not dilute the glossy gemstone identity or hide the eight-category home grid.

The instrumentation journeys are meaningful rather than screenshots posing as
proof. They cross persistence boundaries, recreate the Activity, exercise
search add/remove/re-add, recall Restaurants, rename and move a favorite,
change and reset its gem, remove in place, and restore it.

## Impossible-to-please acceptance critic

Acceptance requires:

1. Full-catalog projection off the main thread.
2. A final build, unit tests, lint, release shrinking, and test APK packaging.
3. One combined device invocation, because individually green tests can hide
   process-lifecycle or cross-test defects.
4. Accessibility-state evidence for toggles, removal labels, reminders, and
   delight announcements.
5. Visual evidence of the new setup and selected red-X state.
6. A post-run crash audit.
7. No unexplained startup/background failure.

## Seasoned software architect

Move repository snapshot mapping to `Dispatchers.Default` and cache item maps,
favorite lists, and custom-name maps in each immutable snapshot. Layer JVM
contracts with Compose semantics tests and two end-to-end Activity journeys.
Run them together on a clean installed API 30 guest and inspect logcat after
the run. Treat every startup exception as a release blocker, even if focused
tests pass.

## Senior software developer

Moved snapshot projection off-main and added cached derived views. Expanded the
device suite to five tests: two persistent journeys and three focused semantics
tests. Captured adaptive setup, selected red-X, and home evidence.

During the first combined run, the process crashed after an individually green
journey. Dropbox identified the cause precisely: reminder reconciliation called
`WorkManager.getInstance` in a process where WorkManager was not initialized.
No acceptance claim was made from the split passes.

## Critic verdict

Not satisfied. The combined-run discovery is a real cold-start defect. Proceed
only after WorkManager initialization has one deterministic owner, the merged
manifest proves automatic initialization is removed, and the entire five-test
suite passes together without a matching logcat crash.
