package uk.whitecrescent.waqti.model

import uk.whitecrescent.waqti.task.ID

interface Cacheable {

    fun id(): ID
}