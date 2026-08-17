# Acceptance iteration 02

Branch: `agent/delight-install-readiness`

Status: runtime defect fixed; complete Android suite passed; visual audit pending

## Kind acceptance advocate

An official API 30 Google APIs guest was bootstrapped and the app and test APKs
were installed directly. Four of five tests passed on the first complete runner
attempt. Setup selection, management, reminder controls, and the accessible
delight component all behaved correctly. Most importantly, the runner targeted
`com.faveit.app`; an earlier package-resolution failure from an unhealthy guest
was diagnosed rather than misreported as an app failure.

## Impossible-to-please acceptance critic

The end-to-end rapid-add journey failed at the new saved confirmation. The
favorite itself was persisted and the add control changed state, but the setup
confirmation still occupied the single Snackbar queue. A user who immediately
searched and added a second favorite could wait behind stale feedback. Delight
that arrives late is friction, and this violates G3.

The fix cannot weaken the ten-second test, add sleeps, or show success before
persistence. The latest completed action must replace obsolete feedback.

## Seasoned software architect

Keep repository-snapshot reconciliation as the source of truth. Immediately
dismiss any currently displayed Snackbar just before showing the newly
reconciled message. This preserves honest success semantics while making
feedback recency deterministic. Rebuild, reinstall, and rerun the same full
five-test runner.

## Senior software developer

Updated both persisted-notice paths to dismiss stale Snackbar data before
showing the current message. No timeout or assertion was loosened.

Runtime validation on the API 30 guest then completed:

- `OK (5 tests)` in 233.543 seconds under software CPU/GPU emulation.
- First-run selection used `Start now` and asserted setup confirmation.
- `In N Out` search/add asserted `In-N-Out is in your favorites`.
- Activity recreation proved the add survived before restaurant recall.
- Customize, category move, gem change, reset, remove, and rediscovery passed.
- Setup, rapid-add/reminder, and live-region semantics passed.

The critic accepted the behavioral gate but still required clean-install and
visual evidence before recommending the user's time.
