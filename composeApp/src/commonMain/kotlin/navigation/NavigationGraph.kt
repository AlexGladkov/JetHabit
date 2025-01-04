object NavigationGraph {
    const val DAILY_START = "daily_start"
    const val DAILY_DETAIL = "daily_detail/{habitId}"
    const val HEALTH = "health"
    const val HEALTH_TRACK = "health_track/{habitId}"
    const val STATISTICS = "statistics"
    const val SETTINGS = "settings"
    const val CREATE_HABIT = "create_habit"

    fun dailyDetail(habitId: String) = "daily_detail/$habitId"
    fun healthTrack(habitId: String) = "health_track/$habitId"
    fun createHabit(type: String? = null) = if (type != null) "$CREATE_HABIT?type=$type" else CREATE_HABIT
} 