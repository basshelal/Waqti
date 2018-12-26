# Plan

## Current Plan

As of 26-Dec-18 we are probably done, the BoardView works quite well, not perfect but good 
enough, we will probably delete this file sometime in the near future

As of 21-Dec-18 we have a very very very very basic working BoardView, it needs optimizations and
 we still are missing autoscrolling, but it's not too bad, we used option 2 which was the best 
 option anyway

So it seems using the ItemTouchHelper way works well only for a current RecyclerView but moving 
across multiple RecyclerViews may prove to be a problem using this method, so we may have to use 
the old school way of dragging [here](https://developer.android.com/guide/topics/ui/drag-drop).

Currently as of 17-Dec-18 we have a BoardView that allows for dragging its lists and dragging the 
items of the lists inside a single list, dragging an item across lists does not work and is the 
reason for this problem, so we must do it using different ways.

We have 4 possible options:

* Use the current way and have inconvenient dragging Tasks across lists, this might be ok but is 
not very functional, essentially in order to drag a Task from a list on one end to another you'd 
need to keep dragging and dropping it from one list to the next, *this is sub-optimal but is 
pretty much already implemented right now*

* Use the drag and drop old school way, so we basically just change any dragging code to use the 
old school way of dragging in the link above, we'd either completely get rid of the 
ItemTouchHelper stuff we've already written or we try to mix them together, so ItemTouchHelper 
for dragging inside this list but the other way when dragging across lists. I'd prefer we just do
 all of it using the old school way to stay consistent, *this is probably the best way as of 
17-Dec-18*

* Borrow a lot of the code from the other library, here we'd just re-purpose the code from the 
old library and do it ourselves how we like it, *this is not desired but if the above two prove 
troublesome enough then we should consider it*

* Force the user to move Tasks between lists using an option menu rather than dragging, this 
would mean a user cannot drag a Task across a list and would have to move a task from one list to
 another using some kind of options menu, *this is a last resort as it is the worst possible thing
 to do but if we're completely stumped we'll just do this until we can fix it later*



## Old Plan

### Draggable View

A View that can be dragged and dropped across the screen on its own. This should most probably be
 a ViewGroup and dragging can be started either from the entire view itself or from a child of 
the ViewGroup. This is to allow Draggable Lists, which we will only allow to be dragged from the 
header of the list

### Draggable Recycler View

A Recycler View that allows for all of its elements to be dragged, the issue here is that there 
needs to be a placeholder view that will be empty that will show where the currently dragging 
view will be dropped, the items in the Recycler View will also need to respond to the ongoing 
dragging

### Board View

A Horizontal Recycler View that contains Draggable Recycler Views, each Draggable Recycler View 
allows for items to be dragged around the entire screen, and it itself can be dragged around.

Board View can maybe be thought of as a Draggable Recycler View itself. Each List can be dragged 
horizontally across the board when dragged from the header of the list.

The Board View will need to AutoScroll horizontally but we can worry about this later.