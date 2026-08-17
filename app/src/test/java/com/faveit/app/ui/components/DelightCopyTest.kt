package com.faveit.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class DelightCopyTest {
    @Test fun savedFavoriteNamesThePersistedItem() {
        assertEquals(
            "In-N-Out is in your favorites",
            favoriteSavedMessage("In-N-Out"),
        )
    }

    @Test fun setupCompletionCopyHandlesEmptyAndPopulatedStates() {
        assertEquals("Your favorites are ready", setupCompleteMessage(1))
        assertEquals(
            "Faveit is ready when inspiration strikes",
            setupCompleteMessage(0),
        )
    }
}
