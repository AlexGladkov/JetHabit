package feature.feed.domain

import feature.feed.data.ActivityFeedDao
import feature.feed.data.ActivityFeedEntity
import kotlinx.coroutines.flow.Flow

class GetActivityFeedUseCase(
    private val activityFeedDao: ActivityFeedDao
) {

    suspend fun execute(limit: Int = 50): List<ActivityFeedEntity> {
        return activityFeedDao.getActivityFeed(limit)
    }

    fun executeFlow(): Flow<List<ActivityFeedEntity>> {
        return activityFeedDao.getActivityFeedFlow()
    }
}
