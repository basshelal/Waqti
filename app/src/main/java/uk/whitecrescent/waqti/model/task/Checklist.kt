package uk.whitecrescent.waqti.model.task

data class ListItem(val value: String, var isChecked: Boolean = false)

// TODO: 30-Mar-18 Update this to look modern and idiomatic
// TODO: 31-Mar-18 Update this to be a WaqtiList sometime!
// TODO: 14-May-18 Either make this independent of a Task or tightly coupled to Task
// so either a Collection or a component of Task (cannot exist without a Task)
class Checklist(vararg itemValues: String) : Iterable<ListItem> {

    private val list = ArrayList<ListItem>()

    init {
        addAll(*itemValues)
    }

    fun size() = list.size

    fun asList(): List<ListItem> = this.list

    fun addItem(listItem: ListItem) = this.list.add(listItem)

    fun addItem(listItemValue: String) = this.list.add(ListItem(listItemValue))

    fun addAll(vararg listItems: ListItem) = this.list.addAll(listItems)

    fun addAll(vararg listItemValues: String) {
        for (itemValue in listItemValues) {
            addItem(itemValue)
        }
    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        if (toIndex > size() - 1 || toIndex < 0 || fromIndex > size() - 1 || fromIndex < 0) {
            throw IllegalArgumentException("Index doesn't exist!")
        }
        if (fromIndex == toIndex) {
            return
        }

        val itemToMove = list[fromIndex]

        if (fromIndex < toIndex) {

            for ((i, element) in list.filter { list.indexOf(it) in (fromIndex + 1)..toIndex }.withIndex()) {
                list[fromIndex + i] = element
            }
            list[toIndex] = itemToMove

        } else if (fromIndex > toIndex) {

            for ((i, element) in list.filter { list.indexOf(it) in toIndex..(fromIndex - 1) }.asReversed().withIndex()) {
                list[fromIndex - i] = element
            }
            list[toIndex] = itemToMove

        }

        // everything after fromIndex push left by 1 and everything after toIndex don't move, just like everything before fromIndex
        // basically only manipulate what's between fromIndex and toIndex
    }

    fun checkItem(index: Int) {
        this.list[index].isChecked = true
    }

    fun checkItem(item: ListItem) {
        getItemByReference(item)?.isChecked = true
    }

    fun uncheckItem(index: Int) {
        this.list.get(index).isChecked = false
    }

    fun uncheckItem(item: ListItem) {
        getItemByReference(item)?.isChecked = false
    }

    fun deleteItem(index: Int) = this.list.removeAt(index)

    fun deleteItem(item: ListItem) = this.list.remove(getItemByReference(item))

    fun clear() = list.clear()

    fun isEmpty() = list.isEmpty()

    fun getAllCheckedItems() = this.list.filter { it.isChecked }

    fun checkedItemsSize() = getAllCheckedItems().size

    fun getAllUncheckedItems() = this.list.filter { !it.isChecked }

    fun uncheckedItemsSize() = getAllUncheckedItems().size

    private fun getItemByReference(item: ListItem) = this.list.find { it == item }

    private fun getItemByContents(value: String) = this.list.find { it.value == value }

    //region Operators

    operator fun get(index: Int) =
            this.list[index]

    operator fun get(listItem: ListItem) =
            getItemByReference(listItem)

    operator fun get(string: String) =
            getItemByContents(string)

    operator fun set(index: Int, listItem: ListItem) {
        this.list[index] = listItem
    }

    operator fun set(index: Int, string: String) {
        this.list[index] = ListItem(string)
    }

    operator fun contains(listItem: ListItem) =
            this.list.contains(listItem)


    //endregion Operators

    //region Overridden from kotlin.Any and kotlin.collections.Iterable

    override fun hashCode() = list.hashCode()

    override fun equals(other: Any?) =
            other is Checklist && this.toString() == other.toString()

    override fun toString() = list.toString()

    override fun iterator() =
            list.iterator()

    //endregion Overridden from kotlin.Any and kotlin.collections.Iterable
}