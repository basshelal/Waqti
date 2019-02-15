package uk.whitecrescent.waqti.task

import uk.whitecrescent.waqti.BaseTest
import uk.whitecrescent.waqti.backend.task.Task
import uk.whitecrescent.waqti.testTask

abstract class BaseTaskTest : BaseTest() {

    var task: Task = testTask

    override fun beforeEach() {
        super.beforeEach()

        task = testTask
    }

}