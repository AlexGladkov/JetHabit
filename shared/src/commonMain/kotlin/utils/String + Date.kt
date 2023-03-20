package utils

import kotlinx.datetime.Instant

fun String.parseToDate(): Instant {
    val splitted = this.split(".")
    val monthNumber = if (splitted[1].toInt() < 10) "0${splitted[1]}" else splitted[1]
    val dayNumber = if (splitted[2].toInt() < 10) "0${splitted[2]}" else splitted[2]
    return Instant.parse("${splitted[0]}-$monthNumber-${dayNumber}T00:00:00Z")
}