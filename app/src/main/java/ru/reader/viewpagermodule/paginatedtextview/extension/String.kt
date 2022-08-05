package ru.reader.viewpagermodule.paginatedtextview.extension

/**
 * ru.mamykin.widget:paginatedtextview:0.1.1
 * */

fun CharSequence.allWordsPositions(): List<Int> {
    val indexes = mutableListOf<Int>()
    for (i in 0 until this.length) {
        if (this[i] == ' ' || this[i] == '\n') {
            indexes.add(i)
        }
    }
    return indexes
}