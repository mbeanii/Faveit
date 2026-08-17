# Acceptance iteration 01 — catalog breadth and curation

Date: 2026-08-17
Branch: `agent/adaptive-catalog`
Base: `95ff93cf5837c70b89311c3ceb825e18cdc4daad`

## Kind acceptance advocate

The expansion fulfills the central request in measurable terms: every one of
the eight categories now has 210 concrete entries, for 1,680 total. The first
12 choices are recognizable heads of different facets rather than twelve
variations on one genre. Restaurants include Swig and other specific drink
shops; media spans eras and genres; games include video and tabletop works;
activities are actions; foods are dishes.

The research record is unusually good for a prototype. It names the popularity
and cultural signals used, explains where manual breadth curation was required,
and avoids commercial artwork, copied descriptions, sponsorship, and ranking
claims. The generated gemstone treatment preserves Faveit's delight without
turning the catalog into an asset-licensing project.

## Impossible-to-please acceptance critic

Breadth alone is not acceptance. The catalog must pass all of these gates:

1. Exactly hundreds per category, with no category quietly underfilled.
2. No duplicate IDs or duplicate case-insensitive names inside a category.
3. No invented venues, vague playlists, umbrella placeholders, or filler used
   merely to reach a count.
4. Meaningful facet diversity and complete tag/popularity metadata.
5. Deterministic regeneration from a readable canonical source.
6. Source provenance that does not imply an objective universal ranking.
7. Existing concrete favorite IDs must survive an in-place update.
8. Retiring old filler must be explicit rather than accidental data loss.

## Seasoned software architect

Use one reviewed Python catalog source organized as 21 ten-item facets per
category. Generate JSON deterministically and reject malformed facet sizes,
duplicate names, duplicate IDs, invalid metadata, and missing legacy mappings
before output. Keep popularity category-local and curation-based. Carry
explicit stable-ID overrides for shipped concrete choices; document the 14
invented/vague entries intentionally retired. Back this with a strict Android
asset contract test and a human-readable provenance document.

## Senior software developer

Implemented `tools/catalog/generate_catalog.py`,
`app/src/main/assets/catalog.json`, and
`docs/catalog_sources.md`. The loader now requires facet, tags, and positive
popularity. Contract tests require exactly 1,680 entries, 210 per category, 21
facets, ranks 1 through 210, unique IDs/names, non-empty metadata, all shipped
concrete IDs, and rejection of known filler/prefix regressions.

Audit result: 1,680 total; 210 in every category; 21 facets in every category;
1,680 unique IDs; no duplicate category-local names; no empty tags or duplicate
tags. All 82 concrete shipped IDs remain; 14 filler entries are intentionally
retired.

## Critic verdict

The catalog gates pass. Continue. One remaining migration objection becomes the
next iteration's gate: a stable ID is insufficient if an update silently
changes the familiar emoji or gem palette of an existing favorite.
