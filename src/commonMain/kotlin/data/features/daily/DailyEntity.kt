package data.features.daily

data class DailyEntity(
    val date: String,
    val habitItemIdsWithStatuses: String,
) {

    companion object {
        const val TABLE_DAILY_NAME = "Daily_Entity"
        const val DAILY_HABIT_IDS = "Daily_Habit_Ids"
    }
}
