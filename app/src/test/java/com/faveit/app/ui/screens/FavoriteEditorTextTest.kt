package com.faveit.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FavoriteEditorTextTest {
    @Test fun displayNameLimitDoesNotSplitSupplementaryCharacters() {
        val emoji = "💎"
        val input = "a".repeat(59) + emoji + "z"

        val limited = input.takeCodePoints(60)

        assertEquals("a".repeat(59) + emoji, limited)
        assertEquals(60, limited.codePointCount(0, limited.length))
        assertFalse(Character.isHighSurrogate(limited.last()))
    }
}
