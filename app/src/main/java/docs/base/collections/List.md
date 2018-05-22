# List

A List is an abstract Collection of Tasks that groups Tasks sequentially and shows only those that satisfy a given 
List Property which may be Task Property(s).s

For example a List "Today" can be the Collection of Tasks that have their time Property being equivalent to today's date,
 another List "Soon" can be the Collection of Tasks that have their Constraints ending soon etc, these Lists should be flexibly 
 created by the user and also powerful enough to generate themselves.

2 attributes: What to show and how to sort it.

What to show just means, from all the Tasks available which ones should this List show, this is done by giving a specific Task Property(s)
 to filter through, similar to SELECT * FROM * WHERE ... in SQL and the ... is the what to show.

How to sort it means that after a List has been filtered to show only certain Tasks from the entire Collection of Tasks,
 how should these Tasks be sorted in this List, in a List the order should matter, if it does not the List would be unordered,
  this may or may not be useful.