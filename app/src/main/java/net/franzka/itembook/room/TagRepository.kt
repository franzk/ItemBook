package net.franzka.itembook.room

import kotlinx.coroutines.flow.Flow
import net.franzka.itembook.models.Tag


class TagRepository(private val tagDao: TagDao) {

    val allTags: Flow<List<String>> = tagDao.getAllTags()

    fun getItemTags(itemId: Long): Flow<List<Tag>> {
        return tagDao.getItemTags(itemId)
    }

}
