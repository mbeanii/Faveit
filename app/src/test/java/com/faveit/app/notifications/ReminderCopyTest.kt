package com.faveit.app.notifications

import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderCopyTest {
    @Test fun mvpReminderUsesExactProductFormat() {
        assertEquals("Remember In-N-Out?", reminderTitle("In-N-Out"))
    }

    @Test fun locallyCustomizedNameIsPreserved() {
        assertEquals("Remember Friday 🍔 spot?", reminderTitle("Friday 🍔 spot"))
    }
}
