package uk.whitecrescent.waqti.model.task

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.model.Time

@Entity
data class TestEntity(
        @Convert(
                converter = TimePropertyConverter::class,
                dbType = String::class)
        val time: TimeProperty = TimeProperty(false, Time.MIN),
        @Convert(
                converter = LabelArrayListPropertyConverter::class,
                dbType = String::class)
        val list: LabelArrayListProperty = LabelArrayListProperty(false, DEFAULT_LABELS_LIST),
        @Id var id: Long = 0)
