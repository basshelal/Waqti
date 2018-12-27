# Waqti Android (Work in Progress)

README last updated on Thur-27-Dec-18

![Waqti Demo Thur-27-Dec-18](WaqtiDemo-27-Dec-18.gif)

## Overview

Waqti (My Time in Arabic) is a Time Management System based on boards and lists, very similar to 
Kanban and heavily inspired by Trello.

Waqti was motivated by a frustration with using Trello as a daily time management system. Trello 
is excellent for many applications, particularly project management, but lacks in terms of being 
a personal time management system. Waqti is intended to fill the gaps where Trello fails in this 
regard.

## Implementation Details

This is the Android implementation. Waqti was intended to 
be a platform independent system (hence the other repo named 
[Waqti](https://github.com/basshelal/Waqti)) however many issues arose as
a result of this and so platform dependence was required, particularly in terms of persistence. 
Despite that, there is no significant change/rewrite needed to implement on  another platform, 
at least in terms of models, so in theory the entire back-end can be transferred over to a new 
platform such as JavaFX while only changing the front-end and possibly the database (ObjectBox, 
our database, is available on Desktop and Server Applications as well as Android).

Currently the back-end (Models and Database) are almost completely finished and some basic 
front-end is usable as well. Waqti uses a Single-Activity design so all Android UI controllers 
are Fragments within the same Activity, thus they can share resources among each other, 
particularly the Activity's ViewModel.

This project is written entirely in Kotlin and makes use of multiple libraries including but not 
limited to:
* RxJava 2 (for concurrency)
* ObjectBox (for persistence)
* JUnit5 (for local unit tests)
* GSON (to convert objects to JSON for ObjectBox)
* ThreeTenABP (for converting Java 8 Time API to Android usable)

This is still a Work in Progress but if you would like to read code I would suggest
[Task.kt](https://github.com/basshelal/Waqti-Android/blob/master/app/src/main/java/uk/whitecrescent/waqti/model/task/Task.kt)
as most of the code is there with plenty of documentation. 

There are also documents outlining the general requirements and features planned for the system 
[here](https://github.com/basshelal/Waqti-Android/tree/master/app/src/main/java/uk/whitecrescent/waqti/docs)

There also exist a large suite of unit tests (currently 200+ tests) which are located
[here](https://github.com/basshelal/Waqti-Android/tree/master/app/src/test/java/uk/whitecrescent/waqti)