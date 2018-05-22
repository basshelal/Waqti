# Tuple

A Tuple is a Collection of Tasks that acts as an Ordering, meaning the Tasks in it are meant to occur 
sequentially, so Task 4 cannot be killed before Task 3 which cannot be killed before Task 2 and so on. This is done 
on the List level, so a List contains Tuples.

Tuples are by default an unconstrained ordering but there is an option to constrain some or all Tasks.

This is useful for defining groups of Tasks that have an ordering relationship that are not necessarily parts of a larger
parent Task, these may be unrelated Tasks and the only thing they have in common is the need to be executed sequentially one after the other.
This differs from a Super/Sub Task relationship in which ordering is not enforced and the SubTasks make up the parts required of the SuperTask.
This is a slight difference but a significant one.

A Routine is an example of a type of Tuple.