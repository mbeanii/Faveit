# Deferred beyond the adaptive-catalog iteration

These are intentional boundaries, not unfinished requirements for the current
vertical slice. Product candidates are developed further in
[`docs/roadmap.md`](docs/roadmap.md).

## Highest value next

1. Repeat the automated and hands-on journeys on physical low-, mid-, and
   high-end Android devices; measure cold-start/search latency and verify
   TalkBack traversal, font scaling, haptic character, rotation, and delayed
   notification delivery.
2. Establish a versioned catalog update/import process with provenance, stable
   ID review, migration receipts, and regional/language packs. The current
   researched catalog is broad but U.S.- and English-language-leaning.
3. Evaluate discovery quality with consenting users. Tune facets, tag overlap,
   exploration, popularity bands, and batch size only against explicit measures
   of useful recall and delight—not time-on-app.
4. Measure startup and conversational lookup with Macrobenchmark on physical
   hardware, then add a baseline profile only if measurements justify it.
5. Add a permission-free Android photo picker, crop/contrast treatment, local
   URI persistence, and an orphan-file cleanup policy for custom tile images.

## Later product vision

- User-created catalog items/categories when search has no bundled result.
- Nested/contextual favorites, including a drink or menu item within a favorite
  restaurant; the custom-versus-fixed data model remains a research question.
- Consent-based family sharing and fair group suggestions, with an optional
  privacy-preserving nearby/route experience.
- A documented JSON import/export format.
- Optional user-driven AI-agent import prompts. No external AI SDK should be
  embedded merely to support this.
- More situation-oriented recall shortcuts only after real user research.

## Explicitly not planned

- Accounts or a mandatory backend merely to drive recommendations.
- Sponsored favorites, affiliate links, ads, engagement feeds, or tracking.
- Automated taste judgments that override a user’s explicit choices.
