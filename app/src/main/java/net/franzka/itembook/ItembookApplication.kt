package net.franzka.itembook

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.franzka.itembook.room.*

class ItembookApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppRoomDatabase.getDatabase(this, applicationScope)}

    val spaceRepository by lazy { SpaceRepository(database.spaceDao()) }

    val itemRepository by lazy { ItemRepository(database.itemDao(), database.tagDao()) }

    val tagRepository by lazy { TagRepository(database.tagDao())}

    val searchRepository by lazy { SearchRepository(database.searchDao())}

}