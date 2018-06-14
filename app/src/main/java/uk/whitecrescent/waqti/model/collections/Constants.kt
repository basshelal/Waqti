package uk.whitecrescent.waqti.model.collections

import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap

// Contains Routines or saved Tuples, Long is for IDs
// TODO: 15-Apr-18 Maybe Tuples should have some sort of ID
val ROUTINE_DATABASE = ConcurrentHashMap<Long, Tuple>()
val TUPLE_DATABASE = ConcurrentHashMap<Long, Tuple>()

// Threads for Concurrency
val LIST_OBSERVER_THREAD = Schedulers.newThread()
val HABIT_OBSERVER_THREAD = Schedulers.newThread()