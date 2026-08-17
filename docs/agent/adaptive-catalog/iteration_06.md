# Acceptance iteration 06 — compatibility recovery and baseline polish

Date: 2026-08-17
Branch: `agent/adaptive-catalog`
Pull request: [#4](https://github.com/mbeanii/Faveit/pull/4)

## Kind acceptance advocate

The first review-resolution pass protected favorite IDs, local customization,
setup browsing state, and accessibility semantics. Its rereview did exactly
what a demanding gate should do: it found five deeper upgrade and
minimum-platform cases without disputing the iteration's central achievement.

The expanded catalog remains specific, fast, deterministic, and locally
adaptive. The new one-tap controls still behave as promised. This pass makes
that experience safer for the people most likely to notice regressions:
existing users and users on Faveit's oldest supported Android baseline.

## Impossible-to-please acceptance critic

The exact rereview head still had five release blockers:

1. Retained items lost shipped aliases such as `botw`, `murderbot`, and
   `gbbo`, breaking learned recall queries.
2. Hidden compatibility favorites inflated setup progress and could suppress
   the final discoverable choices.
3. A retired favorite became impossible to recover after removal even though
   the editor promised search or Discover More.
4. Newer emoji could render as missing-character boxes on API 23 devices that
   do not have a modern OEM font.
5. A category red-X removal lost its pinned position after Activity
   recreation.

The critic also required that recovery eligibility not become a back door that
exposes rejected filler to every new user.

## Seasoned software architect

Treat recall vocabulary as migration data keyed by stable catalog ID. Merge all
shipped aliases into the generated record, independently of current names.

Persist a compact set of IDs that the user has genuinely favorited. Current
favorites implicitly migrate into that set; add and toggle record ownership;
remove records ownership only if the item was actually present. Build one
immutable search index, but admit a hidden entry only when its ID is in that
user-owned set. Apply the same eligibility in Discover More using the effective
locally customized category.

Count setup progress from discoverable IDs only. Save category pinned IDs with
Compose's saveable-state registry. Constrain generated catalog glyphs to the
set already shipped on the API 23 baseline, with category-specific fallbacks
and a generation invariant.

## Senior software developer

Implemented all five fixes plus the critic's ownership hardening:

- all 82 retained items preserve all 132 shipped aliases;
- hidden records stay absent for new users but become searchable and
  rediscoverable for their previous owner;
- effective local category/name/gem customization survives removal and powers
  recovery;
- setup More-picks progress ignores hidden upgrade records;
- category pinned removal state survives Activity recreation;
- the generator rejects any output outside the baseline glyph allowlist.

Added JVM contracts for alias preservation, glyph safety, and owner-scoped
hidden search. Expanded Compose coverage for hidden setup favorites and the
on-device journey for pinned recreation plus retired-favorite recovery.

The exact-source matrix passed: 35 JVM tests, debug APK, seven-test
instrumentation APK, lint, R8, resource shrinking, and release packaging.
Lint reports 0 errors and only the same three dependency-version warnings.
`git diff --check` passes. A direct comparison with `origin/main` confirms
zero missing aliases across all retained items.

The new Android tests compile. Runtime execution was attempted, but API 36
requires unavailable KVM and the API 30 software emulator now exits before
boot because its installed launcher cannot load `libX11-xcb.so.1`. No app
assertion ran, so this is recorded as a host validation limitation rather than
a pass. The earlier substantive API 30 suite remains 5/5 green.

## Critic verdict

No reasonable implementation objection remains from this pass. Every rereview
finding has a narrow fix, a durable invariant or regression, and a truthful
validation receipt. Request automatic rereview on the new exact commit and
merge only after it returns without further actionable findings.
