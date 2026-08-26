package org.everyvoice.aac.engine

/**
 * Search over tiles.
 *
 * A user who can type a few letters should reach a word faster than paging
 * through categories. Ranking is intentionally simple and predictable:
 * exact match first, then label-starts-with, then label-contains, each
 * group alphabetical. Predictability matters more than cleverness here —
 * motor-impaired users build muscle memory around stable orderings.
 */
object Search {

    fun query(tiles: List<Tile>, query: String): List<Tile> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        val exact = mutableListOf<Tile>()
        val prefix = mutableListOf<Tile>()
        val contains = mutableListOf<Tile>()

        for (tile in tiles) {
            val label = tile.label.lowercase()
            when {
                label == q -> exact.add(tile)
                label.startsWith(q) -> prefix.add(tile)
                label.contains(q) -> contains.add(tile)
            }
        }

        val byLabel = compareBy<Tile> { it.label.lowercase() }
        return exact.sortedWith(byLabel) +
                prefix.sortedWith(byLabel) +
                contains.sortedWith(byLabel)
    }
}
