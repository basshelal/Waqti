package uk.whitecrescent.waqti.persistence

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.getTasks
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.mustEqual
import uk.whitecrescent.waqti.size
import java.io.File

@DisplayName("Database")
class Database : BasePersistenceTest() {

    @DisplayName("test")
    @Test
    fun test() {
        val file = File("src\\test\\java\\uk\\whitecrescent\\waqti\\persistence\\test.json")
        file.setReadable(true)
        file.setWritable(true)
        getTasks(10)
        Database.tasks.size mustEqual 10
        Database.export(file)
    }
}