@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package uk.whitecrescent.waqti

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.function.Executable
import org.opentest4j.AssertionFailedError

// Cleaner Assertions that use JUnit5 Jupiter Assertions

inline infix fun <T> T?.mustEqual(other: T?) {
    Assertions.assertEquals(this, other)
}

inline infix fun <T> T?.mustNotEqual(other: T?) {
    Assertions.assertNotEquals(this, other)
}

inline infix fun <T : Executable> T.mustThrow(exception: Class<Exception>) {
    Assertions.assertThrows(exception, this)
}

inline infix fun <T : Executable> T.mustNotThrow(exception: Class<Exception>) {
    try {
        this.execute()
    } catch (t: Throwable) {
        if (t::class.java == exception) {
            throw AssertionFailedError("Expected not to throw exception ${exception.name} " +
                    "but actually did", t)
        }
    }
}

inline infix fun Boolean.mustBe(boolean: Boolean) {
    when (boolean) {
        true -> {
            this.assertTrue
        }
        else -> {
            this.assertFalse
        }
    }
    Assertions.assertDoesNotThrow { }
}


inline val <T> T?.assertNull
    get() = Assertions.assertNull(this)


inline val Boolean.assertTrue
    get() = Assertions.assertTrue(this)


inline val Boolean.assertFalse
    get() = Assertions.assertFalse(this)


inline val <T : Executable> Iterable<T>.assertAll
    get() = Assertions.assertAll(this.toList())
