package uk.whitecrescent.waqti.tests.task

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.task.Constraint
import uk.whitecrescent.waqti.task.DEFAULT_LABELS_LIST
import uk.whitecrescent.waqti.task.HIDDEN
import uk.whitecrescent.waqti.task.Label
import uk.whitecrescent.waqti.task.Property
import uk.whitecrescent.waqti.task.SHOWING
import uk.whitecrescent.waqti.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Label Tests")
class Label {

    // Before All
    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Label.getOrCreateLabel("TestLabel_0")
            Label.getOrCreateLabel("TestLabel_1")
            Label.getOrCreateLabel("TestLabel_2")
        }
    }

    @DisplayName("Label")
    @Test
    fun testLabel() {
        // delete what beforeAll does
        Label.deleteLabel("TestLabel_0")
        Label.deleteLabel("TestLabel_1")
        Label.deleteLabel("TestLabel_2")

        Label.getOrCreateLabel("My Label")
        assertEquals("My Label", Label.getLabel("My Label").name)
        Label.getOrCreateLabel("My Label").children.add(Label.getOrCreateLabel("My Child Label"))
        assertTrue(Label.getLabel("My Label").children.isNotEmpty())
        assertEquals("My Child Label", Label.getLabel("My Label").children[0].name)
        Label.deleteLabel("My Label")
        assertTrue(Label.allLabels.isEmpty())

        // redo beforeAll
        Label.getOrCreateLabel("TestLabel_0")
        Label.getOrCreateLabel("TestLabel_1")
        Label.getOrCreateLabel("TestLabel_2")
    }

    @DisplayName("Label Default Values")
    @Test
    fun testTaskLabelDefaultValues() {
        val task = testTask()
        assertFalse(task.labels is Constraint)
        assertEquals(DEFAULT_LABELS_LIST, task.labels.value)
        assertFalse(task.labels.isVisible)
    }

    @DisplayName("Set Label Property using setLabelsProperty")
    @Test
    fun testTaskSetLabelProperty() {
        val task = testTask()
                .setLabelsProperty(Property(SHOWING,
                        arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
                ))

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
        ))
        assertEquals(arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1")), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Set Label Property using setLabelsValue")
    @Test
    fun testTaskSetLabelValue() {
        val task = testTask()
                .setLabelsValue(
                        Label.getLabel("TestLabel_0"),
                        Label.getLabel("TestLabel_1")
                )

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
        ))
        assertEquals(arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1")), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }


    @DisplayName("Set Label Constraint")
    @Test
    fun testTaskSetLabelConstraint() {
        val task = testTask()
                .setLabelsProperty(Constraint(SHOWING,
                        arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
                        , UNMET))

        assertFalse(task.labels is Constraint)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertEquals(arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1")), task.labels.value)
        assertTrue(task.labels.isVisible)
        Assertions.assertThrows(ClassCastException::class.java,
                { assertTrue((task.labels as Constraint).isMet == true) })

    }

    @DisplayName("Add Label")
    @Test
    fun testTaskAddLabel() {
        val task = testTask()
                .setLabelsProperty(Property(SHOWING,
                        arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
                )).addLabels(Label.getLabel("TestLabel_2"))

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(Label.getLabel("TestLabel_0"),
                        Label.getLabel("TestLabel_1"),
                        Label.getLabel("TestLabel_2"))
        ))
        assertEquals(arrayListOf(
                Label.getLabel("TestLabel_0"),
                Label.getLabel("TestLabel_1"),
                Label.getLabel("TestLabel_2")), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Add Label when not showing")
    @Test
    fun testTaskAddLabelNotShowing() {
        val task = testTask()
                .setLabelsProperty(Property(HIDDEN,
                        arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
                ))

        assertFalse(task.labels.isVisible)
        task.addLabels(Label.getLabel("TestLabel_2"))
        assertTrue(task.labels.isVisible)

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(Label.getLabel("TestLabel_0"),
                        Label.getLabel("TestLabel_1"),
                        Label.getLabel("TestLabel_2"))
        ))
        assertEquals(arrayListOf(
                Label.getLabel("TestLabel_0"),
                Label.getLabel("TestLabel_1"),
                Label.getLabel("TestLabel_2")), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Remove Label")
    @Test
    fun testTaskRemoveLabel() {
        val task = testTask()
                .setLabelsProperty(Property(SHOWING,
                        arrayListOf(Label.getLabel("TestLabel_0"), Label.getLabel("TestLabel_1"))
                )).removeLabel(Label.getLabel("TestLabel_1"))

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(Label.getLabel("TestLabel_0"))
        ))
        assertEquals(arrayListOf(
                Label.getLabel("TestLabel_0")), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

}