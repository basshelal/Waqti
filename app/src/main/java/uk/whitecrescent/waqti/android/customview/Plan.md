# Plan

## Draggable View

A View that can be dragged and dropped across the screen on its own. This should most probably be
 a ViewGroup and dragging can be started either from the entire view itself or from a child of 
the ViewGroup. This is to allow Draggable Lists, which we will only allow to be dragged from the 
header of the list

## Draggable Recycler View

A Recycler View that allows for all of its elements to be dragged, the issue here is that there 
needs to be a placeholder view that will be empty that will show where the currently dragging 
view will be dropped, the items in the Recycler View will also need to respond to the ongoing 
dragging

## Board View

A Horizontal Recycler View that contains Draggable Recycler Views, each Draggable Recycler View 
allows for items to be dragged around the entire screen, and it itself can be dragged around.

Board View can maybe be thought of as a Draggable Recycler View itself. Each List can be dragged 
horizontally across the board when dragged from the header of the list.

The Board View will need to AutoScroll horizontally but we can worry about this later.