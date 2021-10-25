package ru.alexgladkov.jetpackcomposedemo

import org.junit.Test
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyDao
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyEntity
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.DailyRepository
import ru.alexgladkov.jetpackcomposedemo.data.features.daily.models.DailyHabitContainer

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

    private val dailyRepository = DailyRepository(dailyDao = DailyDaoMock())

    @Test
    fun testDataCompression() {
        val input = listOf(
            DailyHabitContainer(habbitId = 1, value = true),
            DailyHabitContainer(habbitId = 2, value = false)
        )

        val expectedResult = "[{\"habbitId\":1,\"value\":true},{\"habbitId\":2,\"value\":false}]"
        val testResult = dailyRepository.compressHabitsWithValues(input)

        assert(expectedResult == testResult)
    }

    @Test
    fun testDataDecompression() {
        val input = "[{ \"habbitId\": 1, \"value\": true }, { \"habbitId\": 2, \"value\": false }]"
        val result = dailyRepository.decompressHabitsWithValues(input)

        assert(result[0].habbitId == 1L)
        assert(result[0].value)
        assert(result[1].habbitId == 2L)
        assert(!result[1].value)
    }

    @Test
    fun testEndToEndCompression() {
        val input = listOf(
            DailyHabitContainer(habbitId = 1, value = true),
            DailyHabitContainer(habbitId = 2, value = false)
        )

        val compressed = dailyRepository.compressHabitsWithValues(input)
        val decompressed = dailyRepository.decompressHabitsWithValues(compressed)

        assert(decompressed[0].habbitId == 1L)
        assert(decompressed[0].value)
        assert(decompressed[1].habbitId == 2L)
        assert(!decompressed[1].value)
    }
}