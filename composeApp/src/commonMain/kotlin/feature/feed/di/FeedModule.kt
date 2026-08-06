package feature.feed.di

import feature.feed.data.ActivityFeedDao
import feature.feed.domain.CalculateStreakUseCase
import feature.feed.domain.DetectBrokenStreaksUseCase
import feature.feed.domain.GetActivityFeedUseCase
import feature.feed.domain.RecordStreakEventUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val feedModule = DI.Module("feed") {
    bind<CalculateStreakUseCase>() with singleton {
        CalculateStreakUseCase(
            habitDao = instance(),
            dailyDao = instance(),
            trackerDao = instance()
        )
    }

    bind<GetActivityFeedUseCase>() with singleton {
        GetActivityFeedUseCase(
            activityFeedDao = instance()
        )
    }

    bind<RecordStreakEventUseCase>() with singleton {
        RecordStreakEventUseCase(
            habitDao = instance(),
            activityFeedDao = instance(),
            calculateStreakUseCase = instance()
        )
    }

    bind<DetectBrokenStreaksUseCase>() with singleton {
        DetectBrokenStreaksUseCase(
            habitDao = instance(),
            dailyDao = instance(),
            trackerDao = instance(),
            activityFeedDao = instance(),
            calculateStreakUseCase = instance()
        )
    }
}
