package ru.alexgladkov.jetpackcomposedemo.data.features.habit

data class HabitEntity(
    val itemId: Long = 0L,
    val title: String,
    val isGood: Boolean
) {

    companion object {
        const val TABLE_HABIT_NAME = "Habbit_Entity"
        const val HABIT_TITLE = "Habbit_Title"
        const val HABIT_IS_GOOD = "Habbit_Is_Good"
    }
}