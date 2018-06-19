package uk.whitecrescent.waqti.model.collections

import io.reactivex.schedulers.Schedulers

// Threads for Concurrency
val LIST_OBSERVER_THREAD = Schedulers.newThread()
val HABIT_OBSERVER_THREAD = Schedulers.newThread()