package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AutoBrowserDatabase
import com.example.data.BrowserRepository
import com.example.data.model.BookmarkEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrowserDaoTest {

    private lateinit var db: AutoBrowserDatabase
    private lateinit var repository: BrowserRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AutoBrowserDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BrowserRepository(db.browserDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveBookmark() = runBlocking {
        repository.addBookmark("YouTube", "https://www.youtube.com", "Video & Media", "video")
        val bookmarks = repository.bookmarks.first()
        assertEquals(1, bookmarks.size)
        assertEquals("YouTube", bookmarks[0].title)
        assertEquals("https://www.youtube.com", bookmarks[0].url)
    }

    @Test
    fun testAutofillSnippetInsertion() = runBlocking {
        repository.addAutofillSnippet("Quick Gas", "ปั๊มน้ำมันใกล้ฉัน", "Search")
        val snippets = repository.autofillSnippets.first()
        assertEquals(1, snippets.size)
        assertEquals("Quick Gas", snippets[0].label)
        assertEquals("ปั๊มน้ำมันใกล้ฉัน", snippets[0].value)
    }
}
