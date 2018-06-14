package uk.whitecrescent.waqti.model.collections

/**
 * Used to indicate that is either not recommended or not necessary (or both) to override the function, property,
 * constructor or class (even though it is allowed). This would usually be because the current implementation should
 * be sufficient for most applications and therefore an override (without calling super) cannot guarantee a desired
 * outcome.
 *
 * The details of why it is not recommended to override and what actions to take when needed to override should be
 * documented clearly.
 *
 * @author Bassam Helal
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class NoOverride

// TODO: 16-Apr-18 Document this
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class SimpleOverride