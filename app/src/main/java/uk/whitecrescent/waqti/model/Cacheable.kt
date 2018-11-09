package uk.whitecrescent.waqti.model

import uk.whitecrescent.waqti.model.task.ID

// Cacheable is persistable in ObjectBox
interface Cacheable {

    val id: ID

    fun notDefault(): Boolean

    fun update()

}