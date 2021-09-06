package ru.alexgladkov.jetpackcomposedemo.data.features.habbit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = HabbitEntity.TABLE_HABBIT_NAME)
data class HabbitEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0L,

    @ColumnInfo(name = HABBIT_TITLE)
    val title: String,

    @ColumnInfo(name = HABBIT_IS_GOOD)
    val isGood: Boolean
) {

    companion object {
        const val TABLE_HABBIT_NAME = "Habbit_Entity"
        const val HABBIT_TITLE = "Habbit_Title"
        const val HABBIT_IS_GOOD = "Habbit_Is_Good"
    }
}