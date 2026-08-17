# Catalog research and provenance

## Scope

The bundled catalog contains 1,680 entries: 210 specific choices in each of
Restaurants, Music, Movies, TV, Books, Games, Activities, and Foods. It is a
broad prototype catalog, not a claim to be a universal or objective canon.

Research was performed on 2026-08-17. Sources were used as independent signals
for recognizability, enduring affection, current audience interest, and facet
coverage. No source was copied wholesale and no source's descriptions, images,
ratings, or proprietary metadata are bundled.

## Research signals

- Restaurants: [QSR 50 2025](https://www.qsrmagazine.com/story/top-50-fast-food-chains-ranked-2025/)
  supplied a data-backed signal for widely recognized U.S. limited-service
  brands. Regional icons, international dining, cafes, casual dining, and
  destination restaurants were then curated separately so “restaurants” did
  not mean only fast food.
- Music: [Billboard Canada 2025 year-end artists](https://ca.billboard.com/charts/year-end-2025/top-artists)
  supplied a current popularity signal and the
  [RIAA Gold & Platinum program](https://www.riaa.com/gold-platinum/?tab_active=awards_by_artist)
  supplied a long-running commercial-recognition signal. Genre and era
  coverage prevents current pop from crowding out jazz, classical, country,
  Latin, K-pop, soul, rock, electronic, folk, reggae, and funk.
- Movies: the [American Film Institute's 100 Movies](https://www.afi.com/afis-100-years-100-movies-10th-anniversary-edition/)
  and broader [AFI lists](https://www.afi.com/afi-lists/) supplied enduring
  American-film signals. Modern, international, animated, documentary, family,
  genre, and cult choices were added rather than treating one canon as the
  whole category.
- TV: [Nielsen's 2025 Streaming Unwrapped](https://www.nielsen.com/data-center/top-streaming-shows-artey-awards/)
  supplied recent measured-viewing signals. The catalog balances those with
  enduring sitcoms, dramas, animation, international series, reality,
  documentary, food/travel, and limited series.
- Books: [PBS's Great American Read results](https://www.pbs.org/the-great-american-read/results/)
  supplied a public favorite-vote signal, while the
  [Library of Congress America Reads exhibition](https://www.loc.gov/exhibits/america-reads/overview.html)
  supplied a cultural-impact signal. Genre fiction, children's books, poetry,
  graphic novels, essays, science, history, memoir, psychology, and practical
  nonfiction broaden the result.
- Video games: [Nintendo's official lifetime platform sales data](https://www.nintendo.co.jp/ir/en/finance/hard_soft/)
  and [The Game Awards game-of-the-year selections](https://thegameawards.com/nominees/game-of-the-year)
  supplied mass-audience and contemporary critical signals.
- Tabletop games: the independent
  [Spiel des Jahres archive](https://www.spiel-des-jahres.de/en/games/)
  supplied a durable family/tabletop signal. The prototype also includes
  established strategy, party, classic, card, and role-playing choices.
- Activities: the [Outdoor Industry Association 2025 participation report](https://outdoorindustry.org/2024-outdoor-participation-trends-report/)
  identified growing gateway activities, and the
  [U.S. Bureau of Labor Statistics American Time Use Survey](https://www.bls.gov/tus/)
  supplied a broad official leisure-and-sports frame. Creative, social,
  culinary, musical, mindful, and indoor activities were deliberately added
  so the list is not exercise-only.
- Foods: the
  [National Restaurant Association 2025 culinary forecast](https://restaurant.org/education-and-resources/resource-library/whats-hot-in-2025-report-cites-ease-innovation-and-lots-of-flavor/)
  supplied current cuisine and flavor signals. The catalog balances trends
  with recognizable dishes across regions, meal types, dietary shapes, and
  desserts.

## Curation rules

Each entry had to be a concrete thing a person could plausibly say they love.
Invented businesses, vague mood playlists, placeholder labels, umbrella filler
such as “Fun Outdoors,” and near-duplicate spellings were rejected. Activities
are phrased as specific actions; foods as dishes; Restaurants as named
establishments or brands; Music as named artists, bands, ensembles, or
composers; and Movies, TV, Books, and Games as named works, series, or
franchises where the series itself is a natural preference unit.

Every category has 21 meaningful facets with 10 entries each. Within a facet,
entries move broadly from more recognizable to more specialist. The first
setup batch takes popular heads from distinct facets. Subsequent batches use
the original facet/tag labels for local content similarity.

## Licensing and affiliation

Names and titles are factual identifiers. Faveit does not bundle commercial
artwork, source descriptions, logos, ratings, reviews, or copied taxonomy
text. Emoji and gemstone surfaces are rendered locally. Inclusion is not an
endorsement, partnership, sponsorship, or affiliate relationship, and there
is no paid placement.

The canonical editable source is
`tools/catalog/generate_catalog.py`; `app/src/main/assets/catalog.json` is
generated output. The generator rejects wrong facet sizes, duplicate names, and duplicate stable
IDs before writing the asset. It also carries explicit overrides for concrete
IDs shipped in the original MVP, so an in-place app update does not orphan those
favorites. Invented venues and vague playlist labels were deliberately removed.

## Known coverage limits

The current research base and likely prototype audience create a U.S.-leaning
catalog, especially for restaurant brands and English-language media. Global
works and cuisines are included, but location, language, accessibility,
dietary needs, age, and regional availability are not personalized. A later
versioned catalog process should add regional packs and user-created items
without silently deleting or renumbering existing favorites.
