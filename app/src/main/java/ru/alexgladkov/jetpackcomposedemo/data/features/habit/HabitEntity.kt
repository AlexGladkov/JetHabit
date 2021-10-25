package ru.alexgladkov.jetpackcomposedemo.data.features.habit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = HabitEntity.TABLE_HABIT_NAME)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0L,

    @ColumnInfo(name = HABIT_TITLE)
    val title: String,

    @ColumnInfo(name = HABIT_IS_GOOD)
    val isGood: Boolean
) {

    companion object {
        const val TABLE_HABIT_NAME = "Habbit_Entity"
        const val HABIT_TITLE = "Habbit_Title"
        const val HABIT_IS_GOOD = "Habbit_Is_Good"
    }
}