package uk.whitecrescent.waqti

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.persistence.BasePersistenceTest

@DisplayName("Random Tests")
class RandomTests : BasePersistenceTest() {

    @DisplayName("Test")
    @Test
    fun test() {

        getTasks(10_000)

        println("")

    }

}