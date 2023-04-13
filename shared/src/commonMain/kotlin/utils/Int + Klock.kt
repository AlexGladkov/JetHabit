package utils

fun Int.wrap(): String = if (this < 10) "0$this" else this.toString()