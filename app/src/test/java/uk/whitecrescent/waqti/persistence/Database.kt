package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.threetenabp.now
import uk.whitecrescent.threetenabp.rfcFormatted
import uk.whitecrescent.threetenabp.secs
import uk.whitecrescent.threetenabp.till
import uk.whitecrescent.waqti.backend.persistence.Database
import uk.whitecrescent.waqti.backend.task.DEBUG
import uk.whitecrescent.waqti.extensions.size
import uk.whitecrescent.waqti.getFilledBoardList
import uk.whitecrescent.waqti.mustEqual
import java.io.File

@DisplayName("Database")
class Database : BasePersistenceTest() {

    @DisplayName("test")
    @Test
    fun test() {
        DEBUG = false
        val file = File("src\\test\\java\\uk\\whitecrescent\\waqti\\persistence\\test.json")
        file.setReadable(true)
        file.setWritable(true)
        getFilledBoardList(
                amountOfBoards = 1,
                amountOfLists = 2,
                amountOfTasks = 1
        )

        var startTime = now
        println("EXPORTING FILE STARTED AT: ${startTime.rfcFormatted}")

        Database.export(file)

        var endTime = now
        println("EXPORTING FILE ENDED AT: ${endTime.rfcFormatted}")
        println("OPERATION TOOK: ${(startTime till endTime).secs} SECONDS")


        startTime = now
        println("IMPORTING FILE STARTED AT: ${startTime.rfcFormatted}")

        Database.import(file, Database.ImportMethod.REPLACE)
        Database.tasks.size mustEqual 1 * 2 * 1
        Database.taskLists.size mustEqual 2 * 1
        Database.boards.size mustEqual 1

        endTime = now
        println("IMPORTING FILE ENDED AT: ${endTime.rfcFormatted}")
        println("OPERATION TOOK: ${(startTime till endTime).secs} SECONDS")


        // 100,000 Tasks took 6.2 seconds to export and has size 131MB
        // 10,000 Tasks took 0.8 seconds to export and has size 13MB
        // 1,000 Tasks took 0.19 seconds to export and has size 1.3MB
    }
}