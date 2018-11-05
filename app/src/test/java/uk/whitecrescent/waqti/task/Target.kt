package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_TARGET
import uk.whitecrescent.waqti.model.task.DEFAULT_TARGET_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Target Tests")
class Target : BaseTaskTest() {

    @DisplayName("Target Default Values")
    @Test
    fun testTaskTargetDefaultValues() {
        val task = testTask()
        assertFalse(task.target.isConstrained)
        assertEquals(DEFAULT_TARGET, task.target.value)
        assertFalse(task.target.isVisible)
    }

    @DisplayName("Set Target Property using setTargetProperty")
    @Test
    fun testTaskSetTargetProperty() {
        val task = testTask()
                .setTargetProperty(
                        Property(SHOWING, "Test Target", NOT_CONSTRAINED, UNMET)
                )

        assertFalse(task.target.isConstrained)
        assertEquals("Test Target", task.target.value)
        assertTrue(task.target.isVisible)


        task.hideTarget()
        assertEquals(Property(HIDDEN, DEFAULT_TARGET, NOT_CONSTRAINED, UNMET), task.target)
    }

    @DisplayName("Set Target Property using setTargetPropertyValue")
    @Test
    fun testTaskSetTargetPropertyValue() {
        val task = testTask()
                .setTargetPropertyValue(
                        "Test Target"
                )

        assertFalse(task.target.isConstrained)
        assertEquals("Test Target", task.target.value)
        assertTrue(task.target.isVisible)

        task.hideTarget()
        assertEquals(Property(HIDDEN, DEFAULT_TARGET, NOT_CONSTRAINED, UNMET), task.target)
    }

    @DisplayName("Set Target Constraint using setTargetProperty")
    @Test
    fun testTaskSetTargetPropertyWithConstraint() {
        val task = testTask()
                .setTargetProperty(
                        Property(SHOWING, "Test Target", CONSTRAINED, UNMET)
                )

        assertTrue(task.target.isConstrained)
        assertEquals("Test Target", task.target.value)
        assertTrue(task.target.isVisible)
        assertFalse((task.target).isMet)
    }

    @DisplayName("Set Target Constraint using setTargetConstraintValue")
    @Test
    fun testTaskSetTargetConstraintValue() {
        val task = testTask
                .setTargetConstraintValue("Test Target")

        assertTrue(task.target.isConstrained)
        assertEquals("Test Target", task.target.value)
        assertTrue(task.target.isVisible)
        assertFalse((task.target).isMet)
    }

    @DisplayName("Set Target Property failable")
    @Test
    fun testTaskSetTargetPropertyFailable() {
        val task = testTask()
                .setTargetPropertyValue("Test Target")

        assertFalse(task.isFailable)
        assertFalse(task.target.isConstrained)
        assertEquals("Test Target", task.target.value)
        assertTrue(task.target.isVisible)
    }

    @DisplayName("Set Target Constraint failable")
    @Test
    fun testTaskSetTargetConstraintFailable() {
        val task = testTask()
                .setTargetProperty(Property(SHOWING, "Test Target", CONSTRAINED, UNMET))


        assertTrue(task.isFailable)
        assertTrue(task.target.isConstrained)
        assertEquals("Test Target", task.target.value)
        assertTrue(task.target.isVisible)
        assertFalse((task.target).isMet)
    }

    @DisplayName("Target Hiding")
    @Test
    fun testTargetHiding() {
        val target = "Target"

        val task = testTask()
                .setTargetPropertyValue(target)
        assertEquals(target, task.target.value)

        task.hideTarget()
        assertEquals(DEFAULT_TARGET_PROPERTY, task.target)

        task.setTargetConstraintValue(target)
        assertEquals(target, task.target.value)
        Assertions.assertThrows(IllegalStateException::class.java, { task.hideTarget() })
    }
}