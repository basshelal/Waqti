# Property

A Property is an attribute of a Task. A Task may exist with none or many Properties as they are optional, however a 
Task can only exist with one value or instance of each Property so at most a Task can have as many Properties as the 
total number of Properties supported.

The list of Properties available is subject to change.

Note that Constraints are a special type of Property that can modify the Task's lifecycle.

Properties are said to be descriptors whereas Constraints are enforcers.

## Properties List

### Constrainables

* **Time:** The point in natural time after which this Task will be relevant, this can also be referred to as 
scheduled time. If time is a Constraint then the Task cannot be killed until after that time and is in the SLEEPING 
state during that period, however, if time is not a Constraint then the Task can be killed freely and will be in its
previous state meaning no lifecycle change will be made. If time is a Property then it has no rules on killing the 
Task.

* **Duration:** The estimated amount of time that this Task will take, this is defined in any Time Measurement Unit, 
this can also be referred to as minimum duration. If duration is a Constraint then the Task cannot be killed in the 
midst of the duration, only after it has ended. If duration is a Property then it has no rules on killing the Task.

* **Checklist:** A list of checkable items that this Task may have. This is useful if the Task can be broken down into 
smaller chunks which can be represented as list items in a checklist. Items in a checklist can be checked or deleted. 
If a checklist is a Constraint then the Task cannot be killed unless every item in the checklist is checked. If 
checklist is a Property then it has no rules on killing the Task.

* **Deadline:** The point in natural time after which this Task can no longer be killed and thus is FAILED, this 
would include a user defined grace period. If deadline is a Constraint then after the deadline time passes plus the 
grace period the Task will be FAILED and thus no longer can be KILLED. If deadline is a Property it has no rules on 
failing or killing the Task, it will only show a notification that the Task was not killed on time and the used can 
still kill it at any point.

* **Target:** The user defined textual representation of a desirable target to be achieved by the user before killing
 this Task. If target is a Constraint then the Task cannot be killed unless the Target is checked. If target is a 
 Property then it has no rules on killing the Task and acts very similar to a description.

* **Before:** The Task that occurs before this Task. If before is a Constraint then this Task cannot be killed unless
 the before Task is killed, and if the before Task is FAILED then this Task will also be FAILED if it can be since 
 this Task will have a dependence on the state of the before Task. If before is a Property then it has no rules on 
 killing or failing the Task, it will just be a description of the Task that comes before this one, good for ordering
 Tasks but not enforcing any ordering of completion.
 
* **SubTasks:** The list of sub-Tasks this Task has, a Task can have zero to potentially many sub-Tasks but has zero by
default. If SubTasks is a Constraint then the state of the sub-Tasks is shared upwards to the parent, meaning if this
Task's sub-Tasks contains a FAILED Task then this Task is FAILED, if this Task's sub-Tasks contains a non-killed 
non-optional Task then this Task cannot be killed. If SubTasks is a Property then it has no rules on killing or 
failing the Task, the sub-Tasks' states will make no difference to this Task.

### Non-Constrainables

* **Priority:** The user defined level of importance of a Task represented as a String with a number representing 
importance level. Priority is particularly useful in solving or mediating Task collisions. A Task collision occurs
when two or more Tasks share the same time, if they have different priority levels then the Task with the higher 
priority level will be shown and a collision warning will be displayed to the user, this is called a weak collision. 
If the tasks have equal priority levels then the user must mediate or solve the collision themselves, this is called 
a strong collision. Priority can not be a Constraint.

* **Labels:** The user defined category(s) that this Task belongs to. A Task can belong to or have 0 or many labels. 
Labels are used as a way of categorizing Tasks and are thus helpful in filtering and analytics. Labels can not be a
Constraint.

* **Optional:** Shows whether the Task is optional or not. An optional Task is one that is to be done or pursued if 
there is free time, thus an optional Task has less priority than a non-optional Task (mandatory Task) even if the 
Task has the lowest priority, this makes optional Tasks the lowest priority of all Tasks. Optional can not be a 
Constraint.In many ways the optional Property acts similar to the priority Property except in that optional is lower 
priority than the lowest priority.
                
* **Description:** A textual description of this Task, useful for if the Task is complex or requires further 
information that the title cannot provide. Description can not be a Constraint.
