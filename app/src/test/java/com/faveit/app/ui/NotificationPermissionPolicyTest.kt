package com.faveit.app.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPermissionPolicyTest {
    @Test fun firstRequestUsesRuntimePrompt() {
        assertTrue(canRequestNotificationPermission(false, false, false))
    }

    @Test fun dismissedPromptRemainsRequestable() {
        assertTrue(canRequestNotificationPermission(false, false, false))
    }

    @Test fun ordinaryDenialWithRationaleRemainsRequestable() {
        assertTrue(canRequestNotificationPermission(false, true, true))
    }

    @Test fun permanentDenialUsesSystemSettings() {
        assertFalse(canRequestNotificationPermission(false, true, false))
    }

    @Test fun grantedPermissionDoesNotRequestAgain() {
        assertFalse(canRequestNotificationPermission(true, false, false))
    }
}
