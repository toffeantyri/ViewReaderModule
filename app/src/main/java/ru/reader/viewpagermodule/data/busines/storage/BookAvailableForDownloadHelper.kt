package ru.reader.viewpagermodule.data.busines.storage

import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.models.BookCardData


class BookAvailableForDownloadHelper {

    private var listBook: ArrayList<BookCardData>

    private val books: List<List<String>> = listOf(
        APP_CONTEXT.resources.getStringArray(R.array.array_url_bhagavad_gita).toList(),
        APP_CONTEXT.resources.getStringArray(R.array.array_url_bhagavad_gita2).toList(),
        APP_CONTEXT.resources.getStringArray(R.array.array_url_bhagavad_gita3).toList(),
    )

    init {
        listBook = createListEmptyBooks()
    }

    private fun getEmptyBookCardData(name: String, listUrl: List<String>) = BookCardData(
        tagName = name,
    ).apply { isLoading = false
        author = "-"
        nameBook = name
        imageValue = ""
        fileFullPath = ""
        urlForLoad = listUrl
    }

    private fun createListEmptyBooks(): ArrayList<BookCardData> {
        return arrayListOf<BookCardData>().apply {
            for (i in books.indices) {
                add(
                    getEmptyBookCardData(
                        books[i][0],
                        books[i].drop(1)
                    )
                )
            }
        }
    }

    fun getListAvailableBooksForDownloads() = listBook
}

