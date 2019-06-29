@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.backend.persistence

import uk.whitecrescent.waqti.backend.Committable
import uk.whitecrescent.waqti.backend.collections.Board
import uk.whitecrescent.waqti.backend.collections.BoardList
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.backend.task.Label
import uk.whitecrescent.waqti.backend.task.Priority
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.backend.task.Template
import uk.whitecrescent.waqti.backend.task.TimeUnit
import uk.whitecrescent.waqti.doInBackgroundAsync
import uk.whitecrescent.waqti.size


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

    val tasks: Cache<Task> = Cache(Database.tasks, TASKS_CACHE_SIZE)
    val templates: Cache<Template> = Cache(Database.templates, TEMPLATES_CACHE_SIZE)
    val labels: Cache<Label> = Cache(Database.labels, LABELS_CACHE_SIZE)
    val priorities: Cache<Priority> = Cache(Database.priorities, PRIORITIES_CACHE_SIZE)
    val timeUnits: Cache<TimeUnit> = Cache(Database.timeUnits, TIME_UNITS_CACHE_SIZE)

    val taskLists: Cache<TaskList> = Cache(Database.taskLists, TASK_LISTS_CACHE_SIZE)
    val boards: Cache<Board> = Cache(Database.boards, BOARDS_CACHE_SIZE)
    val boardLists: Cache<BoardList> = Cache(Database.boardLists, BOARD_LISTS_CACHE_SIZE)

    inline val boardList: BoardList
        get() {
            require(Database.boardLists.size == 1) {
                "BoardLists Cache cannot contain ${Database.boardLists.size}, must be exactly one"
            }
            return Database.boardLists.all.first()
        }

    val allCaches = listOf(
            tasks, templates, labels, priorities, timeUnits, taskLists, boards, boardLists
    )

    fun initialize() {
        boardLists.initialize()
        doInBackgroundAsync { allCaches.forEach { it.initialize() } }
    }

    fun close() {
        allCaches.forEach { it.close() }
    }

    fun clearAllCaches() = Committable {
        allCaches.forEach { it.clearAll().commit() }
    }

    inline fun deleteTask(taskID: ID, listID: ID) {
        Caches.taskLists[listID].remove(taskID).update()
        Caches.tasks.remove(taskID)
    }

    inline fun deleteTaskList(taskListID: ID, boardID: ID) {
        Caches.taskLists[taskListID].clear().update()
        Caches.boards[boardID].remove(taskListID).update()
    }

    inline fun deleteBoard(boardID: ID) {
        Caches.boards[boardID].clear().update()
        Caches.boardList.remove(boardID).update()
    }

    inline fun seed(boards: Int = 5, lists: Int = 5, tasks: Int = 10) {
        Caches.clearAllCaches().commit()

        Caches.boardLists.put(BoardList("Default"))

        Caches.boardList.addAll(Array(boards) {
            Board("Board").apply {
                name = "Board $id"
            }
        }.asList()).update()

        Caches.boards.forEach {
            it.addAll(Array(lists) {
                TaskList("TaskList").apply {
                    name = "TaskList $id"
                }
            }.asList()).update()
        }

        Caches.taskLists.forEach {
            it.addAll(Array(tasks) {
                Task("Task").apply {
                    changeName("Task $id")
                }
            }.asList()).update()
        }
    }

    inline fun seedRealistic() {
        Caches.clearAllCaches().commit()

        Caches.boardLists.put(BoardList("Default"))

        Caches.boardList.addAll(listOf(
                Board("Personal",
                        listOf(
                                TaskList("To Do",
                                        listOf("Buy groceries",
                                                "Dentist appointment on Friday",
                                                "House viewing on Monday",
                                                "Buy new clothes",
                                                "Buy new microphone",
                                                "Weekly music session",
                                                "Weekly language session",
                                                "Liverpool match on Saturday with friends",
                                                "Clean up room",
                                                "Haircut before project meeting",
                                                "Finish writing CV",
                                                "Thank Professor Van Dyke",
                                                "Book Star Wars tickets")
                                                .map { Task(it) }
                                ),
                                TaskList("Today",
                                        listOf("Lunch with Salman",
                                                "Meeting with Dr. Roberts at 14:00",
                                                "Workout at 16:00",
                                                "Call Mum at 20:00",
                                                "Piano practice")
                                                .map { Task(it) }
                                ),
                                TaskList("Done",
                                        listOf("Pick up phone from repair on Monday",
                                                "Call electric company regarding outages",
                                                "Hangout with friends on Saturday",
                                                "Watch Lego Movie with brother",
                                                "Send laundry to dry cleaners",
                                                "Book dentist appointment",
                                                "Piano practice",
                                                "Meeting with Dr. Roberts at 10:00",
                                                "Buy new speakers",
                                                "Weekly language session",
                                                "Weekly music session",
                                                "Start writing CV",
                                                "Transfer money to savings account",
                                                "Call Uncles and Aunts",
                                                "Send phone to repair shop",
                                                "Clean up Drive folders",
                                                "Buy new hard drive",
                                                "Take out trash on Wednesday")
                                                .map { Task(it) }
                                )
                        )
                ),
                Board("University",
                        listOf(
                                TaskList("Assignments",
                                        listOf("Web Development assignment 2 on 11-Jan-19",
                                                "Machine Learning assignment 2 on 12-Nov-18",
                                                "Software Testing assignment 1 on 10-Dec-18",
                                                "Programming assignment 1 on 14-Dec-18")
                                                .map { Task(it) }
                                ),
                                TaskList("Due this week",
                                        listOf("Web Development assignment 1 on 10-Oct-18",
                                                "Machine Learning assignment 1 on 17-Oct-18")
                                                .map { Task(it) }
                                ),
                                TaskList("Done",
                                        listOf<String>()
                                                .map { Task(it) }
                                )
                        )
                ),
                Board("Final Year Project",
                        listOf(
                                TaskList("To Do",
                                        listOf("1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9")
                                                .map { Task(it) }
                                ),
                                TaskList("Today",
                                        listOf("1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9")
                                                .map { Task(it) }
                                ),
                                TaskList("Done",
                                        listOf("1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9")
                                                .map { Task(it) }
                                )
                        )
                ),
                Board("Android App",
                        listOf(
                                TaskList("To Do",
                                        listOf("1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9")
                                                .map { Task(it) }
                                ),
                                TaskList("Today",
                                        listOf("1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9")
                                                .map { Task(it) }
                                ),
                                TaskList("Done",
                                        listOf("1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9")
                                                .map { Task(it) }
                                )
                        )
                )
        )).update()
    }

}