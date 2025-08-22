package com.potatosheep.kite.core.media.util

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

fun InputStream.readAllLines(): List<String> {
    val result: MutableList<String> = mutableListOf()
    val bufferedReader = BufferedReader(InputStreamReader(this))

    var line: String? = bufferedReader.readLine()

    while (line != null) {
        result.add(line)
        line = bufferedReader.readLine()
    }

    bufferedReader.close()

    return result
}
