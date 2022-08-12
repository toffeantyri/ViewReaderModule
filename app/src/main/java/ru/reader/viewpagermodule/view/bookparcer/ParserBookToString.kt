package ru.reader.viewpagermodule.view.bookparcer

import android.util.Log
import com.kursx.parser.fb2.Epigraph
import com.kursx.parser.fb2.FictionBook
import com.kursx.parser.fb2.Person
import com.kursx.parser.fb2.Section
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.util.showToast
import java.io.File

class ParserBookToString {

    private lateinit var fb2: FictionBook

    fun getText(pathFile: String): CharSequence {
        Log.d("MyLog", "getText internal path $pathFile")
        kotlin.runCatching {
            val file = File(pathFile)
            fb2 = FictionBook(file)
        }.onSuccess {
            showToast("Загружено успешно")
            return bookStringBuilder()
        }.onFailure {
            showToast("Error open file : ${it.message}")
        }
        return ""
    }


    private fun bookStringBuilder(): String {
        val string = StringBuilder()
        if (fb2.body != null) {
            string.append(getAuthors())
            string.append(getEpigraph())
            string.append(getAllChaptersText())
            Log.d("MyLog", "bookString Builder : $string")
        } else {
            return APP_CONTEXT.getString(R.string.book_is_empty)
        }
        return string.toString()
    }

    private fun getEpigraph(): String {
        val epigraph = fb2.body.epigraphs?.let {
            return it.getEpigraphsFromListPerson()
        }
        return ""
    }

    private fun getAuthors(): String {
        val string = StringBuilder()
        var authors = fb2.description.titleInfo.authors
        string.append(authors.getAuthorsFromListPerson())

        if (string.isEmpty()) {
            authors = fb2.description.documentInfo.authors
            string.append(authors.getAuthorsFromListPerson())
        }
        if (string.isEmpty()) {
            authors = fb2.description.srcTitleInfo.authors
            string.append(authors.getAuthorsFromListPerson())
        }
        if (string.isEmpty()) {
            string.append(APP_CONTEXT.getString(R.string.unknown_author_book))
        }
        string.append("\n")
        return string.toString()
    }

    private fun getAllChaptersText(): String {
        val string = StringBuilder()
        val sections: ArrayList<Section>? = fb2.body.sections
        if (sections != null) {
            Log.d("MyLog", "section1 size = ${sections.size}")
            for (index in sections.indices) {
                //------------------------------------------
                string.append("\n \n")
                val titles = sections[index].titles
                for (titleInd in titles.indices) {
                    val paragraphs = titles[titleInd].paragraphs
                    for (paragInd in paragraphs.indices) {
                        string.append("\n \n")
                        Log.d("MyLog", "Parag text : ${paragraphs[paragInd].text}")
                        string.append(paragraphs[paragInd].text)
                        string.append("\n \n")
                    }
                }
                string.append("\n")
                Log.d("MyLog", "section1 elems size = ${sections[index].elements.size}")
                for (element in sections[index].elements) {
                    element.text?.let {
                        string.append(element.text)
                        string.append("\n")
                    }
                }
                //------------------------------------------
                Log.d("MyLog", "section2 sizein = ${sections[index].sections.size}") //28
                for (innerSection in sections[index].sections.indices) {
                    string.append("\n \n")
                    val innerTitles = sections[index].sections[innerSection].titles
                    for (titleInd in innerTitles.indices) {
                        val paragraphs = innerTitles[titleInd].paragraphs
                        for (paragInd in paragraphs.indices) {
                            Log.d("MyLog", "Parag text : ${paragraphs[paragInd].text}")
                            string.append("\n \n")
                            string.append(paragraphs[paragInd].text)
                            string.append("\n \n")
                        }
                    }


                    Log.d("MyLog", "section2 elems size = ${sections[index].sections[innerSection].elements.size}")
                    for (element in sections[index].sections[innerSection].elements) {
                        element.text?.let {
                            string.append("\t\t")
                            string.append(element.text)
                            string.append("\n")
                        }
                    }

                }
            }
        }
        return string.toString()
    }


    private fun List<Person>.getAuthorsFromListPerson(): String {
        val string = StringBuilder()
        for (index in this.indices) {
            val name = this[index].fullName ?: ""
            string.append(name)
            if (index < this.size - 1 && name.isNotEmpty()) {
                string.append(", ")
            }
        }
        return string.toString()
    }

    private fun List<Epigraph>.getEpigraphsFromListPerson(): String {
        val string = StringBuilder()
        for (index in this.indices) {
            val epigraph = this[index].elements?.get(0)?.text ?: ""
            string.append(epigraph)
            string.append("\n")
        }
        return string.toString()
    }
}