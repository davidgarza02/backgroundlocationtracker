package com.example.backgroundlocationtracker.util

//Extension function that returns a pair with start and end of substring found
fun String.findFirstLastIndex(substring: String): Pair<Int, Int> =
    Pair(indexOf(substring), substring.length)