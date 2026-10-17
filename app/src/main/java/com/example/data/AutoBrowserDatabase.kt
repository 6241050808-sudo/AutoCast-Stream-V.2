package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BrowserDao
import com.example.data.model.AutofillSnippetEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [BookmarkEntity::class, AutofillSnippetEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AutoBrowserDatabase : RoomDatabase() {
    abstract fun browserDao(): BrowserDao

    companion object {
        @Volatile
        private var INSTANCE: AutoBrowserDatabase? = null

        fun getDatabase(context: Context): AutoBrowserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoBrowserDatabase::class.java,
                    "auto_browser_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getDatabase(context).browserDao())
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(dao: BrowserDao) {
            val initialBookmarks = listOf(
                BookmarkEntity(
                    title = "YouTube",
                    url = "https://www.youtube.com",
                    category = "Video & Media",
                    iconKey = "video"
                ),
                BookmarkEntity(
                    title = "Twitch",
                    url = "https://www.twitch.tv",
                    category = "Video & Media",
                    iconKey = "live"
                ),
                BookmarkEntity(
                    title = "Netflix",
                    url = "https://www.netflix.com",
                    category = "Video & Media",
                    iconKey = "movie"
                ),
                BookmarkEntity(
                    title = "Google Maps",
                    url = "https://maps.google.com",
                    category = "Navigation",
                    iconKey = "map"
                ),
                BookmarkEntity(
                    title = "Windy Weather",
                    url = "https://www.windy.com",
                    category = "Navigation",
                    iconKey = "weather"
                ),
                BookmarkEntity(
                    title = "Spotify Web",
                    url = "https://open.spotify.com",
                    category = "Audio",
                    iconKey = "music"
                ),
                BookmarkEntity(
                    title = "Thai PBS Live",
                    url = "https://www.thaipbs.or.th/live",
                    category = "News",
                    iconKey = "news"
                ),
                BookmarkEntity(
                    title = "Fast.com Speedtest",
                    url = "https://fast.com",
                    category = "Utility",
                    iconKey = "speed"
                )
            )
            dao.insertBookmarks(initialBookmarks)

            val initialSnippets = listOf(
                AutofillSnippetEntity(
                    label = "ค้นหา: ปั๊มน้ำมันใกล้ฉัน",
                    value = "ปั๊มน้ำมันใกล้ฉัน",
                    category = "Search"
                ),
                AutofillSnippetEntity(
                    label = "ค้นหา: จุดชาร์จรถยนต์ไฟฟ้า EV",
                    value = "จุดชาร์จรถยนต์ไฟฟ้า EV Station",
                    category = "Search"
                ),
                AutofillSnippetEntity(
                    label = "ค้นหา: ร้านอาหารอร่อยใกล้ฉัน",
                    value = "ร้านอาหารใกล้ฉัน",
                    category = "Search"
                ),
                AutofillSnippetEntity(
                    label = "ปลายทาง: ท่าอากาศยานสุวรรณภูมิ (BKK)",
                    value = "ท่าอากาศยานสุวรรณภูมิ",
                    category = "Location"
                ),
                AutofillSnippetEntity(
                    label = "ความละเอียดวิดีโอ 1080p Full HD",
                    value = "1080p",
                    category = "Quality"
                )
            )
            dao.insertAutofillSnippets(initialSnippets)
        }
    }
}
