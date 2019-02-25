package uk.whitecrescent.waqti

/**
 * Considered final, write since when so that if changed we know when to revert back
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class FinalSince(val waqtiVersion: WaqtiVersion)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class TestedSince(val waqtiVersion: WaqtiVersion)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class DocumentedSince(val waqtiVersion: WaqtiVersion)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class TestedDocumentedAndFinalSince(val waqtiVersion: WaqtiVersion)

/**
 * Is expected to change
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class NonFinal

/**
 * Needs some kind of testing
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class Untested

/**
 * Needs updated tests
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class UpdateTests

/**
 * Needs some kind of documenting
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class UnDocumented

/**
 * Needs updated documentation
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class UpdateDocumentation

/**
 * Leave this for a later version or phase
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD,
        AnnotationTarget.FUNCTION, AnnotationTarget.FILE, AnnotationTarget.EXPRESSION)
annotation class ForLater

/**
 * An idea for the future to consider
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class FutureIdea

/**
 * Feature missing that should exist
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class MissingFeature

/**
 * A non-bug, basically it's something that's not technically a bug but it's still annoying, an
 * inconvenience
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class Inconvenience

/**
 * Bug, preferable if you write how to replicate the bug
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class Bug

/**
 * Unsure about something so check it out later and verify
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class NeedsChecking

/**
 * Needs optimizations later
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class NeedsOptimization

/**
 * Needs re-organizing or re-structuring
 */
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.FILE)
annotation class NeedsReOrganizing