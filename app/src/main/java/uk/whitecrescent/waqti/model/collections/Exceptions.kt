package uk.whitecrescent.waqti.model.collections

class ElementNotFoundException(element: Any = "") : NoSuchElementException("Element $element not found")