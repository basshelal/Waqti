package uk.whitecrescent.waqti.model.persistence

/**
 * Shows that this queries the Database, useful if database queries are expensive.
 * Only used on Queries, not any writes
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION, AnnotationTarget.PROPERTY)
annotation class QueriesDataBase