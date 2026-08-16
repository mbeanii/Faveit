# Deferred beyond MVP

These are intentional boundaries, not unfinished requirements for the current
vertical slice.

## Highest value next

1. Run the compiled Compose journey on physical low-, mid-, and high-end
   Android devices; verify cold-start/search latency, TalkBack traversal, font
   scaling, haptic character, rotation, and the delayed notification path.
2. Add a permission-free Android photo picker, crop/contrast treatment, local
   URI persistence, and a clear orphan-file cleanup policy for users who want
   custom tile images.
3. Measure startup and conversational lookup with Macrobenchmark, then add a
   baseline profile if measurements justify it.
4. Expand the catalog through a versioned import pipeline with provenance and
   licensing metadata. Preserve stable IDs and merge rather than overwrite
   local user state.
5. Add reminder controls for cadence and quiet hours while keeping the default
   conservative and opt-in.

## Later product vision

- User-created catalog items/categories when search has no bundled result.
- A documented JSON import/export format.
- Optional user-driven AI-agent import prompts. No external AI SDK should be
  embedded merely to support this.
- More situation-oriented recall shortcuts only after real user research.

## Explicitly not planned

- Accounts or a mandatory backend.
- Sponsored favorites, affiliate links, ads, engagement feeds, or tracking.
- Automated taste judgments that override a user’s explicit choices.
