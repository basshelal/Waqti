package uk.whitecrescent.waqti.model.collections

import uk.whitecrescent.waqti.model.persistence.Cache
import uk.whitecrescent.waqti.model.persistence.Caches

// Just a container of all the Boards, not a collection because we don't want to persist it, it's
// just a wrapper for Caches.boards

// TODO: 29-Dec-18 I think the only way this can truly work is if it's an AbstractWaqtiList
// there can only exist one BoardList ever but this helps us to have the dragging and moving
// functionality
object BoardList {

    val cache: Cache<Board>
        get() = Caches.boards


}