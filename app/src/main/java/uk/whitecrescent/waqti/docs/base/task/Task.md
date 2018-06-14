# Task

A Task is the smallest, ***independent*** unit in the Waqti Time Management System.

A Task has Properties and a lifecycle.

Properties add detail and information to the Task, Properties can be set and modified by the user as well as by time.

The Task lifecycle shows which state the Task is currently in which affects its behavior.

A Task's state can only be modified by 2 actors, the user and time.

Properties can be Constraints which are a special type of Property that can modify the Task's lifecycle.

Although Constraints can modify the Task's state, they cannot do this alone, they do this as a result of either and 
action or lack of action from either time or the user or both.

## The Task Lifecycle

Tasks have a lifecycle. A Task may be in only one of the of the following lifecycle states at a given point in time.

    --> E --> K
       ^ \
      /   \
     v     v
     S <-- F

### SLEEPING

A Task is said to be in the SLEEPING state if it is not relevant, this means it is not EXISTING and cannot be killed 
nor failed, it is waiting to become EXISTING or relevant again.

This can happen as a consequence of two possible scenarios:

* The Task has a Scheduled Time and that time is in the future, so the Task is waiting to be EXISTING

* The Task has been failed and has been delegated and given a new Scheduled Time to be EXISTING again after that time

The SLEEPING state can lead to the EXISTING state only and can come from the FAILED state or the initial point of 
the Task's creation. In the Task Life-cycle it is the initial state.

The SLEEPING state is attributed to a Task that is not currently relevant and is waiting to become so, relevant 
meaning EXISTING and can be killed and or can be failed.

An example of a SLEEPING Task would be the following:

    "Prepare tomorrow's breakfast"
    id    !failable    killable    SLEEPING
        C:
            time: Friday 10th November 16:00
            
This Task is only relevant and EXISTING at 16:00 on Friday 10th November, before this time it is not EXISTING nor 
killable, this is because of the time Property. Before that time, the Task is SLEEPING.

A Task without a time Property however cannot be SLEEPING and so it either goes to the EXISTING state or remains in 
the FAILED state, if that is where it is.

### EXISTING

A Task is said to be in the EXISTING state if it is relevant, meaning it can be killed and or failed. It is currently
 in its time of importance or use to the user.
 
If a Task has no means of failing or being killed then it will be EXISTING indefinitely until any of those is 
performed.
 
The EXISTING state can lead to the KILLED state if the Task is killed, or the FAILED state if the Task is failed. 
The EXISTING state can only come from the SLEEPING state after the SLEEPING state has ended as a result of the 
scheduled time passing.
 
The EXISTING state is attributed to a Task that is currently killable and or failable at this point in time.
 
An example of an EXISTING Task would be the following:

    "Tidy up office space"  
    id    failable    killable    EXISTING
        C:
            time: Friday 10th November 18:00
            deadline: Friday 10th November 20:00
            
This Task becomes EXISTING at Friday 10th November 18:00, before that time it is SLEEPING and after Friday 10th 
November 20:00 plus the grace period then it is no longer EXISTING.
            
 If the Task had no Constraints defining time then it is possible for it to exist in the EXISTING state indefinitely 
 until the user takes some action on it.

### FAILED

A Task is said to be in the FAILED state if it was at some point prior EXISTING and could be killed and now is no 
longer EXISTING and can no longer be killed, this is usually as a result of some unmet Constraint. A Task that allows
failing is said to be failable. If a Task is not failable then this state is impossible.

A Task with a Constraint that can be at some point no longer possible is a Task that can be failed since the Constraint can no longer
be met and so it is no longer killable, so it is Failed.

The FAILED state can only lead to the SLEEPING sate and can only come from the EXISTING state.

The FAILED state is attributed to a Task that was able to be killed previously and now can no longer be killed.

An example of a FAILED Task would be the following:

    "Buy mushrooms for tomorrow's lunch"
    id    failable    killable    FAILED
        P:
            description: "Get mushrooms before Tesco closes at 17:00"
        C:
            time: Friday 10th November 13:00
            deadline: Friday 10th November 17:00
            
This Task is failable, since it has a deadline Constraint, after which plus the grace period the Task will be FAILED 
and can no longer be killed. The Task can be re-scheduled or delegated at which point it will be SLEEPING to become 
EXISTING and killable again at some point.

### KILLED

A Task is said to be in the KILLED state if it was at some point prior EXISTING and is no longer so, it could be 
killed before and now is not. This corresponds to a Task being done hence the only way a Task can be killed is by the
user. If a Task is not killable then this state is impossible.
 
In order for a Task to be killed its Constraints must be met.

The KILLED state cannot lead to any other state, it is the final state in a Task's lifecycle. The KILLED state can 
only come from the EXISTING state, only an EXISTING Task can be killed.

The KILLED state is attributed to a Task that was EXISTING and could be killed and is now no longer EXISTING and can 
no longer be killed. A Task is no longer relevant after being killed.

An example of a KILLED Task is the following:

    "Eat Breakfast"
    id    failable    killable    KILLED
        C:
            time: Friday 10th November 09:00
            
This task is in the KILLED state since it was able to be killed and has met all its Constraints and was killed by the 
user signifying that it has been completed and is no longer relevant.

---------------------------------------------------------------------------------------------------------------------------

# Old Notes

### Immortal

A Task is said to be in the Immortal state if it is a special kind of Task called the Template Task, a Task that 
exists solely to create copies of itself which are actual Tasks that have life-cycles of their own.

The Immortal state is attributed to a Template Task, which is a Task that has no Existing time and is not failable nor 
killable.

An example of this would be the following Task:

    Properties:
        Duration: 30 minutes
        Blueprint: True

This task is in the Immortal state since it is a Blueprint Task, it cannot exist, and is not failable nor killable. 
Its sole purpose is to create copies of itself that are regular Tasks.

### Notes on the Task lifecycle:

* If a SuperTask has been killed then its SubTasks are all killed as well, provided all Constraints are met. Constraints of SubTasks
 should by definition be Constraints of the SuperTask (this should be a selectable option, to Constrain the SuperTask to its SubTasks,
  if not Constrained and the SuperTask is killed the SubTasks will be either independent Tasks or killed as well)

## Other Task Notes

There's a difference between killable and can be killed, and failable and can be failed.

### Killable

A Task is killable when all its Constraints are met and it has not yet been failed if it is failable

### Failable

A Task is failable when it has a Constraint that can be failed and the Task is currently Existing as well.

### Template Tasks (Self Replicating Tasks)

A Blueprint Task is a Task that exists only to create copies of itself that are normal Tasks.

A Blueprint Task therefore is always in the Immortal state.

