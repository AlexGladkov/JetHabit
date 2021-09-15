package ru.alexgladkov.jetpackcomposedemo

import org.junit.Before
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyDao
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyRepository

class DailyDaoMock : DailyDao {

    override suspend fun insert(entity: DailyEntity) {

    }

    override suspend fun update(entity: DailyEntity) {

    }

    override suspend fun getAll(): List<DailyEntity> {
        return emptyList()
    }

}

class DailyRepositoryTests {

    private val dailyDaoMock = DailyDaoMock()
    val dailyRepository = DailyRepository(dailyDao = dailyDaoMock)

    fun testDataCompression() {
        dailyRepository
    }

    fun testDataDecompression() {

    }
}