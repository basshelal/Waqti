package uk.whitecrescent.waqti.model

/**
 * Shows that the following will be different depending on the implementation platform eg, Android, JavaFX etc
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION)
@MustBeDocumented
annotation class ImplementationVariable