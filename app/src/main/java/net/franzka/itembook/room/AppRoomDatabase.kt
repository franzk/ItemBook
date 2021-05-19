package net.franzka.itembook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import net.franzka.itembook.models.*

@Database(entities = [Space::class, Item::class, Tag::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun spaceDao(): SpaceDao

    abstract fun itemDao(): ItemDao

    abstract fun tagDao(): TagDao

    abstract fun searchDao(): SearchDao

    companion object {

        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope
                        ): AppRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "itembook_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}