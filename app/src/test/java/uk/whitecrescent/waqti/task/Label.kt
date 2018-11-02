package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.Constraint
import uk.whitecrescent.waqti.model.task.DEFAULT_LABELS_LIST
import uk.whitecrescent.waqti.model.task.DEFAULT_LABELS_PROPERTY
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Label Tests")
class Label : BaseTaskTest() {

//    @DisplayName("Label")
//    @Test
//    fun testLabel() {
//        val myLabel = Label("My Label")
//
//        assertEquals("My Label", myLabel.name)
//        myLabel.addChild(Label("My Child Label"))
//        assertTrue(myLabel.children.isNotEmpty())
//        assertTrue(myLabel.children.size == 1)
//        assertEquals("My Child Label", myLabel.children[0].name)
//    }

    @DisplayName("Label Default Values")
    @Test
    fun testTaskLabelDefaultValues() {
        val task = testTask
        assertFalse(task.labels is Constraint)
        assertEquals(DEFAULT_LABELS_LIST, task.labels.value)
        assertFalse(task.labels.isVisible)
        assertEquals(DEFAULT_LABELS_PROPERTY, Property(HIDDEN, DEFAULT_LABELS_LIST))
    }

    @DisplayName("Set Label Property using setLabelsProperty")
    @Test
    fun testTaskSetLabelProperty() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2)))

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(
                arrayListOf(label1, label2)
        ))
        assertEquals(arrayListOf(label1, label2), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Set Label Property using setLabelsValue")
    @Test
    fun testTaskSetLabelValue() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsValue(label1, label2)

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1, label2)))
        assertEquals(arrayListOf(label1, label2), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Set Label Constraint")
    @Test
    fun testTaskSetLabelConstraint() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsProperty(Constraint(SHOWING, arrayListOf(label1, label2), UNMET))

        assertFalse(task.labels is Constraint)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertEquals(arrayListOf(label1, label2), task.labels.value)
        assertTrue(task.labels.isVisible)
        Assertions.assertThrows(ClassCastException::class.java)
        { assertTrue(task.labels.asConstraint.isMet) }

    }

    @DisplayName("Add Label")
    @Test
    fun testTaskAddLabel() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")
        val label3 = Label("Label3")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2)))
                .addLabels(label3)

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1, label2, label3)))

        assertEquals(arrayListOf(label1, label2, label3), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Add Label when not showing")
    @Test
    fun testTaskAddLabelNotShowing() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")
        val label3 = Label("Label3")

        val task = testTask
                .setLabelsProperty(Property(HIDDEN, arrayListOf(label1, label2)))

        assertFalse(task.labels.isVisible)
        task.addLabels(label3)
        assertTrue(task.labels.isVisible)

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1, label2, label3)))

        assertEquals(arrayListOf(label1, label2, label3), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

    @DisplayName("Remove Label")
    @Test
    fun testTaskRemoveLabel() {
        val label1 = Label("Label1")
        val label2 = Label("Label2")

        val task = testTask
                .setLabelsProperty(Property(SHOWING, arrayListOf(label1, label2)))
                .removeLabel(label2)

        assertFalse(task.labels is Constraint)
        assertTrue(task.labels.value.containsAll(arrayListOf(label1)))

        assertEquals(arrayListOf(label1), task.labels.value)
        assertTrue(task.labels.isVisible)


        task.hideLabel()
        assertEquals(Property(HIDDEN, DEFAULT_LABELS_LIST), task.labels)
    }

}