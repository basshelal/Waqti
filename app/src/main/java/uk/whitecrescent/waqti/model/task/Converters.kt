package uk.whitecrescent.waqti.model.task

import io.objectbox.converter.PropertyConverter
import uk.whitecrescent.waqti.model.Date
import uk.whitecrescent.waqti.model.Duration
import uk.whitecrescent.waqti.model.Time
import java.util.Scanner

class WaqtiPropertyConverter<T> : PropertyConverter<T, String> {

    override fun convertToDatabaseValue(entityProperty: T): String {
        return entityProperty.convert()
    }

    @Suppress("UNCHECKED_CAST")
    override fun convertToEntityProperty(databaseValue: String?): T {
        if (databaseValue == null) throw NullPointerException("Database value is null in " +
                "WaqtiPropertyConverter")
        return reverseConvert(databaseValue) as T
    }

}

fun Any?.convert(): String {
    return when {
        this == null -> throw NullPointerException("Cannot convert null value!")
        Iterable::class.java.isAssignableFrom(this::class.java) -> {

            val result = StringBuilder("${this::class.simpleName}\n")

            @Suppress("UNCHECKED_CAST")
            (this as Iterable<Any?>).forEach { result.append("${it.convert()}\n") }

            result.toString()
        }
        else -> "${this::class.simpleName}\n$this"
    }
}

// returns the type based on the given string
fun reverseConvert(string: String): Any {
    val type = string.firstLine()
    val contents = string.removeType()
    return when (type) {
        "LocalDateTime" -> Time.parse(contents)
        "LocalDate" -> Date.parse(contents)
        "Duration" -> Duration.parse(contents)
        "Priority" -> Priority.fromString(contents)
        "Label" -> Label.fromString(contents)
        "ArrayList" -> parseArrayList(contents)
        "Boolean" -> (contents == "true")
        "String" -> contents
        "Long" -> contents.toLong()
        "Int" -> contents.toInt()
        else -> throw UnsupportedOperationException("Reverse convert failed!\n$type\n\n $contents")
    }
}

fun parseArrayList(string: String): ArrayList<Any> {
    val result = ArrayList<Any>()

    val scanner = Scanner(string)
    while (scanner.hasNextLine()) {
        result.add(reverseConvert(scanner.nextLine() + "\n" + scanner.nextLine()))
    }

    return result
}

fun String.removeType(): String {
    return this.substringAfter("\n")
}

fun String.firstLine(): String {
    return this.substringBefore("\n")
}