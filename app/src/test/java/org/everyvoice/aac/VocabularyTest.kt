package org.everyvoice.aac

import org.everyvoice.aac.engine.Vocabulary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the seed content itself. A vocabulary that ships with blank
 * labels, duplicate ids, or silent drift below its promised size is a bug
 * that lands on a child, so it fails the build, not the user.
 */
class VocabularyTest {

    private val seed = Vocabulary.seed()

    @Test
    fun `seed has at least 250 tiles`() {
        val total = seed.sumOf { it.tiles.size }
        assertTrue("Expected at least 250 tiles, found $total", total >= 250)
    }

    @Test
    fun `category ids are unique`() {
        val ids = seed.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `category names are unique and non-blank`() {
        val names = seed.map { it.name }
        assertEquals(names.size, names.toSet().size)
        assertTrue(names.none { it.isBlank() })
    }

    @Test
    fun `every category has an icon and at least five tiles`() {
        for (category in seed) {
            assertTrue("${category.id} has no icon", category.icon.isNotBlank())
            assertTrue(
                "${category.id} has only ${category.tiles.size} tiles",
                category.tiles.size >= 5,
            )
        }
    }

    @Test
    fun `no tile has a blank label or speak text`() {
        for (category in seed) {
            for (tile in category.tiles) {
                assertTrue("Blank label in ${category.id}", tile.label.isNotBlank())
                assertTrue("Blank speakText in ${category.id}", tile.speakText.isNotBlank())
            }
        }
    }

    @Test
    fun `essential self-advocacy words exist`() {
        val all = seed.flatMap { it.tiles }.map { it.speakText.lowercase() }
        val essential = listOf("stop", "no", "help", "yes")
        for (word in essential) {
            assertTrue("Missing essential word: $word", all.any { it.contains(word) })
        }
    }

    @Test
    fun `seed is deterministic across calls`() {
        val first = Vocabulary.seed().flatMap { c -> c.tiles.map { it.label to it.speakText } }
        val second = Vocabulary.seed().flatMap { c -> c.tiles.map { it.label to it.speakText } }
        assertEquals(first, second)
    }
}
