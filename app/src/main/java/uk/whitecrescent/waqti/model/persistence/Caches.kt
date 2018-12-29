package uk.whitecrescent.waqti.model.persistence

import uk.whitecrescent.waqti.model.Committable
import uk.whitecrescent.waqti.model.NeedsOptimization
import uk.whitecrescent.waqti.model.collections.Board
import uk.whitecrescent.waqti.model.collections.TaskList
import uk.whitecrescent.waqti.model.task.Label
import uk.whitecrescent.waqti.model.task.Priority
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.Template
import uk.whitecrescent.waqti.model.task.TimeUnit


/*
 * The intent of this class is to contain all the objects we need in memory, literally a cache, what it has to do is
 * the following.
 * Be able to read the main persistence (Database, will differ by platform) then load the necessary (and only the
 * necessary) objects from persistence to ConcurrentHashMaps inside this class which we can then use to quickly
 * access the objects we need, not needing to request to read the database all the time, the issue will come with
 * automatic updating which actually we can do here and is one of the main purposes of this class, we can have a few
 * observers check to see that the objects correspond to their equivalent in the persistent database, if not then
 * we update the persistent database with what's in the cache because it implies that there was some update called
 * or done, this saves us from having to mess with the persistent database directly very often or even better, stops
 * us from having an inconsistency between memory and database making us not have to call 2 updates (1 for memory
 * and 1 for persistence).
 *
 * The point is that this class will abstract away all of that, making it seem like all we have to do is just update
 * the memory.
 *
 * There probably will be potential issues with this, concurrency being one, consistency being the other, who's the
 * source of truth etc etc, but the benefits are quite great so this is worth it but it needs to be tested and done
 * with care
 *
 * This also stops us from having to update a million things when the database implementation changes, we would just
 * need to change this class
 *
 * In summary the Caches is then just a middle man between the code (CRUDs) and the persistence database mainly for
 * the reason to have database independence/ modularity and minimize database operations directly, instead delegating
 * them to be done here
 *
 * Long note but well worth it, it's a good idea.
 *
 * Bassam Helal Mon-14-May-18
 *
 * We can put the persistence database operations into a queue maybe (not entirely sure why though)
 * Bassam Helal Mon-21-May
 *
 * */
object Caches {

    val tasks: Cache<Task> = Cache(Database.tasks)
    val templates: Cache<Template> = Cache(Database.templates)
    val labels: Cache<Label> = Cache(Database.labels)
    val priorities: Cache<Priority> = Cache(Database.priorities)
    val timeUnits: Cache<TimeUnit> = Cache(Database.timeUnits)

    val taskLists: Cache<TaskList> = Cache(Database.taskLists)
    val boards: Cache<Board> = Cache(Database.boards)

    val allCaches = listOf(
            tasks, templates, labels, priorities, timeUnits, taskLists, boards
    )

    @NeedsOptimization
    // TODO: 29-Dec-18 A little too slow initializing Caches and Building Database
    // possible idea is to do it asynchronously but we have to be careful with that as many
    // caches rely on one another
    fun initialize() {
        allCaches.forEach { it.initialize() }
    }

    fun close() {
        allCaches.forEach { it.close() }
    }

    fun clearAllCaches(): Committable {
        return object : Committable {
            override fun commit() {
                allCaches.forEach { it.clearAll().commit() }
            }
        }
    }

}