package com.faveit.app.data

import com.faveit.app.model.FaveCategory
import com.faveit.app.model.FavoriteOverride
import com.faveit.app.model.GemPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FavoriteOverrideCodecTest {
    @Test fun roundTripsSpacesPunctuationAndUnicode() {
        val original = FavoriteOverride(
            itemId = "restaurant_in_n_out",
            displayName = "Our Friday 🍔 spot",
            category = FaveCategory.ACTIVITIES,
            palette = GemPalette.AMETHYST,
        )
        assertEquals(original, FavoriteOverrideCodec.decode(FavoriteOverrideCodec.encode(original)))
    }

    @Test fun malformedStoredDataIsIgnored() {
        assertNull(FavoriteOverrideCodec.decode("not-a-valid-record"))
    }
}
