package io.github.dockyardmc.sentinel.common.utils

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getRandomAckString(): String {
    return "${getRandomString(3)}-${getRandomString(3)}"
}