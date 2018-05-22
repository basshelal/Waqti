package uk.whitecrescent.waqti.tests.collections

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.ChronoUnit
import uk.whitecrescent.waqti.collections.Calendar

@DisplayName("Calendar Tests")
class CalendarTests {

    @DisplayName("Get Calendar")
    @Test
    fun testCalendarGet() {
        Calendar.get(365L, ChronoUnit.DAYS)
    }
}