package data.features.medication

import Database
import com.soywiz.klock.DateTime
import com.soywiz.klock.weeks
import data.MedicationEntity

class MedicationRepository(private val database: Database) {

    suspend fun createNewMedication(
        title: String, weekCount: Int, frequency: Int,
        periodicity: List<Int>, startDate: DateTime?
    ) {
        val stringBuilder = StringBuilder()
        periodicity.forEachIndexed { index, i ->
            if (i == 1) {
                stringBuilder.append(
                    when (index) {
                        0 -> "Mon"
                        1 -> "Tue"
                        2 -> "Wed"
                        3 -> "Thu"
                        4 -> "Fri"
                        5 -> "Sat"
                        6 -> "Sun"
                        else -> throw IllegalStateException()
                    }
                )
            }
        }

        if (startDate == null) {
            database.medicineQueries.insert(
                title = title,
                startDate = null,
                endDate = null,
                frequency = frequency.toLong(),
                periodicity = stringBuilder.toString(),
                itemId = null
            )
        } else {
            val endDateTime = startDate.plus(weekCount.weeks)

            database.medicineQueries.insert(
                title = title,
                startDate = startDate.toString("yyyy-MM-dd"),
                endDate = endDateTime.toString("yyyy-MM-dd"),
                frequency = frequency.toLong(),
                periodicity = stringBuilder.toString(),
                itemId = null
            )
        }
    }

    suspend fun fetchCurrentMedications(): List<MedicationEntity> {
        return database.medicineQueries
            .selectAll()
            .executeAsList()
            .filter { it.startDate != null && it.endDate != null }
            .filter {
                val today = DateTime.now()
                val startDateTime = DateTime.fromString(it.startDate!!)
                val endDateTime = DateTime.fromString(it.endDate!!)
                val startDateCompare = today.compareTo(startDateTime.local)
                val endDateCompare = today.compareTo(endDateTime.local)

                startDateCompare == 1 && endDateCompare == -1
            }
    }

    suspend fun deleteItem(itemId: Long) {

    }

    suspend fun updateMedication(itemId: Long, startDate: DateTime, endDate: DateTime) {
        database.medicineQueries.transaction {
            database.medicineQueries.updateDate(startDate.format("yyyy-MM-dd"), endDate.format("yyyy-MM-dd"), itemId)
        }
    }
}