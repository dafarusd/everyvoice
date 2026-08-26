package org.everyvoice.aac

import org.everyvoice.aac.engine.SentenceStrip
import org.everyvoice.aac.engine.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SentenceStripTest {

    @Test
    fun `empty strip produces empty text`() {
        val strip = SentenceStrip()
        assertEquals("", strip.text())
        assertTrue(strip.isEmpty)
    }

    @Test
    fun `single word is capitalized and punctuated`() {
        val strip = SentenceStrip()
        strip.add(Tile("help"))
        assertEquals("Help.", strip.text())
    }

    @Test
    fun `words join with single spaces`() {
        val strip = SentenceStrip()
        strip.add(Tile("I"))
        strip.add(Tile("want"))
        strip.add(Tile("water"))
        assertEquals("I want water.", strip.text())
    }

    @Test
    fun `phrase tile ending in punctuation starts a new sentence`() {
        val strip = SentenceStrip()
        strip.add(Tile("Thanks for helping", "Thank you for helping me."))
        strip.add(Tile("I"))
        strip.add(Tile("want"))
        strip.add(Tile("water"))
        assertEquals("Thank you for helping me. I want water.", strip.text())
    }

    @Test
    fun `existing terminal punctuation is not doubled`() {
        val strip = SentenceStrip()
        strip.add(Tile("Are we there yet", "Are we there yet?"))
        assertEquals("Are we there yet?", strip.text())
    }

    @Test
    fun `backspace removes the most recent tile only`() {
        val strip = SentenceStrip()
        strip.add(Tile("I"))
        strip.add(Tile("want"))
        strip.add(Tile("water"))
        strip.removeLast()
        assertEquals("I want.", strip.text())
        assertEquals(2, strip.size)
    }

    @Test
    fun `backspace on empty strip is a no-op`() {
        val strip = SentenceStrip()
        strip.removeLast()
        assertTrue(strip.isEmpty)
    }

    @Test
    fun `clear empties the strip`() {
        val strip = SentenceStrip()
        strip.add(Tile("I"))
        strip.add(Tile("want"))
        strip.clear()
        assertTrue(strip.isEmpty)
        assertEquals("", strip.text())
    }

    @Test
    fun `extra whitespace inside tiles is collapsed`() {
        val strip = SentenceStrip()
        strip.add(Tile("  I  "))
        strip.add(Tile("want", "  want   "))
        assertEquals("I want.", strip.text())
    }

    @Test
    fun `contents returns an independent copy`() {
        val strip = SentenceStrip()
        strip.add(Tile("I"))
        val snapshot = strip.contents
        strip.add(Tile("want"))
        assertEquals(1, snapshot.size)
        assertEquals(2, strip.size)
    }

    @Test
    fun `strip reports non-empty after add`() {
        val strip = SentenceStrip()
        strip.add(Tile("yes"))
        assertFalse(strip.isEmpty)
    }

    @Test
    fun `a tile with no spoken text does not crash the strip`() {
        // A restored backup is data from outside the app and can carry a
        // tile whose spoken text is empty. text() used to throw
        // NoSuchElementException on it, taking the app down mid-sentence.
        val strip = SentenceStrip()
        strip.add(Tile("blank", ""))
        assertEquals("", strip.text())
    }

    @Test
    fun `blank tiles do not swallow the words around them`() {
        val strip = SentenceStrip()
        strip.add(Tile("I", "I"))
        strip.add(Tile("blank", ""))
        strip.add(Tile("water", "water"))
        assertEquals("I water.", strip.text())
    }
}
