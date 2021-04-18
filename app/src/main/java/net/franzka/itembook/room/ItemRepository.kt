package net.franzka.itembook.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import net.franzka.itembook.models.Item
import net.franzka.itembook.models.ItemWithTags
import net.franzka.itembook.models.Tag

class ItemRepository(private val itemDao: ItemDao, private val tagDao: TagDao) {

    companion object {
        private const val TAG = "ItemRepository"
    }

    val allItems: Flow<List<Item>> = itemDao.getAll()

    suspend fun loadItemWithTags(itemId: Long): ItemWithTags {
        return itemDao.loadItemWithTags(itemId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Item, tags: List<String>) = withContext(Dispatchers.IO) {
        val id = itemDao.insert(item)
        tags.forEach { tagDao.insert(Tag(null, id, it)) }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(item: Item, tags: List<String>)  = withContext(Dispatchers.IO) {
        itemDao.update(item)
        item.itemId?.let { id ->
            tagDao.deleteItemTags(id)
            tags.forEach { tagName -> tagDao.insert(Tag(null, id, tagName)) }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

}