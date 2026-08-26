package org.everyvoice.aac.engine

/**
 * One tappable thing: a word, a phrase, or a symbol.
 *
 * [label] is what the user reads on the button.
 * [speakText] is what the device says. Usually the same as the label; for
 * symbols or shorthand it carries the full sentence.
 */
data class Tile(
    val label: String,
    val speakText: String = label,
    /**
     * The storage row id this tile came from, or 0 for tiles that have
     * never been written to the database (seed data, test fixtures).
     */
    val id: Long = 0,
) {

    /**
     * Identity for list rendering.
     *
     * A core word legitimately appears in more than one category — "go"
     * belongs in both Core and Actions, and that is correct AAC design, not
     * duplication to be cleaned up. So the label and the spoken text do not
     * identify a tile. Compose throws when two items in a lazy grid share a
     * key, which crashed search on "help", "go", "no" and "more" — the words
     * a user reaches for first.
     */
    val stableKey: String
        get() = if (id != 0L) "id:$id" else "text:$label|$speakText"
}

/** A named group of tiles, shown as its own page. */
data class SeedCategory(
    val id: String,
    val name: String,
    val icon: String,
    val tiles: List<Tile>,
)
