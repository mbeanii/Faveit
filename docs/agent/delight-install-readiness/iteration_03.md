# Acceptance iteration 03

Branch: `agent/delight-install-readiness`

Status: clean-install and visual defects addressed; final receipt pending

## Kind acceptance advocate

The debug APK was uninstalled from the disposable guest, freshly installed,
and launched into first-run setup. The new screen has a clear promise—“For the
blank-mind moment”—and visibly offers value after a single favorite. The tiles
feel jewel-like without using external imagery, and the 2-by-4 Home grid makes
all categories scannable at once.

Two actual 1080-by-1920 Android captures now show the corrected first-run and
Home experiences:

- [`evidence/setup-api30.png`](evidence/setup-api30.png)
- [`evidence/home-api30.png`](evidence/home-api30.png)

## Impossible-to-please acceptance critic

The evidence process surfaced two objections.

1. On this host with no `/dev/kvm`, framebuffer capture starved Android's own
   System UI long enough to raise an ANR overlay. An obscured screenshot is not
   product evidence and must not be committed as though it were.
2. Once an unobscured Home capture was obtained, the `Faveit` wordmark and
   other unspecified primary text rendered near-black against the dark app
   background. The gem grid was attractive, but the first visual hierarchy
   failed basic legibility. G4 remained closed.

## Seasoned software architect

1. Treat System UI ANRs as disclosed unaccelerated-emulator infrastructure,
   not a product pass or failure. Suppress only guest error dialogs for the
   evidence capture, retain the behavior suite as the product authority, and
   commit only unobscured images.
2. Set the root Compose content color explicitly to the dark theme's
   `onBackground` using `CompositionLocalProvider`. This fixes every
   unspecified heading consistently without duplicating colors across screens.
3. Rebuild, reinstall, restart, and visually recapture both Setup and Home.
   Inspect the images rather than relying on theme configuration alone.

## Senior software developer

Implemented the root content-color provider and rebuilt/reinstalled the app.
The final captures were visually inspected at reduced and full source
resolution:

- Setup now has a crisp ivory heading, readable helper text, an emerald guidance
  mark, distinct ruby/emerald tiles, and an unclipped fixed footer.
- Home now has a crisp ivory wordmark, legible subtitle/search/section label,
  all eight categories above the fold, and one visibly counted Restaurant
  favorite.
- No System UI overlay is present in either committed image.

The critic accepted the visual gate and required the final clean matrix,
permission receipt, install handoff, and exact-artifact runtime disposition.
