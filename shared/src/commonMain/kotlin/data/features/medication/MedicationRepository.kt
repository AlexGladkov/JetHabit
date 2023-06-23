package data.features.medication

import Database
import com.soywiz.klock.DateTime
import com.soywiz.klock.weeks
import data.MedicationEntity
import tech.mobiledeveloper.shared.AppRes
import tech.mobiledeveloper.shared.strings.AppResStrings

class MedicationRepository(private val database: Database) {

    suspend fun createNewMedication(
        title: String, weekCount: Int, frequency: List<Boolean>,
        periodicity: List<Boolean>, startDate: DateTime?
    ) {
        val periodicityStringBuilder = StringBuilder()
        periodicity.forEachIndexed { index, i ->
            if (i) {
                periodicityStringBuilder.append(
                    when (index) {
                        0 -> AppRes.string.days_monday_short
                        1 -> AppRes.string.days_tuesday_short
                        2 -> AppRes.string.days_wednesday_short
                        3 -> AppRes.string.days_thursday_short
                        4 -> AppRes.string.days_friday_short
                        5 -> AppRes.string.days_saturday_short
                        6 -> AppRes.string.days_sunday_short
                        else -> throw IllegalStateException()
                    }
                )
            }
        }
        val frequencyStringBuilder = StringBuilder()

        frequency.forEachIndexed { index, i ->
            if (i) {
                frequencyStringBuilder.append(
                    when (index) {
                        0 -> AppRes.string.times_of_day_morning
                        1 -> AppRes.string.times_of_day_afternoon
                        2 -> AppRes.string.times_of_day_evening
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
                frequency = frequencyStringBuilder.toString(),
                periodicity = periodicityStringBuilder.toString(),
                itemId = null
            )
        } else {
            val endDateTime = startDate.plus(weekCount.weeks)

            database.medicineQueries.insert(
                title = title,
                startDate = startDate.toString("yyyy-MM-dd"),
                endDate = endDateTime.toString("yyyy-MM-dd"),
                frequency = frequencyStringBuilder.toString(),
                periodicity = periodicityStringBuilder.toString(),
                itemId = null
            )
        }
    }

    suspend fun fetchCurrentMedications(): List<MedicationEntity> {
        val values = database.medicineQueries.selectAll().executeAsList()
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
            database.medicineQueries.updateDate(
                startDate.format("yyyy-MM-dd"),
                endDate.format("yyyy-MM-dd"),
                itemId
            )
        }
    }
}