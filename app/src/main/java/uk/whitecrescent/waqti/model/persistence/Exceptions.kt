package uk.whitecrescent.waqti.model.persistence

import uk.whitecrescent.waqti.model.task.ID

/*
 * Used to show that the element with a given ID is not found, we use IDs for convenience but you
 * can still do something like this...
 *
 *      val x = Task("My Task")
 *
 *      try {
 *          Caches.tasks[x]
 *      } catch(exception: CacheElementNotFoundException) {
 *          if(exception.elementID == x.id()) //something
 *      }
 *
 * Which creates safety with specificity, just be sure to catch the exception
 * CENFE (CacheElementNotFoundException)
 *
 */
class CacheElementNotFoundException(val elementID: ID = 0, val element: Any = "") :
        NoSuchElementException(
                "Element $element of ID $elementID not found in this Cache"
        )
