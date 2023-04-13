package utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz

fun getValueOrNull(value: String?): DateTimeTz? {
    return if (value == null) {
        null
    } else {
        DateTime.fromString(value)
    }
}