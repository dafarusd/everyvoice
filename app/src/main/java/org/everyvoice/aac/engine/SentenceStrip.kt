package org.everyvoice.aac.engine

/**
 * The sentence strip: the row of tapped tiles waiting to be spoken.
 *
 * Deliberately dumb. It holds tiles, builds the sentence, and nothing else.
 * All intelligence (search, vocabulary, storage) lives elsewhere so this
 * stays testable on any JVM with no Android runtime.
 */
class SentenceStrip {

    private val tiles = mutableListOf<Tile>()

    /** Read-only view of the current strip contents, oldest first. */
    val contents: List<Tile>
        get() = tiles.toList()

    val size: Int
        get() = tiles.size

    val isEmpty: Boolean
        get() = tiles.isEmpty()

    fun add(tile: Tile) {
        tiles.add(tile)
    }

    /** Removes the most recently added tile. Safe on an empty strip. */
    fun removeLast() {
        if (tiles.isNotEmpty()) tiles.removeAt(tiles.lastIndex)
    }

    fun clear() {
        tiles.clear()
    }

    /**
     * Builds the spoken sentence.
     *
     * Joins with single spaces, capitalizes the first letter, and appends a
     * period only if the text does not already end in sentence punctuation.
     * An empty strip produces an empty string — callers should not speak it.
     *
     * If a tile's [Tile.speakText] already ends in sentence punctuation, the
     * tile is treated as a complete utterance and the next tile starts a new
     * sentence with a capital letter.
     */
    fun text(): String {
        if (tiles.isEmpty()) return ""

        val raw = tiles.joinToString(" ") { it.speakText.trim() }
            .replace(Regex("\\s+"), " ")
            .trim()

        // Every path through the app defaults blank spoken text to the
        // label, but a restored backup is data from outside. A strip that
        // says nothing is silence; a strip that throws takes the whole app
        // down mid-sentence.
        if (raw.isEmpty()) return ""

        val sb = StringBuilder(raw.length + 1)
        var capitalizeNext = true
        for (c in raw) {
            if (capitalizeNext && c.isLetter()) {
                sb.append(c.uppercaseChar())
                capitalizeNext = false
            } else {
                sb.append(c)
            }
            if (c == '.' || c == '!' || c == '?') capitalizeNext = true
        }

        val result = sb.toString()
        val last = result.last()
        return if (last == '.' || last == '!' || last == '?') result else "$result."
    }
}
