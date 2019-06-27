package uk.whitecrescent.waqti.backend

interface Committable {
    fun commit()

    companion object {
        operator fun invoke(commit: () -> Unit): Committable {
            return object : Committable {
                override fun commit() = commit()
            }
        }
    }
}