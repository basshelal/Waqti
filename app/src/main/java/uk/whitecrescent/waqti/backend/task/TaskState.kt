package uk.whitecrescent.waqti.backend.task

import uk.whitecrescent.waqti.FinalSince
import uk.whitecrescent.waqti.WaqtiVersion

/**
 * The states that a [Task] can be in.
 *
 * A Task can only be in one state at any given point in time.
 *
 * Below is a basic outline of the Task Lifecycle
 * ```
 * --> E --> K
 *    ^ \
 *   /   \
 *  v     v
 *  S <-- F
 *  ```
 * @see Task
 */
@FinalSince(WaqtiVersion.FEB_2019)
enum class TaskState {

    /**
     * A Task is said to be in the SLEEPING state if it is not relevant, this means it is not EXISTING and cannot be killed
     * nor failed, it is waiting to become EXISTING or relevant again.
     *
     * This can happen as a consequence of two possible scenarios:
     *  * The Task has a Scheduled Time and that time is in the future, so the Task is waiting to
     * be EXISTING
     *  * The Task has been failed and has been delegated and given a new Scheduled Time to be
     * EXISTING again after that time
     *
     * The SLEEPING state can lead to the EXISTING state only and can come from the FAILED state or
     * the initial point of the Task's creation. In the Task Life-cycle it is the initial state.
     *
     * The SLEEPING state is attributed to a Task that is not currently relevant and is waiting to become so, relevant
     * meaning EXISTING and can be killed and or can be failed.
     */
    SLEEPING,

    /**
     * A Task is said to be in the EXISTING state if it is relevant, meaning it can be killed and or failed. It is currently
     * in its time of importance or use to the user.
     *
     * If a Task has no means of failing or being killed then it will be EXISTING indefinitely
     * until any of those is performed.
     *
     * The EXISTING state can lead to the KILLED state if the Task is killed, or the FAILED state
     * if the Task is failed.
     * The EXISTING state can only come from the SLEEPING state after the SLEEPING state has ended
     * as a result of the scheduled time passing.
     *
     * The EXISTING state is attributed to a Task that is currently killable and or failable at
     * this point in time.
     */
    EXISTING,

    /**
     * A Task is said to be in the FAILED state if it was at some point prior EXISTING and could be killed and now is no
     * longer EXISTING and can no longer be killed, this is usually as a result of some unmet
     * Constraint. A Task that allows failing is said to be failable. If a Task is not failable
     * then this state is impossible.
     *
     * A Task with a Constraint that can be at some point no longer possible is a Task that can be
     * failed since the Constraint can no longer be met and so it is no longer killable, so it is
     * Failed.
     *
     * The FAILED state can only lead to the SLEEPING sate and can only come from the EXISTING
     * state.
     *
     * The FAILED state is attributed to a Task that was able to be killed previously and now can
     * no longer be killed.
     */
    FAILED,

    /**
     * A Task is said to be in the KILLED state if it was at some point prior EXISTING and is no longer so, it could be
     * killed before and now is not. This corresponds to a Task being done hence the only way a
     * Task can be killed is by the user. If a Task is not killable then this state is impossible.
     *
     * In order for a Task to be killed its Constraints must be met.
     *
     * The KILLED state cannot lead to any other state, it is the final state in a Task's
     * lifecycle. The KILLED state can only come from the EXISTING state, only an EXISTING Task
     * can be killed.
     *
     * The KILLED state is attributed to a Task that was EXISTING and could be killed and is now
     * no longer EXISTING and can no longer be killed. A Task is no longer relevant after being
     * killed.
     */
    KILLED,

    /**
     * As a good practice, this is just any other state. This is impossible and should never
     * occur and if it does, something terrible has gone wrong and you must contact the
     * authorities immediately, or you could just throw an [IllegalStateException].
     */
    UNKNOWN
}