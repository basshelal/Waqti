@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package uk.whitecrescent.waqti

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.function.Executable
import org.opentest4j.AssertionFailedError
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

// Cleaner Assertions that use JUnit5 Jupiter Assertions

inline infix fun <T> T?.mustEqual(other: T?) {
    Assertions.assertEquals(this, other)
}

inline infix fun <T> T?.mustNotEqual(other: T?) {
    Assertions.assertNotEquals(this, other)
}

inline infix fun <T : Comparable<T>> T.mustBeLessThan(other: T) {
    (this < other) mustBe true
}

inline infix fun <T : Comparable<T>> T.mustBeLessThanOrEqualTo(other: T) {
    (this <= other) mustBe true
}

inline infix fun <T : Comparable<T>> T.mustBeGreaterThan(other: T) {
    (this > other) mustBe true
}

inline infix fun <T : Comparable<T>> T.mustBeGreaterThanOrEqualTo(other: T) {
    (this >= other) mustBe true
}

inline infix fun <T : () -> Any?> T.mustThrow(exception: KClass<out Throwable>) {
    Assertions.assertThrows(exception.java, { this() })
}

/**
 * This function/block must not throw the given [Throwable], if it does an [AssertionFailedError]
 * is given and the test is failed.
 *
 * Any other Exceptions are still thrown, meaning the test can still fail as a result of these
 * Exceptions being thrown.
 *
 * Prefer this over [mustNotThrowOnly] as it is better practice to fail tests that throw any
 * exceptions.
 */
inline infix fun <T : () -> Any?> T.mustNotThrow(exception: KClass<out Throwable>) {
    try {
        this()
    } catch (t: Throwable) {
        if (t::class == exception || t::class.allSuperclasses.contains(exception)) {
            throw AssertionFailedError("Expected not to throw ${exception.simpleName} " +
                    "but actually did throw ${t::class.simpleName}", t)
        } else {
            throw t
        }
    }
}

/**
 * This function/block must not throw the given [Throwable], if it does an [AssertionFailedError]
 * is given and the test is failed.
 *
 * **Any other Exceptions are caught and their stack trace is printed**
 *
 * Prefer [mustNotThrow] over this as it is better practice to fail tests that throw any
 * exceptions.
 */
inline infix fun <T : () -> Any?> T.mustNotThrowOnly(exception: KClass<out Throwable>) {
    try {
        this()
    } catch (t: Throwable) {
        if (t::class == exception || t::class.allSuperclasses.contains(exception)) {
            throw AssertionFailedError("Expected not to throw ${exception.simpleName} " +
                    "but actually did throw ${t::class.simpleName}", t)
        } else {
            t.printStackTrace()
        }
    }
}

inline fun <T : () -> Any?> T.mustNotThrowAnyException() {
    Assertions.assertAll(Executable { this() })
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
}

inline fun <T> Collection<T>.mustBeEmpty() {
    this.isEmpty() mustBe true
}

inline fun <T> Collection<T>.mustNotBeEmpty() {
    this.isEmpty() mustBe false
}

inline infix fun <T> Collection<T>.mustHaveSizeOf(size: Int) {
    this.size mustEqual size
}

inline infix fun <T : Comparable<T>> T.mustBeIn(range: ClosedRange<T>) {
    (this in range) mustBe true
}

/**
 * Runs the `first` block first then the `do` block, `first` is assumed to not throw any
 * Exceptions and if it does then the test is aborted and failed.
 *
 * Typical use for this is to run some code in the `first` block that will result in something
 * that will be tested in the `do` block, where the assertions will be.
 *
 * For example:
 * ```
 * after({ myInt + 1 }) {
 *     myInt mustEqual 1
 * }
 * ```
 */
inline fun <T : () -> Any?, R : () -> Any?> after(first: T, `do`: R) {
    first.mustNotThrowAnyException()
    `do`()
}

/**
 * Just a reversed apply, so instead of `task.apply{...}` we do
 * `on(task){...}`.
 * This just makes things a little more readable in tests when you're running multiple tests on
 * the same thing.
 * @see apply
 */
inline fun <T> on(element: T, func: T.() -> Unit) {
    element.apply(func)
}

inline val <T> T.ignoreResult: Unit
    get() = this as Unit

inline val <T> T?.assertNull
    get() = Assertions.assertNull(this)


inline val Boolean.assertTrue
    get() = Assertions.assertTrue(this)


inline val Boolean.assertFalse
    get() = Assertions.assertFalse(this)


inline val <T : Executable> Iterable<T>.assertAll
    get() = Assertions.assertAll(this.toList())
