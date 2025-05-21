package core.utils

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ValidationUtilsTest {
    @Test
    fun validEmailPasses() {
        assertTrue("test@example.com".isValidEmail())
    }

    @Test
    fun invalidEmailFails() {
        assertFalse("test@example.com$".isValidEmail())
    }
}
