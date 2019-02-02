@file:Suppress("NOTHING_TO_INLINE", "UNUSED")
@file:ForLater

// TODO: 02-Feb-19 See if this is useful later

package uk.whitecrescent.waqti

import org.junit.jupiter.api.Assumptions

// Should can be used to throw a soft error

inline infix fun <T> T?.shouldEqual(other: T?) {
    Assumptions.assumeTrue(this == other)
}