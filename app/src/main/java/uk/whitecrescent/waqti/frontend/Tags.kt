package uk.whitecrescent.waqti.frontend

// View Fragments
const val VIEW_BOARD_LIST_FRAGMENT = "ViewBoardListFragment"
const val VIEW_BOARD_FRAGMENT = "ViewBoardFragment"
const val VIEW_LIST_FRAGMENT = "ViewListFragment"
const val VIEW_TASK_FRAGMENT = "ViewTaskFragment"

// Create Fragments
const val CREATE_BOARD_FRAGMENT = "CreateBoardFragment"
const val CREATE_LIST_FRAGMENT = "CreateListFragment"
const val CREATE_TASK_FRAGMENT = "CreateTaskFragment"

// Other Fragments
const val ABOUT_FRAGMENT = "AboutFragment"
const val SETTINGS_FRAGMENT = "SettingsFragment"

// Unknown
/**
 * Represents any possible Fragment, meaning we don't know which one
 */
const val ANY_FRAGMENT = "AnyFragment"
/**
 * The previous Fragment in the back stack
 */
const val PREVIOUS_FRAGMENT = "PreviousFragment"
/**
 * Represents no Fragment, used only when initializing the layout when no Fragment has been added
 * yet
 */
const val NO_FRAGMENT = "NoFragment"