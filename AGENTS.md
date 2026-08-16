# Faveit Agent Instructions

These instructions govern the entire repository.

## Standard merge policy

Every implementation effort must follow
[AGENTS_CRITIC_ADVOCATE_ACCEPTANCE_LOOP_AND_MERGE.md](AGENTS_CRITIC_ADVOCATE_ACCEPTANCE_LOOP_AND_MERGE.md).
That advocate/critic/architect/developer acceptance loop, its per-iteration
records, the pull request, the automatic review loop, and review-satisfied
merge are Faveit's standard merge policy unless the user explicitly replaces
it for a particular effort.

## Goal

Build the best genuinely working greenfield Android MVP possible for Faveit
with minimal or zero supervision. Faveit remembers things a user already likes
and makes those preferences immediately available when recall fails on the
spot.

## Autonomy rules

1. Make reasonable product, UX, architecture, implementation, and library
   decisions autonomously.
2. Do not ask the user ordinary design or implementation questions.
3. If something is ambiguous, choose a sensible interpretation, document the
   decision, and proceed.
4. If something requires credentials, paid services, cloud infrastructure,
   external accounts, manual configuration, or user intervention, avoid it
   where practical. Substitute a local implementation, fixture, mock, stub, or
   abstraction and document what would eventually replace it.
5. Do not block waiting for the user unless proceeding could cause meaningful
   destructive, security, privacy, financial, or irreversible consequences.
6. Run builds, tests, linters, static analysis, and other validation wherever
   the environment permits.
7. Fix problems discovered rather than merely reporting them.
8. Keep the repository clean and understandable.
9. Maintain README.md, DECISIONS.md for consequential decisions and
   assumptions, TODO.md for deliberately deferred work, and START_HERE.md for
   the final handoff.
10. Optimize for a working vertical slice rather than architectural
    completeness.
11. Do not turn the prototype into a giant framework or speculative platform.
12. Do not implement post-MVP features merely because the product vision
    describes them.

## Technical defaults

Unless a strong reason is discovered otherwise:

- Native Android
- Kotlin
- Jetpack Compose
- Material 3 where appropriate, substantially customized for Faveit's visual
  identity
- Local-first storage
- Room and/or DataStore as appropriate
- No account system
- No backend required for the MVP
- No cloud dependency
- No advertising
- No analytics or tracking SDKs
- No external AI integration in the MVP
- Sensible automated tests

## MVP boundary

Build these journeys end to end.

### 1. First-run setup

- Show several major preference categories.
- Let the user rapidly browse a predefined collection of items in each.
- Let the user tap items they love.
- Persist selections locally.
- Keep setup fast and visually engaging.

### 2. Home

- Make search immediately accessible at the top-right/top area.
- Keep every major category visible without hunting.
- Establish the colorful, gemstone-inspired tile-grid visual language.

### 3. Rapid add

- A user thinking “I love In-N-Out” opens Faveit and searches “In-N-Out.”
- The result clearly identifies it as a restaurant.
- One tap adds it.
- Do not require a form, rating, manual category choice, image upload, or
  similar friction.
- Design toward the complete interaction being extraordinarily fast.

### 4. Lookup and recall

- Tapping Restaurants shows favorite restaurants essentially immediately.
- Use a highly scannable visual grid.
- The interaction must work at conversational speed; this is the core product
  value.

### 5. Favorite management

- Favorites can be removed.
- A favorite can be customized locally.
- Category and visual representation can be changed.
- Defaults can be restored.
- Non-favorites remain discoverable without obscuring favorites.

### 6. Notifications

- Provide basic local notification support.
- Use the format “Remember In-N-Out?”
- Keep behavior simple and non-annoying.
- Do not build recommendation intelligence yet.

## Data

Bundle a useful demonstration/master dataset across Restaurants, Music, Movies,
TV, Books, Games, Activities, and Foods. Do not make the MVP dependent on a
third-party catalog API.

Design the data layer so that a much larger or externally sourced master
catalog can replace or extend the bundled data later. Avoid copyright and
licensing problems: use legally safe bundled, generated, abstract, or
placeholder imagery instead of blocking on commercial artwork.

## Design priorities

In order:

1. Speed
2. Cognitive simplicity
3. Recall and scannability
4. Delight
5. Visual polish
6. Feature count

The app should feel unusually responsive. Its gemstone aesthetic should feel
tactile, glossy, colorful, dimensional, and satisfying rather than childish or
chaotic. Use animation, haptics, sound, depth, and interaction feedback
judiciously. Tiles and buttons may evoke polished emeralds, rubies, amethysts,
gold, diamonds, cylindrical gems, or cut prisms. Never sacrifice speed or
legibility for visual effects.

## Product principle

Faveit is not an advertising, recommendation-sales, sponsorship, affiliate, or
engagement-maximizing engine. The user's own preferences are the authority.
Do not add sponsored content, paid placement, manipulative feeds, or mechanisms
that undermine that principle.

Do not market or gate Faveit specifically as an ADHD or disability app. The
idea may help people who struggle with recall, but the product is an inclusive
preference-recall tool that helps users drive their own choices instead of
having marketing teams drive them.

## Delivery standard

Continue autonomously until the strongest working MVP reasonably achievable in
the environment is complete. Before declaring an effort complete:

- Build the project.
- Run available tests.
- Exercise important flows as far as tooling permits.
- Fix discovered defects.
- Review the implementation.
- Remove placeholder engineering shortcuts that prevent meaningful use.
- Document anything that could not be validated.

START_HERE.md must concisely state:

- what works;
- how to run it;
- what was validated;
- screenshots or other useful evidence when practical;
- known limitations;
- the five most important things the user should inspect on return.

## Complete product concept

Faveit remembers the user's favorite music, restaurants, movies, and other
preferences and surfaces them when the user struggles to recall them.

### Initial setup

The user spends a few minutes scrolling through the standard picture-and-word
grid for each category and selecting their very favorites from a predefined
master list. That selection experience is the MVP.

Later versions may define a standard JSON import format and suggested prompts
for a user's own AI agent: one prompt to select items the agent has heard the
user mention and a more adventurous prompt to suggest likely preferences. A
future import screen may use tactile emerald upload and gold copy buttons.
These AI/import ideas are explicitly post-MVP.

### Maintenance and rapid capture

When something comes to mind, the user must be able to add it immediately. A
lookup for “In N Out” should find “In-N-Out,” label it “Restaurant,” and offer
one-tap add without asking for a rating, category, image, or other form fields.
The target from thought to saved favorite is five seconds or less, including
unlocking, finding the app, opening it, searching, and tapping Add. Search must
therefore be prominent on Home and app startup must be fast.

### UI, delight, and responsiveness

Responsiveness is non-negotiable: a distracted or frustrated user will abandon
the capture. Subtle sound, color, haptics, animation, and reactivity should make
the interface genuinely enjoyable without overdoing stimulation. The visual
identity uses ruby, emerald, amethyst, gold, diamond, and related polished-gem
forms with visible press depth and satisfying feedback.

### Notifications

The MVP notification is simply beautiful, colorful, and restrained:
“Remember In-N-Out?” Later versions may add directions, links, calendar-aware
recommendations, or other context, but the MVP must not.

### Lookup scenario

A colleague asks where the group should go for lunch. The user's mind goes
blank, so they open Faveit and tap Restaurants in less than a second. Home
shows bold category names over intuitive glossy colored/pictured tiles, with
all categories available without required scrolling in the normal layout.

Restaurants opens instantly to a possibly scrolling, highly scannable grid of
the user's favorite restaurants. An In-N-Out burger tile catches the eye at
conversation speed. The user asks, “You guys like In-N-Out?” and the group
goes there.

Within that category view, the user can remove favorites, customize local
names/categories/visuals, restore defaults, and continue into a clearly
separated muted view of non-favorites. Discovery must never obscure the
favorites that make this recall moment work.

## Post-MVP restraint

Directions, links, calendar intelligence, AI integrations, JSON import,
user-created catalog infrastructure, recommendation models, commercial
artwork, and broader platform capabilities remain outside the MVP unless the
user explicitly changes scope. Document them rather than building them
speculatively.
