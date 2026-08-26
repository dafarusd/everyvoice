package org.everyvoice.aac

import org.everyvoice.aac.engine.Search
import org.everyvoice.aac.engine.Tile
import org.everyvoice.aac.engine.Vocabulary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards tile identity.
 *
 * Search results used to be keyed by label plus spoken text. Twelve core
 * words appear in two categories each, so searching "help", "go", "no" or
 * "more" produced two items with the same key and Compose threw. These
 * tests fail if anything reintroduces text-based identity.
 */
class TileKeyTest {

    @Test
    fun `same word in two categories gets two different keys`() {
        val fromCore = Tile("help", "help", id = 7)
        val fromActions = Tile("help", "help", id = 42)
        assertNotEquals(fromCore.stableKey, fromActions.stableKey)
    }

    @Test
    fun `key is stable across equal tiles`() {
        assertEquals(Tile("water", "water", id = 3).stableKey, Tile("water", "water", id = 3).stableKey)
    }

    @Test
    fun `unsaved tiles still get a key`() {
        assertTrue(Tile("water").stableKey.isNotEmpty())
    }

    @Test
    fun `search results over stored tiles have unique keys`() {
        // Mirrors what the view model builds: every tile carries its row id.
        var nextId = 1L
        val stored = Vocabulary.seed().flatMap { category ->
            category.tiles.map { it.copy(id = nextId++) }
        }
        for (query in listOf("help", "go", "no", "more", "hot", "cold", "work")) {
            val keys = Search.query(stored, query).map { it.stableKey }
            assertEquals("Duplicate keys for query \"$query\"", keys.size, keys.toSet().size)
        }
    }

    @Test
    fun `the duplicate words this guards against really are in the seed`() {
        // If this ever fails the vocabulary changed, not the bug. Core words
        // belonging to several categories is correct AAC design; keep it.
        val pairs = Vocabulary.seed().flatMap { c -> c.tiles.map { it.label to it.speakText } }
        assertTrue("Expected duplicate labels across categories", pairs.size > pairs.toSet().size)
    }
}
