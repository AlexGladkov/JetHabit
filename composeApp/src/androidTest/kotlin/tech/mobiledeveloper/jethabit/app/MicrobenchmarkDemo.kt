package tech.mobiledeveloper.jethabit.app

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import core.database.AppDatabase
import core.database.getDatabaseBuilder
import core.database.getRoomDatabase
import core.platform.AndroidImagePicker
import feature.habits.data.HabitDao
import feature.habits.data.HabitEntity
import feature.habits.data.HabitType
import feature.habits.data.Measurement
import kotlinx.uuid.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MicrobenchmarkDemo {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

//    private lateinit var habitDao: HabitDao
//    private lateinit var database: AppDatabase
//    private lateinit var context: Context
//    private lateinit var allHabits: List<HabitEntity>

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule =
        if(Build.VERSION.SDK_INT > 32) {
            GrantPermissionRule.grant(
                "android.permission.READ_MEDIA_IMAGES",
                "android.permission.RECORD_AUDIO",
                "android.permission.READ_MEDIA_AUDIO",
                "android.permission.READ_MEDIA_VIDEO",
            )
        } else {
            GrantPermissionRule.grant(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
        }

    @Before
    fun setup() {
//        context = ApplicationProvider.getApplicationContext()
//        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
//            .allowMainThreadQueries() // Benchmark-тесты работают в main thread
//            .build()
//
//        habitDao = database.getHabitDao()
//        allHabits = List(1000) { index ->
//            val habit = HabitEntity(
//                id = UUID().toString(),
//                title = "Demo Habit",
//                isGood = true,
//                startDate = "14.06.1989",
//                endDate = "14.07.1989",
//                daysToCheck = "",
//                type = HabitType.REGULAR,
//                measurement = Measurement.KILOGRAMS
//            )
//            habit
//        }
    }

    @Test
    fun benchmarkInsertHabit() = benchmarkRule.measureRepeated {
//        val filtered = allHabits.filter { it.title.contains("Demo") }
//        require(filtered.isNotEmpty()) // Убеждаемся, что фильтрация сработала
    }
}