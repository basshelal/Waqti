package uk.whitecrescent.waqti.backend.persistence

import uk.whitecrescent.waqti.backend.task.ID

// Estimates for reasonable Cache sizes
const val TASKS_CACHE_SIZE = 100
const val TEMPLATES_CACHE_SIZE = 10
const val LABELS_CACHE_SIZE = 10
const val PRIORITIES_CACHE_SIZE = 10
const val TIME_UNITS_CACHE_SIZE = 10
const val TASK_LISTS_CACHE_SIZE = 25
const val BOARDS_CACHE_SIZE = 5
const val BOARD_LISTS_CACHE_SIZE = 1

class CacheElementNotFoundException(val elementID: ID = 0,
                                    val elementName: String = "",
                                    val cacheType: String = "") :
        NoSuchElementException("Element $elementName of ID $elementID " +
                "not found in Cache of type $cacheType")

/**
 * Shows that this queries the Database, useful if database queries are expensive.
 * Only used on Queries, not any writes
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION, AnnotationTarget.PROPERTY)
annotation class QueriesDataBase