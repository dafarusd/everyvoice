package org.everyvoice.aac

import org.everyvoice.aac.engine.Search
import org.everyvoice.aac.engine.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchTest {

    private val tiles = listOf(
        Tile("water"),
        Tile("watch"),
        Tile("warm"),
        Tile("hot water"),
        Tile("apple"),
        Tile("Watermelon"),
    )

    @Test
    fun `blank query returns nothing`() {
        assertTrue(Search.query(tiles, "").isEmpty())
        assertTrue(Search.query(tiles, "   ").isEmpty())
    }

    @Test
    fun `exact match ranks before prefix and contains`() {
        val results = Search.query(tiles, "water")
        assertEquals("water", results.first().label)
    }

    @Test
    fun `prefix matches rank before contains matches`() {
        val results = Search.query(tiles, "wat")
        val labels = results.map { it.label }
        val prefixIndex = labels.indexOf("watch")
        val containsIndex = labels.indexOf("hot water")
        assertTrue(prefixIndex >= 0)
        assertTrue(containsIndex >= 0)
        assertTrue(prefixIndex < containsIndex)
    }

    @Test
    fun `search is case insensitive`() {
        val results = Search.query(tiles, "WATERMELON")
        assertEquals(listOf("Watermelon"), results.map { it.label })
    }

    @Test
    fun `results are alphabetical within each rank group`() {
        val results = Search.query(tiles, "wa")
        // prefix group: warm, watch, water, Watermelon — alphabetical,
        // case-folded, so Watermelon sorts with lowercase.
        val prefixGroup = results.take(4).map { it.label.lowercase() }
        assertEquals(prefixGroup.sorted(), prefixGroup)
    }

    @Test
    fun `no matches returns empty list`() {
        assertTrue(Search.query(tiles, "zebra").isEmpty())
    }

    @Test
    fun `contains match finds words mid-label`() {
        val results = Search.query(tiles, "hot")
        assertEquals(listOf("hot water"), results.map { it.label })
    }
}
