# Faveit roadmap

This is a product-direction record, not a promise that every idea should be
built. Faveit's priorities remain speed, cognitive simplicity, recall,
delight, and user authority. New work must not turn the app into advertising,
surveillance, or an engagement feed.

## Delivered: adaptive catalog iteration

- 210 specific, curated choices in each bundled category.
- A reusable local search index sized and tested against the full catalog.
- Transparent, local, preference-led related/exploratory setup picks.
- Unambiguous one-tap add, remove, re-add, and edit paths that preserve tile
  position.

## Candidate: nested and contextual favorites

Bethany suggested that a favorite often has a more specific favorite inside
it. Examples include:

- a favorite drink at Swig;
- a favorite menu item at a restaurant;
- a favorite actor or scene in a movie;
- a favorite character or quote from a book;
- a favorite song on an album or level in a game.

A future design could let a user drill into a favorite and add sub-favorites.
The open product question is whether these should be free-form child entries,
fixed type-specific fields, or a small hybrid. That decision needs observation
of real usage before implementation; a rigid schema could become tedious,
while completely free-form data could hurt recall and scannability.

## Candidate: family preference network

Bethany also suggested a consent-based family view. A daughter, son, partner,
or other family member could share selected favorites with a parent's app.
Potential family uses include:

- seeing each person's preferences without overwriting individual authority;
- finding overlap for family movie night, meals, games, or activities;
- suggesting choices that fairly represent the whole group;
- optionally using a maps or driving-route integration to find shared
  favorites nearby or along a route.

This is deliberately not part of the current iteration. It introduces
identity, consent, revocation, child privacy, synchronization, conflict
resolution, location handling, and group-ranking questions. Any future design
must make sharing selective and reversible, keep private favorites private,
avoid ranking one family member as more valuable, and never accept sponsored
placement. A local export/share-code prototype should be evaluated before
accounts or cloud infrastructure.

## Other candidates

- User-created items when the bundled catalog has no match.
- Permission-free custom tile photos with durable cleanup behavior.
- Versioned catalog imports and updates that preserve stable IDs and local
  customizations.
- User-controlled reminder cadence and quiet hours.
- Documented import/export for backup and personal AI-assisted preparation.
- Situation shortcuts such as “pick lunch” only after real-world observation
  shows they improve recall without becoming a feed.
- Optional baseline profiles only if physical-device macrobenchmarks show a
  meaningful startup or lookup benefit.

## Product gates for roadmap work

Before promotion into an iteration, a proposal should answer:

1. Does it make the user's own preferences easier to capture or recall?
2. Can the common path remain faster and simpler than it is today?
3. What personal data is introduced, where does it live, and who controls it?
4. Can it work without sponsorship, affiliate bias, or engagement pressure?
5. What evidence would prove that it delights rather than distracts?
