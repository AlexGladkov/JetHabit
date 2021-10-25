package ru.alexgladkov.jetpackcomposedemo.data.features.daily

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = DailyEntity.TABLE_DAILY_NAME)
data class DailyEntity(
    @PrimaryKey
    val date: String,

    @ColumnInfo(name = DAILY_HABIT_IDS)
    val habitItemIdsWithStatuses: String,
) {

    companion object {
        const val TABLE_DAILY_NAME = "Daily_Entity"
        const val DAILY_HABIT_IDS = "Daily_Habit_Ids"
    }
}
