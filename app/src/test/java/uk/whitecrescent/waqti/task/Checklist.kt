package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.Checklist
import uk.whitecrescent.waqti.model.task.DEFAULT_CHECKLIST
import uk.whitecrescent.waqti.model.task.DEFAULT_CHECKLIST_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.ListItem
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.TaskState
import uk.whitecrescent.waqti.model.task.TaskStateException
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.sleep
import uk.whitecrescent.waqti.testTask


@DisplayName("Checklist Tests")
class Checklist : BaseTaskTest() {

    @DisplayName("Checklist add and delete")
    @Test
    fun testChecklistAddAndDelete() {
        val checklist = Checklist(
                "Zero",
                "One",
                "Two",
                "Three"
        )
        assertEquals(4, checklist.size())
        assertEquals(
                listOf(ListItem("Zero", false),
                        ListItem("One", false),
                        ListItem("Two", false),
                        ListItem("Three", false)), checklist.asList())

        checklist.addItem("Four")
        assertEquals(5, checklist.size())

        checklist.deleteItem(4)
        assertEquals(4, checklist.size())

        checklist.clear()
        assertTrue(checklist.isEmpty())

        checklist.addAll(ListItem("Zero", true), ListItem("One", false))
        assertEquals(2, checklist.size())

    }

    @DisplayName("Checklist move")
    @Test
    fun testChecklistMove() {
        val checklist = Checklist(
                "Zero",
                "One",
                "Two",
                "Three"
        )
        assertThrows(IllegalArgumentException::class.java, { checklist.moveItem(0, 4) })
        assertThrows(IllegalArgumentException::class.java, { checklist.moveItem(-1, 2) })

        // Move from 0 to 3 (small to large)
        checklist.moveItem(0, 3)
        assertEquals(listOf(
                ListItem("One", false),
                ListItem("Two", false),
                ListItem("Three", false),
                ListItem("Zero", false)
        ),
                checklist.asList())

        // Don't move at all (same place)
        checklist.moveItem(0, 0)
        assertEquals(listOf(
                ListItem("One", false),
                ListItem("Two", false),
                ListItem("Three", false),
                ListItem("Zero", false)
        ),
                checklist.asList())

        // Move from 3 to 0 (large to small)
        checklist.moveItem(3, 0)
        assertEquals(listOf(
                ListItem("Zero", false),
                ListItem("One", false),
                ListItem("Two", false),
                ListItem("Three", false)
        ),
                checklist.asList())
    }

    @DisplayName("Checklist iterator and checking")
    @Test
    fun testCheckListIteratorAndChecking() {
        val checklist = Checklist(
                "Zero",
                "One",
                "Two",
                "Three"
        )
        for (listItem in checklist) {
            listItem.isChecked = true
        }
        assertEquals(0, checklist.uncheckedItemsSize())

        for (i in 0..checklist.size() - 1) {
            checklist.uncheckItem(i)
        }
        assertEquals(0, checklist.checkedItemsSize())
        assertTrue(ListItem("Two", false) in checklist)
    }

    @DisplayName("Checklist Default Values")
    @Test
    fun testTaskChecklistDefaultValues() {
        val task = testTask
        assertFalse(task.checklist.isConstrained)
        assertEquals(DEFAULT_CHECKLIST, task.checklist.value)
        assertFalse(task.checklist.isVisible)
    }

    @DisplayName("Set Checklist Property using setChecklistProperty")
    @Test
    fun testTaskSetChecklistProperty() {
        val task = testTask
                .setChecklistProperty(
                        Property(SHOWING, Checklist("Zero", "One", "Two"), NOT_CONSTRAINED, UNMET)
                )

        assertFalse(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)


        task.hideChecklist()
        assertEquals(Property(HIDDEN, DEFAULT_CHECKLIST, NOT_CONSTRAINED, UNMET), task.checklist)
    }

    @DisplayName("Set Checklist Property using setChecklistPropertyValue")
    @Test
    fun testTaskSetChecklistPropertyValue() {
        val task = testTask
                .setChecklistPropertyValue(
                        Checklist("Zero", "One", "Two")
                )

        assertFalse(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)

        task.hideChecklist()
        assertEquals(Property(HIDDEN, DEFAULT_CHECKLIST, NOT_CONSTRAINED, UNMET), task.checklist)
    }

    @DisplayName("Set Checklist Constraint using setChecklistProperty")
    @Test
    fun testTaskSetChecklistPropertyWithConstraint() {
        val task = testTask
                .setChecklistProperty(
                        Property(SHOWING, Checklist("Zero", "One", "Two"), CONSTRAINED, UNMET)
                )

        assertTrue(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)
        assertFalse((task.checklist).isMet)
    }

    @DisplayName("Set Checklist Constraint using setChecklistConstraintValue")
    @Test
    fun testTaskSetChecklistConstraintValue() {
        val task = testTask
                .setChecklistConstraintValue(Checklist("Zero", "One", "Two"))

        assertTrue(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)
        assertFalse((task.checklist).isMet)
    }

    @DisplayName("Set Checklist Property failable")
    @Test
    fun testTaskSetDurationPropertyFailable() {
        val task = testTask
                .setChecklistPropertyValue(Checklist("Zero", "One", "Two"))

        assertFalse(task.isFailable)
        assertFalse(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)
    }

    @DisplayName("Set Checklist Constraint failable and killing when all get checked")
    @Test
    fun testTaskSetChecklistConstraintFailableChecked() {
        val task = testTask
                .setChecklistProperty(Property(SHOWING, Checklist("Zero", "One", "Two"), CONSTRAINED, UNMET))

        assertTrue(task.isFailable)
        assertTrue(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)
        assertFalse((task.checklist).isMet)
        assertThrows(TaskStateException::class.java, { task.kill() })

        for (listItem in task.checklist.value) {
            task.checklist.value.checkItem(listItem)
        }

        sleep(2)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
        assertTrue((task.checklist).isMet)
    }

    @DisplayName("Set Checklist Constraint failable and killing when empty later")
    @Test
    fun testTaskSetChecklistConstraintFailableEmptyLater() {
        val task = testTask
                .setChecklistProperty(Property(SHOWING, Checklist("Zero", "One", "Two"), CONSTRAINED, UNMET))

        assertTrue(task.isFailable)
        assertTrue(task.checklist.isConstrained)
        assertEquals(Checklist("Zero", "One", "Two"), task.checklist.value)
        assertTrue(task.checklist.isVisible)
        assertFalse((task.checklist).isMet)
        assertThrows(TaskStateException::class.java, { task.kill() })

        task.checklist.value.clear()

        sleep(2)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
        assertTrue((task.checklist).isMet)
    }

    @DisplayName("Set Checklist Constraint failable and killing when empty early")
    @Test
    fun testTaskSetChecklistConstraintFailableEmptyEarly() {
        val task = testTask
                .setChecklistProperty(Property(SHOWING, Checklist(), CONSTRAINED, UNMET))

        assertTrue(task.isFailable)
        assertTrue(task.checklist.isConstrained)
        assertEquals(Checklist(), task.checklist.value)
        assertTrue(task.checklist.isVisible)
        assertFalse((task.checklist).isMet)

        sleep(2)

        task.kill()

        assertEquals(TaskState.KILLED, task.state)
        assertTrue((task.checklist).isMet)
    }

    @DisplayName("Checklist Un-constraining")
    @Test
    fun testTaskChecklistUnConstraining() {
        val task = testTask
                .setChecklistConstraintValue(Checklist("Zero"))

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)
        task.setChecklistProperty((task.checklist).unConstrain())

        sleep(1)

        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Checklist Constraint Re-Set")
    @Test
    fun testTaskChecklistConstraintReSet() {
        val task = testTask
                .setChecklistConstraintValue(Checklist("First"))

        sleep(1)
        assertThrows(TaskStateException::class.java, { task.kill() })
        assertTrue(task.getAllUnmetAndShowingConstraints().size == 1)

        task.setChecklistConstraintValue(Checklist("Second"))
        assertEquals(Checklist("Second"), task.checklist.value)

        task.checklist.value.checkItem(0)

        sleep(2)

        task.kill()
        assertEquals(TaskState.KILLED, task.state)
    }

    @DisplayName("Checklist Hiding")
    @Test
    fun testChecklistHiding() {
        val checklist = Checklist("ZERO", "ONE")

        val task = testTask
                .setChecklistPropertyValue(checklist)
        assertEquals(checklist, task.checklist.value)

        task.hideChecklist()
        assertEquals(DEFAULT_CHECKLIST_PROPERTY, task.checklist)

        task.setChecklistConstraintValue(checklist)
        assertEquals(checklist, task.checklist.value)
        assertThrows(IllegalStateException::class.java, { task.hideChecklist() })
    }

}