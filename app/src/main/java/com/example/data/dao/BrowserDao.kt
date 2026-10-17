package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AutofillSnippetEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY id ASC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<BookmarkEntity>)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    // Autofill Snippets
    @Query("SELECT * FROM autofill_snippets ORDER BY id ASC")
    fun getAllAutofillSnippets(): Flow<List<AutofillSnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutofillSnippet(snippet: AutofillSnippetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutofillSnippets(snippets: List<AutofillSnippetEntity>)

    @Update
    suspend fun updateAutofillSnippet(snippet: AutofillSnippetEntity)

    @Delete
    suspend fun deleteAutofillSnippet(snippet: AutofillSnippetEntity)

    // History
    @Query("SELECT * FROM history ORDER BY visitedAt DESC LIMIT 100")
    fun getRecentHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}
