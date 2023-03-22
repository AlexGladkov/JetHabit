package data.features.medication

import Database
import com.soywiz.klock.DateTime
import com.soywiz.klock.weeks

class MedicationRepository(private val database: Database) {

    suspend fun createNewMedication(
        title: String, weekCount: Int, frequency: Int,
        periodicity: List<Int>, startDate: String?
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
            val startDateTime = DateTime.fromString(startDate)
            val endDateTime = startDateTime.plus(weekCount.weeks)

            database.medicineQueries.insert(
                title = title,
                startDate = startDateTime.toString("dd.MM.yyyy"),
                endDate = endDateTime.toString("dd.MM.yyyy"),
                frequency = frequency.toLong(),
                periodicity = stringBuilder.toString(),
                itemId = null
            )
        }
    }
}