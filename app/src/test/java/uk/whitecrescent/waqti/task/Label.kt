package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.CONSTRAINED
import uk.whitecrescent.waqti.model.task.DEFAULT_LABELS_LIST
import uk.whitecrescent.waqti.model.task.DEFAULT_LABELS_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.NOT_CONSTRAINED
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Label Tests")
class Label : BaseTaskTest() {

    @DisplayName("Label Default Values")
    @Test
    fun testTaskLabelDefaultValues() {
        val task = testTask
        assertFalse(task.labels.isConstrained)
        assertEquals(DEFAULT_LABELS_LIST, task.labels.value)
        assertFalse(task.labels.isVisible)
        assertEquals(DEFAULT_LABELS_PROPERTY,
                Property(HIDDEN, DEFAULT_LABELS_LIST, NOT_CONSTRAINED, UNMET))
    }

    @DisplayName("Set Label Property using setLabelsProperty")
    @Test
    fun testTaskSetLabelProperty() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2),
                        NOT_CONSTRAINED, UNMET))

        assertFalse(task.labels.isConstrained)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(label1, label2)
        ))
        assertEquals(arrayListOf(label1, label2), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST, NOT_CONSTRAINED, UNMET), task.labels)
    }

    @DisplayName("Set Label Property using setLabelsValue")
    @Test
    fun testTaskSetLabelValue() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsValue(label1, label2)

        assertFalse(task.labels.isConstrained)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1, label2)))
        assertEquals(arrayListOf(label1, label2), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST, NOT_CONSTRAINED, UNMET), task.labels)
    }

    @DisplayName("Set Label Constraint")
    @Test
    fun testTaskSetLabelConstraint() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2), CONSTRAINED, UNMET))

        assertFalse(task.labels.isConstrained)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertEquals(arrayListOf(label1, label2), task.labels.value)
        assertTrue(task.labels.isVisible)

    }

    @DisplayName("Add Label")
    @Test
    fun testTaskAddLabel() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")
        val label3 = Label("Label3")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2),
                        NOT_CONSTRAINED, UNMET))
                .addLabels(label3)

        assertFalse(task.labels.isConstrained)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1, label2, label3)))

        assertEquals(arrayListOf(label1, label2, label3), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST, NOT_CONSTRAINED, UNMET), task.labels)
    }

    @DisplayName("Add Label when not showing")
    @Test
    fun testTaskAddLabelNotShowing() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")
        val label3 = Label("Label3")

        val task = testTask
                .setLabelsProperty(Property(HIDDEN, arrayListOf(label1, label2), NOT_CONSTRAINED,
                        UNMET))

        assertFalse(task.labels.isVisible)
        task.addLabels(label3)
        assertTrue(task.labels.isVisible)

        assertFalse(task.labels.isConstrained)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1, label2, label3)))

        assertEquals(arrayListOf(label1, label2, label3), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST, NOT_CONSTRAINED, UNMET), task.labels)
    }

    @DisplayName("Remove Label")
    @Test
    fun testTaskRemoveLabel() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2),
                        NOT_CONSTRAINED, UNMET))
                .removeLabel(label2)

        assertFalse(task.labels.isConstrained)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1)))

        assertEquals(arrayListOf(label1), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST, NOT_CONSTRAINED, UNMET), task.labels)
    }

}