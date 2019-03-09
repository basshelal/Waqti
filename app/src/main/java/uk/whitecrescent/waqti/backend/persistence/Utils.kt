package uk.whitecrescent.waqti.backend.persistence

import uk.whitecrescent.waqti.backend.task.ID

// Estimates for reasonable Cache sizes
const val TASKS_CACHE_SIZE = 1000
const val TEMPLATES_CACHE_SIZE = 50
const val LABELS_CACHE_SIZE = 50
const val PRIORITIES_CACHE_SIZE = 50
const val TIME_UNITS_CACHE_SIZE = 50
const val TASK_LISTS_CACHE_SIZE = 250
const val BOARDS_CACHE_SIZE = 25
const val BOARD_LISTS_CACHE_SIZE = 1

/*
 * Used to show that the element with a given ID is not found, we use IDs for convenience but you
 * can still do something like this...
 *
 *      val x = Task("My Task")
 *
 *      try {
 *          Caches.tasks[x]
 *      } catch(exception: ElementNotFoundException) {
 *          if(exception.elementID == x.id) //something
 *      }
 *
 * Which creates safety with specificity, just be sure to catch the exception
 * CENFE (ElementNotFoundException)
 *
 */
class ElementNotFoundException(elementID: ID = 0, element: Any = "", cache: Any = "") :
        NoSuchElementException("Element $element of ID $elementID not found in this Cache $cache")

/**
 * Shows that this queries the Database, useful if database queries are expensive.
 * Only used on Queries, not any writes
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION, AnnotationTarget.PROPERTY)
annotation class QueriesDataBase