package com.example.data

import com.example.data.dao.BrowserDao
import com.example.data.model.AutofillSnippetEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val dao: BrowserDao) {

    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val autofillSnippets: Flow<List<AutofillSnippetEntity>> = dao.getAllAutofillSnippets()
    val recentHistory: Flow<List<HistoryEntity>> = dao.getRecentHistory()

    suspend fun addBookmark(title: String, url: String, category: String = "Favorites", iconKey: String = "star"): Long {
        return dao.insertBookmark(
            BookmarkEntity(
                title = title.ifBlank { "Untitled" },
                url = url,
                category = category,
                iconKey = iconKey
            )
        )
    }

    suspend fun deleteBookmark(bookmark: BookmarkEntity) {
        dao.deleteBookmark(bookmark)
    }

    suspend fun addAutofillSnippet(label: String, value: String, category: String = "Custom"): Long {
        return dao.insertAutofillSnippet(
            AutofillSnippetEntity(
                label = label.ifBlank { "Snippet" },
                value = value,
                category = category
            )
        )
    }

    suspend fun deleteAutofillSnippet(snippet: AutofillSnippetEntity) {
        dao.deleteAutofillSnippet(snippet)
    }

    suspend fun addHistory(title: String, url: String) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            dao.insertHistory(
                HistoryEntity(
                    title = title.ifBlank { url },
                    url = url
                )
            )
        }
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }

    suspend fun resetDefaults() {
        AutoBrowserDatabase.populateInitialData(dao)
    }
}
