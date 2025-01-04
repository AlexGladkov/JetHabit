package feature.tracker.domain

import feature.tracker.data.TrackerDao
import feature.tracker.data.TrackerEntity
import kotlinx.datetime.LocalDate
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

class UpdateTrackerValueUseCase(
    private val trackerDao: TrackerDao
) {
    suspend fun execute(habitId: String, value: Double, date: LocalDate) {
        trackerDao.insert(
            TrackerEntity(
                id = UUID.generateUUID().toString(),
                habitId = habitId,
                timestamp = date.toString(),
                value = value
            )
        )
    }
} 